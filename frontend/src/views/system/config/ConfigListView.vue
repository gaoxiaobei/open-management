<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>参数配置</span>
          <el-button type="primary" @click="openDialog()">新增参数</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="query" @submit.prevent="loadData">
        <el-form-item label="参数键">
          <el-input v-model="query.configKey" placeholder="请输入参数键" clearable />
        </el-form-item>
        <el-form-item label="参数名">
          <el-input v-model="query.configName" placeholder="请输入参数名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="configName" label="参数名称" min-width="160" />
        <el-table-column prop="configKey" label="参数键" min-width="180" />
        <el-table-column prop="configValue" label="参数值" min-width="220" show-overflow-tooltip />
        <el-table-column prop="configType" label="参数类型" min-width="100" />
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑参数' : '新增参数'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="form.configName" />
        </el-form-item>
        <el-form-item label="参数键" prop="configKey">
          <el-input v-model="form.configKey" :disabled="!!editingId" />
        </el-form-item>
        <el-form-item label="参数值">
          <el-input v-model="form.configValue" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="参数类型">
          <el-select v-model="form.configType" clearable style="width: 100%">
            <el-option label="系统" value="SYSTEM" />
            <el-option label="业务" value="BUSINESS" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
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
  createConfig,
  deleteConfig,
  pageConfigs,
  updateConfig,
  type ConfigVO,
} from '@/api/system'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const tableData = ref<ConfigVO[]>([])
const total = ref(0)
const editingId = ref<number>()
const formRef = ref<FormInstance>()

const query = reactive({
  pageNum: 1,
  pageSize: 20,
  configKey: '',
  configName: '',
})

const form = reactive<ConfigVO>({
  configName: '',
  configKey: '',
  configValue: '',
  configType: 'SYSTEM',
  remark: '',
})

const rules: FormRules<ConfigVO> = {
  configName: [{ required: true, message: '请输入参数名称', trigger: 'blur' }],
  configKey: [{ required: true, message: '请输入参数键', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const result = await pageConfigs(query)
    tableData.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.configKey = ''
  query.configName = ''
  query.pageNum = 1
  loadData()
}

function resetForm() {
  editingId.value = undefined
  form.configName = ''
  form.configKey = ''
  form.configValue = ''
  form.configType = 'SYSTEM'
  form.remark = ''
  formRef.value?.clearValidate()
}

function openDialog(row?: ConfigVO) {
  resetForm()
  if (row?.id) {
    editingId.value = row.id
    form.configName = row.configName
    form.configKey = row.configKey
    form.configValue = row.configValue || ''
    form.configType = row.configType || ''
    form.remark = row.remark || ''
  }
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (editingId.value) {
      await updateConfig(editingId.value, form)
      ElMessage.success('参数已更新')
    } else {
      await createConfig(form)
      ElMessage.success('参数已创建')
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
  await ElMessageBox.confirm('删除参数后无法恢复，确定继续吗？', '提示', { type: 'warning' })
  await deleteConfig(id)
  ElMessage.success('参数已删除')
  await loadData()
}

onMounted(loadData)
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
