<template>
  <div class="page-container">
    <el-card>
      <template #header><span>我的待办</span></template>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="taskName" label="任务名称" min-width="180" />
        <el-table-column prop="processInstanceId" label="流程实例ID" width="120" />
        <el-table-column label="处理人" width="120">
          <template #default="{ row }">
            {{ row.assigneeName || row.assigneeId || '待领取' }}
          </template>
        </el-table-column>
        <el-table-column label="领取时间" min-width="180">
          <template #default="{ row }">
            {{ formatTime(row.claimTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            {{ statusMap[row.status] || row.status }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openActionDialog(row, 'APPROVE')">通过</el-button>
            <el-button size="small" type="danger" @click="openActionDialog(row, 'REJECT')">驳回</el-button>
            <el-button size="small" @click="openActionDialog(row, 'TRANSFER')">转办</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form :model="actionForm" label-width="90px">
        <el-form-item label="审批意见">
          <el-input v-model="actionForm.comment" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item v-if="actionForm.action === 'TRANSFER'" label="转办对象">
          <el-input-number v-model="actionForm.nextAssigneeId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitAction">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { completeTask, listPendingTasks, type WorkflowTaskVO } from '@/api/workflow'

function formatTime(time?: string) {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'
}

const statusMap: Record<string, string> = {
  PENDING: '待处理',
  IN_PROGRESS: '处理中',
  COMPLETED: '已完成',
  REJECTED: '已驳回',
}

type WorkflowAction = 'APPROVE' | 'REJECT' | 'TRANSFER'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const currentTask = ref<WorkflowTaskVO>()
const tableData = ref<WorkflowTaskVO[]>([])

const actionForm = reactive({
  action: 'APPROVE' as WorkflowAction,
  comment: '',
  nextAssigneeId: undefined as number | undefined,
})

const dialogTitle = computed(() => {
  if (actionForm.action === 'REJECT') {
    return '驳回待办'
  }
  if (actionForm.action === 'TRANSFER') {
    return '转办待办'
  }
  return '通过待办'
})

async function loadData() {
  loading.value = true
  try {
    tableData.value = await listPendingTasks()
  } finally {
    loading.value = false
  }
}

function openActionDialog(task: WorkflowTaskVO, action: WorkflowAction) {
  currentTask.value = task
  actionForm.action = action
  actionForm.comment = ''
  actionForm.nextAssigneeId = undefined
  dialogVisible.value = true
}

async function submitAction() {
  if (!currentTask.value?.id) {
    return
  }
  submitting.value = true
  try {
    await completeTask(currentTask.value.id, {
      action: actionForm.action,
      comment: actionForm.comment,
      nextAssigneeIds: actionForm.nextAssigneeId ? [actionForm.nextAssigneeId] : [],
    })
    ElMessage.success('待办已处理')
    dialogVisible.value = false
    await loadData()
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-container {
  padding: 16px;
}
</style>
