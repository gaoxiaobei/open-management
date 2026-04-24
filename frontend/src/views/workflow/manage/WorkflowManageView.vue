<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col :span="14">
        <el-card>
          <template #header><span>流程定义</span></template>
          <el-table :data="definitions" v-loading="loading" stripe>
            <el-table-column prop="name" label="流程名称" min-width="180" />
            <el-table-column prop="key" label="流程标识" min-width="180" />
            <el-table-column prop="version" label="版本" width="80" />
            <el-table-column prop="resourceName" label="资源文件" min-width="220" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.suspended ? 'warning' : 'success'">
                  {{ row.suspended ? '挂起' : '运行中' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="部署时间" min-width="180">
              <template #default="{ row }">
                {{ formatTime(row.deploymentTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card>
          <template #header><span>发起流程测试</span></template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
            <el-form-item label="流程定义" prop="processKey">
              <el-select v-model="form.processKey" placeholder="请选择流程" style="width: 100%">
                <el-option
                  v-for="item in definitions"
                  :key="item.id"
                  :label="`${item.name} (${item.key})`"
                  :value="item.key"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="业务单号" prop="businessKey">
              <el-input v-model="form.businessKey" placeholder="例如 OA-20260421-0001" />
            </el-form-item>
            <el-form-item label="业务类型">
              <el-input v-model="form.businessType" placeholder="例如 LEAVE_APPLY" />
            </el-form-item>
            <el-form-item label="审批人ID" prop="assigneeId">
              <el-input-number v-model="form.assigneeId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="submitForm">发起流程</el-button>
            </el-form-item>
          </el-form>
          <el-alert
            title="这里提供的是平台底座联调入口，用于验证 Flowable 集成和待办流转。"
            type="info"
            :closable="false"
          />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import {
  listProcessDefinitions,
  startProcess,
  type ProcessDefinitionVO,
} from '@/api/workflow'

function formatTime(time?: string) {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'
}

const loading = ref(false)
const submitting = ref(false)
const definitions = ref<ProcessDefinitionVO[]>([])
const formRef = ref<FormInstance>()

const form = reactive({
  processKey: '',
  businessKey: '',
  businessType: '',
  assigneeId: undefined as number | undefined,
})

const rules: FormRules<typeof form> = {
  processKey: [{ required: true, message: '请选择流程定义', trigger: 'change' }],
  businessKey: [{ required: true, message: '请输入业务单号', trigger: 'blur' }],
  assigneeId: [{ required: true, message: '请输入审批人ID', trigger: 'change' }],
}

async function loadDefinitions() {
  loading.value = true
  try {
    definitions.value = await listProcessDefinitions()
  } finally {
    loading.value = false
  }
}

async function submitForm() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    await startProcess({
      processKey: form.processKey,
      businessKey: form.businessKey,
      businessType: form.businessType,
      variables: {
        assignee: form.assigneeId,
      },
    })
    ElMessage.success('流程已发起')
    form.businessKey = ''
    form.businessType = ''
  } finally {
    submitting.value = false
  }
}

onMounted(loadDefinitions)
</script>

<style scoped>
.page-container {
  padding: 16px;
}
</style>
