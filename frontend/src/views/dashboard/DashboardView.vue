<template>
  <div class="dashboard">
    <el-row :gutter="16" class="dashboard-row">
      <el-col v-for="card in statCards" :key="card.title" :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div>
              <div class="stat-label">{{ card.title }}</div>
              <div class="stat-value">{{ card.value }}</div>
            </div>
            <div class="stat-hint">{{ card.hint }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="dashboard-row">
      <el-col :xs="24" :lg="16">
        <el-card>
          <template #header><span>消息分布</span></template>
          <div ref="chartRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card>
          <template #header><span>快捷入口</span></template>
          <div class="shortcut-list">
            <router-link v-for="shortcut in shortcuts" :key="shortcut.path" :to="shortcut.path" class="shortcut-card">
              <div class="shortcut-title">{{ shortcut.title }}</div>
              <div class="shortcut-desc">{{ shortcut.desc }}</div>
            </router-link>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header><span>最新消息</span></template>
          <el-table :data="recentMessages" size="small">
            <el-table-column prop="title" label="标题" min-width="160" />
            <el-table-column prop="msgType" label="类型" width="90" />
            <el-table-column prop="createdAt" label="时间" min-width="160" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header><span>当前待办</span></template>
          <el-table :data="pendingTasks" size="small">
            <el-table-column prop="taskName" label="任务" min-width="160" />
            <el-table-column prop="assigneeId" label="处理人" width="90" />
            <el-table-column prop="claimTime" label="领取时间" min-width="160" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { pageLoginLogs } from '@/api/audit'
import { getUnreadCount, pageMyMessages, type MessageVO } from '@/api/message'
import { listPendingTasks, type WorkflowTaskVO } from '@/api/workflow'

const chartRef = ref<HTMLDivElement>()
const recentMessages = ref<MessageVO[]>([])
const pendingTasks = ref<WorkflowTaskVO[]>([])
const unreadCount = ref(0)
const totalMessages = ref(0)
const loginTotal = ref(0)
let chart: echarts.ECharts | null = null

const statCards = computed(() => [
  { title: '未读消息', value: unreadCount.value, hint: '顶部消息中心同步展示' },
  { title: '当前待办', value: pendingTasks.value.length, hint: '来源于工作流待办' },
  { title: '消息总量', value: totalMessages.value, hint: '当前用户消息记录' },
  { title: '登录日志', value: loginTotal.value, hint: '审计模块累计条目' },
])

const shortcuts = [
  { title: '消息中心', desc: '查看未读通知和审批待办', path: '/messages' },
  { title: '我的待办', desc: '处理审批任务', path: '/workflow/todo' },
  { title: '流程管理', desc: '查看流程定义并发起联调', path: '/workflow/manage' },
  { title: '系统管理', desc: '维护用户角色和菜单', path: '/system/users' },
]

async function loadDashboard() {
  const [messages, unread, tasks, loginLogs] = await Promise.all([
    pageMyMessages({ pageNum: 1, pageSize: 8 }),
    getUnreadCount(),
    listPendingTasks(),
    pageLoginLogs({ pageNum: 1, pageSize: 1 }),
  ])
  recentMessages.value = messages.records
  totalMessages.value = messages.total
  unreadCount.value = unread
  pendingTasks.value = tasks
  loginTotal.value = loginLogs.total
  await nextTick()
  renderChart()
}

function renderChart() {
  if (!chartRef.value) {
    return
  }
  chart ??= echarts.init(chartRef.value)
  const source = ['TODO', 'NOTICE', 'RESULT'].map((type) => ({
    name: type,
    value: recentMessages.value.filter((item) => item.msgType === type).length,
  }))
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['45%', '72%'],
        avoidLabelOverlap: true,
        label: { formatter: '{b}: {c}' },
        data: source,
      },
    ],
  })
}

function handleResize() {
  chart?.resize()
}

onMounted(async () => {
  await loadDashboard()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
  chart = null
})
</script>

<style scoped>
.dashboard {
  padding: 16px;
}

.dashboard-row {
  margin-bottom: 16px;
}

.stat-card {
  height: 100%;
}

.stat-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-label {
  color: #64748b;
  font-size: 13px;
}

.stat-value {
  margin-top: 10px;
  font-size: 34px;
  font-weight: 700;
  color: #0f172a;
}

.stat-hint {
  color: #94a3b8;
  font-size: 12px;
}

.chart {
  height: 300px;
}

.shortcut-list {
  display: grid;
  gap: 12px;
}

.shortcut-card {
  display: block;
  padding: 14px 16px;
  border: 1px solid #dbe3f0;
  border-radius: 12px;
  text-decoration: none;
  color: inherit;
  transition: border-color 0.2s ease, transform 0.2s ease;
}

.shortcut-card:hover {
  border-color: #1d4ed8;
  transform: translateY(-2px);
}

.shortcut-title {
  font-weight: 600;
  color: #0f172a;
}

.shortcut-desc {
  margin-top: 6px;
  color: #64748b;
  font-size: 13px;
}
</style>
