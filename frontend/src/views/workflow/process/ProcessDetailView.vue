<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="query" @submit.prevent="loadDetail">
        <el-form-item label="业务单号">
          <el-input v-model="query.businessKey" placeholder="请输入业务单号" style="width: 280px" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadDetail">查询流程</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-loading="loading">
      <template #header><span>流程详情</span></template>

      <el-empty v-if="!progress" description="请输入业务单号后查询流程进度" />
      <template v-else>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="流程标识">{{ progress.processKey }}</el-descriptions-item>
          <el-descriptions-item label="业务单号">{{ progress.businessKey }}</el-descriptions-item>
          <el-descriptions-item label="流程状态">{{ progress.status }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ progress.startTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ progress.endTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-timeline class="timeline">
          <el-timeline-item
            v-for="task in progress.taskList"
            :key="String(task.id)"
            :timestamp="String(task.completeTime || task.claimTime || '-')"
            :type="task.status === 'COMPLETED' ? 'primary' : 'warning'"
          >
            <div class="timeline-title">{{ String(task.taskName || '-') }}</div>
            <div class="timeline-content">处理人：{{ String(task.assigneeId || '-') }}</div>
            <div class="timeline-content">状态：{{ String(task.status || '-') }}</div>
            <div class="timeline-content">动作：{{ String(task.action || '-') }}</div>
            <div class="timeline-content">意见：{{ String(task.comment || '-') }}</div>
          </el-timeline-item>
        </el-timeline>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { getProcessProgress, type ProcessProgressVO } from '@/api/workflow'

const loading = ref(false)
const progress = ref<ProcessProgressVO | null>(null)

const query = reactive({
  businessKey: '',
})

async function loadDetail() {
  if (!query.businessKey) {
    progress.value = null
    return
  }
  loading.value = true
  try {
    progress.value = await getProcessProgress(query.businessKey)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-container {
  padding: 16px;
}

.search-card {
  margin-bottom: 16px;
}

.timeline {
  margin-top: 24px;
}

.timeline-title {
  font-weight: 600;
  margin-bottom: 6px;
}

.timeline-content {
  color: #475569;
  line-height: 1.8;
}
</style>
