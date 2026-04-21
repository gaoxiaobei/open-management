<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>岗位管理</span>
          <el-button type="primary" @click="openDialog()">新增岗位</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="query" @submit.prevent="loadData">
        <el-form-item label="岗位名称">
          <el-input v-model="query.positionName" placeholder="请输入岗位名称" clearable />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-select v-model="query.deptId" clearable placeholder="全部部门" style="width: 220px">
            <el-option
              v-for="dept in deptOptions"
              :key="dept.id"
              :label="dept.label"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="positionCode" label="岗位编码" min-width="140" />
        <el-table-column prop="positionName" label="岗位名称" min-width="140" />
        <el-table-column label="所属部门" min-width="160">
          <template #default="{ row }">
            {{ resolveDeptName(row.deptId) || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'warning'">
              {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end"
        @change="loadData"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑岗位' : '新增岗位'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="岗位编码" prop="positionCode">
          <el-input v-model="form.positionCode" />
        </el-form-item>
        <el-form-item label="岗位名称" prop="positionName">
          <el-input v-model="form.positionName" />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-select v-model="form.deptId" clearable style="width: 100%">
            <el-option
              v-for="dept in deptOptions"
              :key="dept.id"
              :label="dept.label"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="ENABLED">启用</el-radio>
            <el-radio value="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createPosition,
  deletePosition,
  getDeptTree,
  pagePositions,
  updatePosition,
  type DeptTreeNode,
  type PositionVO,
} from '@/api/org'

interface DeptOption {
  id: number
  label: string
}

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const tableData = ref<PositionVO[]>([])
const deptOptions = ref<DeptOption[]>([])
const total = ref(0)
const editingId = ref<number>()
const formRef = ref<FormInstance>()

const query = reactive({
  pageNum: 1,
  pageSize: 20,
  deptId: undefined as number | undefined,
  positionName: '',
})

const form = reactive<PositionVO>({
  positionCode: '',
  positionName: '',
  deptId: undefined,
  status: 'ENABLED',
})

const rules: FormRules<PositionVO> = {
  positionCode: [{ required: true, message: '请输入岗位编码', trigger: 'blur' }],
  positionName: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
}

function flattenDeptTree(nodes: DeptTreeNode[], prefix = ''): DeptOption[] {
  return nodes.flatMap((node) => {
    const currentLabel = prefix ? `${prefix} / ${node.deptName}` : node.deptName
    const current = [{ id: node.id, label: currentLabel }]
    const children = node.children?.length ? flattenDeptTree(node.children, currentLabel) : []
    return current.concat(children)
  })
}

function resolveDeptName(deptId?: number) {
  if (!deptId) {
    return ''
  }
  return deptOptions.value.find((item) => item.id === deptId)?.label || ''
}

async function loadData() {
  loading.value = true
  try {
    const result = await pagePositions(query)
    tableData.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

async function loadDeptOptions() {
  deptOptions.value = flattenDeptTree(await getDeptTree())
}

function resetQuery() {
  query.positionName = ''
  query.deptId = undefined
  query.pageNum = 1
  loadData()
}

function resetForm() {
  editingId.value = undefined
  form.positionCode = ''
  form.positionName = ''
  form.deptId = undefined
  form.status = 'ENABLED'
  formRef.value?.clearValidate()
}

function openDialog(row?: PositionVO) {
  resetForm()
  if (row?.id) {
    editingId.value = row.id
    form.positionCode = row.positionCode
    form.positionName = row.positionName
    form.deptId = row.deptId
    form.status = row.status
  }
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (editingId.value) {
      await updatePosition(editingId.value, form)
      ElMessage.success('岗位已更新')
    } else {
      await createPosition(form)
      ElMessage.success('岗位已创建')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id?: number) {
  if (!id) {
    return
  }
  await ElMessageBox.confirm('删除岗位后无法恢复，确定继续吗？', '提示', { type: 'warning' })
  await deletePosition(id)
  ElMessage.success('岗位已删除')
  await loadData()
}

onMounted(async () => {
  await loadDeptOptions()
  await loadData()
})
</script>

<style scoped>
.page-container {
  padding: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
