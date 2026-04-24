<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>员工档案</span>
          <el-button type="primary" @click="openCreateDialog">新增员工</el-button>
        </div>
      </template>
      <el-table :data="employees" v-loading="loading" stripe>
        <el-table-column prop="empNo" label="员工编号" />
        <el-table-column prop="empName" label="姓名" />
        <el-table-column prop="deptName" label="部门" />
        <el-table-column prop="positionName" label="岗位" />
        <el-table-column prop="hireDate" label="入职日期" />
        <el-table-column prop="empStatus" label="状态" />
        <el-table-column prop="mobile" label="手机号" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, sizes, prev, pager, next" @change="loadData" style="margin-top:16px;justify-content:flex-end" />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增员工' : '编辑员工'"
      width="640px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="员工编号" prop="empNo">
              <el-input v-model="form.empNo" :disabled="dialogMode === 'edit'" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="empName">
              <el-input v-model="form.empName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" style="width: 100%" placeholder="请选择">
                <el-option label="男" value="M" />
                <el-option label="女" value="F" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idNo">
              <el-input v-model="form.idNo" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门">
              <el-select v-model="form.deptId" placeholder="请选择部门" clearable style="width: 100%">
                <el-option
                  v-for="dept in deptOptions"
                  :key="dept.id"
                  :label="dept.label"
                  :value="dept.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select v-model="form.positionId" placeholder="请选择岗位" clearable style="width: 100%">
                <el-option
                  v-for="pos in positionOptions"
                  :key="pos.id"
                  :label="pos.label"
                  :value="pos.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期">
              <el-date-picker v-model="form.hireDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.mobile" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createEmployee,
  deleteEmployee,
  getEmployee,
  pageEmployees,
  updateEmployee,
  type EmployeeCreateParams,
  type EmployeeVO,
} from '@/api/hr'
import { getDeptTree, listPositions, type DeptTreeNode, type PositionVO } from '@/api/org'

type DialogMode = 'create' | 'edit'

interface DeptOption {
  id: number
  label: string
}

interface PositionOption {
  id: number
  label: string
}

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const employees = ref<EmployeeVO[]>([])
const total = ref(0)
const deptOptions = ref<DeptOption[]>([])
const positionOptions = ref<PositionOption[]>([])
const formRef = ref<FormInstance>()
const editingId = ref<number>()

const query = reactive({ pageNum: 1, pageSize: 20 })

const form = reactive<EmployeeCreateParams>({
  empNo: '',
  empName: '',
  gender: 'M',
  idNo: '',
  deptId: 0,
  positionId: 0,
  hireDate: '',
  mobile: '',
  email: '',
})

const rules: FormRules<EmployeeCreateParams> = {
  empNo: [{ required: true, message: '请输入员工编号', trigger: 'blur' }],
  empName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  idNo: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  positionId: [{ required: true, message: '请选择岗位', trigger: 'change' }],
}

async function loadData() {
  loading.value = true
  try {
    const result = await pageEmployees(query)
    employees.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

function resetForm() {
  editingId.value = undefined
  form.empNo = ''
  form.empName = ''
  form.gender = 'MALE'
  form.idNo = ''
  form.deptId = 0
  form.positionId = 0
  form.hireDate = ''
  form.mobile = ''
  form.email = ''
  formRef.value?.clearValidate()
}

function flattenDeptTree(nodes: DeptTreeNode[], prefix = ''): DeptOption[] {
  return nodes.flatMap((node) => {
    const currentLabel = prefix ? `${prefix} / ${node.deptName}` : node.deptName
    const current = [{ id: node.id, label: currentLabel }]
    const children = node.children?.length ? flattenDeptTree(node.children, currentLabel) : []
    return current.concat(children)
  })
}

async function bootstrapOptions() {
  const [depts, positions] = await Promise.all([getDeptTree(), listPositions()])
  deptOptions.value = flattenDeptTree(depts)
  positionOptions.value = positions.map((p: PositionVO) => ({
    id: p.id!,
    label: p.positionName,
  }))
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

async function openEditDialog(row: EmployeeVO) {
  dialogMode.value = 'edit'
  resetForm()
  const detail = await getEmployee(row.id)
  editingId.value = row.id
  form.empNo = detail.empNo
  form.empName = detail.empName
  form.gender = detail.gender
  form.idNo = (detail as unknown as Record<string, string>)['idNo'] || ''
  form.deptId = detail.deptId
  form.positionId = detail.positionId
  form.hireDate = detail.hireDate
  form.mobile = detail.mobile || ''
  form.email = detail.email || ''
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'create') {
      await createEmployee(form)
      ElMessage.success('员工已创建')
    } else if (editingId.value) {
      await updateEmployee(editingId.value, form)
      ElMessage.success('员工信息已更新')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('删除后不可恢复，确定继续吗？', '提示', { type: 'warning' })
  await deleteEmployee(id)
  ElMessage.success('员工已删除')
  await loadData()
}

onMounted(async () => {
  await bootstrapOptions()
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
