<template>
  <div class="page-container">
    <!-- 页面标题栏 -->
    <div class="page-title-bar">
      <div>
        <h2 class="page-title">首页总览</h2>
        <p class="page-subtitle">{{ today }} &nbsp;·&nbsp; 欢迎回来，{{ displayName }}</p>
      </div>
      <el-button :icon="Refresh" @click="loadDashboard" :loading="refreshing" size="small">
        刷新数据
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-row">
      <div v-for="card in statCards" :key="card.title" class="stat-card" :class="card.cls">
        <div class="stat-card-icon">
          <el-icon><component :is="card.icon" /></el-icon>
        </div>
        <div class="stat-card-body">
          <div class="stat-card-value">{{ card.value }}</div>
          <div class="stat-card-title">{{ card.title }}</div>
          <div class="stat-card-hint">{{ card.hint }}</div>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="dashboard-main">
      <!-- 左列 -->
      <div class="dashboard-col-main">
        <!-- 消息分布图 -->
        <el-card class="dash-card">
          <template #header>
            <div class="dash-card-header">
              <span class="dash-card-title">消息分布统计</span>
              <span class="dash-card-meta">最近 8 条消息</span>
            </div>
          </template>
          <div ref="chartRef" class="chart-area" />
        </el-card>

        <!-- 最新消息 -->
        <el-card class="dash-card">
          <template #header>
            <div class="dash-card-header">
              <span class="dash-card-title">最新消息</span>
              <router-link to="/messages" class="dash-card-link">查看全部 ›</router-link>
            </div>
          </template>
          <div v-if="recentMessages.length" class="msg-list">
            <div v-for="item in recentMessages" :key="item.id" class="msg-item">
              <div class="msg-type-tag" :class="`msg-type-${item.msgType?.toLowerCase()}`">
                {{ messageTypeMap[item.msgType] || item.msgType }}
              </div>
              <div class="msg-body">
                <div class="msg-title">{{ item.title }}</div>
                <div class="msg-content">{{ item.content || '无附加内容' }}</div>
              </div>
              <div class="msg-time">{{ formatTime(item.createdAt) }}</div>
            </div>
          </div>
          <div v-else class="empty-state">暂无消息记录</div>
        </el-card>
      </div>

      <!-- 右列 -->
      <div class="dashboard-col-side">
        <!-- 快捷入口 -->
        <el-card class="dash-card">
          <template #header>
            <div class="dash-card-header">
              <span class="dash-card-title">快捷入口</span>
            </div>
          </template>
          <div class="shortcut-grid">
            <router-link
              v-for="shortcut in shortcuts"
              :key="shortcut.path"
              :to="shortcut.path"
              class="shortcut-item"
            >
              <div class="shortcut-icon">
                <el-icon><component :is="shortcut.icon" /></el-icon>
              </div>
              <div class="shortcut-label">{{ shortcut.title }}</div>
            </router-link>
          </div>
        </el-card>

        <!-- 当前待办 -->
        <el-card class="dash-card">
          <template #header>
            <div class="dash-card-header">
              <span class="dash-card-title">当前待办</span>
              <router-link to="/workflow/todo" class="dash-card-link">查看全部 ›</router-link>
            </div>
          </template>
          <div v-if="pendingTasks.length" class="todo-list">
            <div v-for="task in pendingTasks" :key="task.id" class="todo-item">
              <div class="todo-dot" />
              <div class="todo-body">
                <div class="todo-name">{{ task.taskName }}</div>
                <div class="todo-meta">
                  处理人：{{ task.assigneeName || task.assigneeId || '待领取' }}
                  &nbsp;·&nbsp;
                  {{ task.claimTime ? formatTime(task.claimTime) : '未领取' }}
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">当前没有待办事项</div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Bell, Box, Connection, DataAnalysis, DocumentChecked, Promotion, Refresh, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { pageLoginLogs } from '@/api/audit'
import { getUnreadCount, pageMyMessages, type MessageVO } from '@/api/message'
import { listPendingTasks, type WorkflowTaskVO } from '@/api/workflow'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const chartRef = ref<HTMLDivElement>()
const recentMessages = ref<MessageVO[]>([])
const pendingTasks = ref<WorkflowTaskVO[]>([])
const unreadCount = ref(0)
const totalMessages = ref(0)
const loginTotal = ref(0)
const refreshing = ref(false)
let chart: echarts.ECharts | null = null

const today = computed(() => dayjs().format('YYYY年MM月DD日'))
const displayName = computed(() => userStore.userInfo?.realName || userStore.userInfo?.username || '用户')

const statCards = computed(() => [
  {
    title: '未读消息',
    value: unreadCount.value,
    hint: '待处理通知',
    icon: Bell,
    cls: 'stat-primary',
  },
  {
    title: '当前待办',
    value: pendingTasks.value.length,
    hint: '需人工处理',
    icon: Connection,
    cls: 'stat-warning',
  },
  {
    title: '消息总量',
    value: totalMessages.value,
    hint: '账户可见记录',
    icon: Promotion,
    cls: 'stat-info',
  },
  {
    title: '登录日志',
    value: loginTotal.value,
    hint: '审计累计条目',
    icon: DocumentChecked,
    cls: 'stat-neutral',
  },
])

const shortcuts = [
  { title: '消息中心', path: '/messages', icon: Bell },
  { title: '我的待办', path: '/workflow/todo', icon: Connection },
  { title: '流程管理', path: '/workflow/manage', icon: DataAnalysis },
  { title: '用户管理', path: '/system/users', icon: User },
  { title: '员工档案', path: '/hr/employees', icon: User },
  { title: '资产管理', path: '/asset', icon: Box },
]

const messageTypeMap: Record<string, string> = {
  TODO: '待办',
  NOTICE: '通知',
  RESULT: '结果',
  ALERT: '告警',
}

function formatTime(time?: string) {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'
}

async function loadDashboard() {
  refreshing.value = true
  const results = await Promise.allSettled([
    pageMyMessages({ pageNum: 1, pageSize: 8 }),
    getUnreadCount(),
    listPendingTasks(),
    pageLoginLogs({ pageNum: 1, pageSize: 1 }),
  ])

  const [messages, unread, tasks, loginLogs] = results

  if (messages.status === 'fulfilled') {
    recentMessages.value = messages.value.records
    totalMessages.value = messages.value.total
  }
  if (unread.status === 'fulfilled') {
    unreadCount.value = unread.value
  }
  if (tasks.status === 'fulfilled') {
    pendingTasks.value = tasks.value
  }
  if (loginLogs.status === 'fulfilled') {
    loginTotal.value = loginLogs.value.total
  }

  if (results.some((r) => r.status === 'rejected')) {
    ElMessage.warning('部分数据加载失败，已展示可用结果')
  }

  refreshing.value = false
  await nextTick()
  renderChart()
}

function renderChart() {
  if (!chartRef.value) return
  chart ??= echarts.init(chartRef.value)
  const source = ['TODO', 'NOTICE', 'RESULT', 'ALERT'].map((type) => ({
    name: messageTypeMap[type] || type,
    value: recentMessages.value.filter((item) => item.msgType === type).length,
  }))
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: {
      bottom: 0,
      icon: 'rect',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: { color: '#4a5568', fontSize: 12 },
    },
    series: [
      {
        type: 'pie',
        radius: ['42%', '66%'],
        center: ['50%', '44%'],
        avoidLabelOverlap: true,
        label: {
          color: '#1a2332',
          fontSize: 12,
          formatter: '{b}: {c}',
        },
        itemStyle: {
          borderRadius: 2,
          borderColor: '#ffffff',
          borderWidth: 2,
        },
        color: ['#1a3a5c', '#2563a8', '#4a7fb5', '#8aabcc'],
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
/* ─── 页面标题栏 ─── */
.page-title-bar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--om-border-light);
}

.page-title {
  font-family: var(--om-font-serif);
  font-size: 22px;
  font-weight: 700;
  color: var(--om-text);
  margin: 0;
  letter-spacing: 0.02em;
}

.page-subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--om-text-muted);
}

/* ─── 统计卡片行 ─── */
.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.stat-card {
  background: var(--om-surface);
  border: 1px solid var(--om-border);
  border-radius: var(--om-radius-md);
  padding: 16px;
  display: flex;
  align-items: flex-start;
  gap: 14px;
  box-shadow: var(--om-shadow-sm);
}

.stat-card-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--om-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.stat-primary .stat-card-icon {
  background: var(--om-primary-pale);
  color: var(--om-primary);
}

.stat-warning .stat-card-icon {
  background: #fef3c7;
  color: var(--om-warning);
}

.stat-info .stat-card-icon {
  background: #dbeafe;
  color: var(--om-info);
}

.stat-neutral .stat-card-icon {
  background: #f1f5f9;
  color: var(--om-text-secondary);
}

.stat-card-body {
  flex: 1;
  min-width: 0;
}

.stat-card-value {
  font-family: var(--om-font-serif);
  font-size: 28px;
  font-weight: 700;
  color: var(--om-text);
  line-height: 1;
}

.stat-card-title {
  font-size: 13px;
  font-weight: 500;
  color: var(--om-text-secondary);
  margin-top: 6px;
}

.stat-card-hint {
  font-size: 12px;
  color: var(--om-text-muted);
  margin-top: 3px;
}

/* ─── 主内容区 ─── */
.dashboard-main {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) minmax(280px, 1fr);
  gap: 12px;
  align-items: start;
}

.dashboard-col-main,
.dashboard-col-side {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* ─── 卡片 ─── */
.dash-card {
  border: 1px solid var(--om-border);
  border-radius: var(--om-radius-md);
  background: var(--om-surface);
  box-shadow: var(--om-shadow-sm);
}

.dash-card :deep(.el-card__header) {
  padding: 12px 16px;
  border-bottom: 1px solid var(--om-border-light);
  background: #fafbfc;
}

.dash-card :deep(.el-card__body) {
  padding: 16px;
}

.dash-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dash-card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--om-text);
}

.dash-card-meta {
  font-size: 12px;
  color: var(--om-text-muted);
}

.dash-card-link {
  font-size: 12px;
  color: var(--om-primary);
  text-decoration: none;
}

.dash-card-link:hover {
  text-decoration: underline;
}

/* ─── 图表 ─── */
.chart-area {
  height: 280px;
}

/* ─── 消息列表 ─── */
.msg-list {
  display: flex;
  flex-direction: column;
}

.msg-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid var(--om-border-light);
}

.msg-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.msg-type-tag {
  flex-shrink: 0;
  padding: 2px 6px;
  font-size: 11px;
  font-weight: 600;
  border-radius: var(--om-radius-sm);
  margin-top: 2px;
}

.msg-type-todo {
  background: var(--om-primary-pale);
  color: var(--om-primary);
}

.msg-type-notice {
  background: #dbeafe;
  color: var(--om-info);
}

.msg-type-result {
  background: #dcfce7;
  color: var(--om-success);
}

.msg-type-alert {
  background: var(--om-accent-pale);
  color: var(--om-accent);
}

.msg-body {
  flex: 1;
  min-width: 0;
}

.msg-title {
  font-size: 13px;
  font-weight: 500;
  color: var(--om-text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.msg-content {
  font-size: 12px;
  color: var(--om-text-muted);
  margin-top: 3px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.msg-time {
  font-size: 11px;
  color: var(--om-text-disabled);
  flex-shrink: 0;
  white-space: nowrap;
}

/* ─── 快捷入口 ─── */
.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 8px;
  border: 1px solid var(--om-border-light);
  border-radius: var(--om-radius-md);
  text-decoration: none;
  color: var(--om-text-secondary);
  transition: all 0.12s;
  background: #fafbfc;
}

.shortcut-item:hover {
  background: var(--om-primary-pale);
  border-color: var(--om-primary);
  color: var(--om-primary);
  text-decoration: none;
}

.shortcut-icon {
  font-size: 20px;
}

.shortcut-label {
  font-size: 12px;
  font-weight: 500;
  text-align: center;
}

/* ─── 待办列表 ─── */
.todo-list {
  display: flex;
  flex-direction: column;
}

.todo-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid var(--om-border-light);
}

.todo-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.todo-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--om-primary);
  flex-shrink: 0;
  margin-top: 6px;
}

.todo-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--om-text);
}

.todo-meta {
  font-size: 12px;
  color: var(--om-text-muted);
  margin-top: 3px;
}

/* ─── 空状态 ─── */
.empty-state {
  padding: 24px 0;
  text-align: center;
  font-size: 13px;
  color: var(--om-text-disabled);
}

/* ─── 响应式 ─── */
@media (max-width: 1200px) {
  .stat-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 900px) {
  .dashboard-main {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .stat-row {
    grid-template-columns: 1fr 1fr;
  }

  .page-title-bar {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
