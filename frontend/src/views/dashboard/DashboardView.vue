<template>
  <div class="dashboard-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <span class="hero-kicker">Operations Snapshot</span>
        <h2>今日运营状态</h2>
        <p>
          从消息提醒、流程待办到登录审计，关键协同状态被压缩在一个总览页里，适合作为进入系统后的第一屏。
        </p>
      </div>

      <div class="hero-focus">
        <div class="focus-label">当前关注项</div>
        <div class="focus-value">{{ unreadCount }}</div>
        <div class="focus-text">条未读消息待处理</div>
        <router-link to="/messages" class="focus-link">前往消息中心</router-link>
      </div>
    </section>

    <section class="stats-grid">
      <article v-for="card in statCards" :key="card.title" class="stat-tile" :class="card.tone">
        <div class="stat-top">
          <component :is="card.icon" class="stat-icon" />
          <span class="stat-label">{{ card.title }}</span>
        </div>
        <div class="stat-value">{{ card.value }}</div>
        <p class="stat-hint">{{ card.hint }}</p>
      </article>
    </section>

    <section class="dashboard-grid">
      <el-card class="chart-card">
        <template #header>
          <div class="panel-heading">
            <div>
              <span class="panel-kicker">Message Map</span>
              <h3>消息分布</h3>
            </div>
            <span class="panel-meta">最近 8 条消息样本</span>
          </div>
        </template>
        <div ref="chartRef" class="chart" />
      </el-card>

      <el-card class="shortcut-card">
        <template #header>
          <div class="panel-heading">
            <div>
              <span class="panel-kicker">Fast Access</span>
              <h3>快捷入口</h3>
            </div>
          </div>
        </template>
        <div class="shortcut-list">
          <router-link v-for="shortcut in shortcuts" :key="shortcut.path" :to="shortcut.path" class="shortcut-item">
            <component :is="shortcut.icon" class="shortcut-icon" />
            <div>
              <div class="shortcut-title">{{ shortcut.title }}</div>
              <div class="shortcut-desc">{{ shortcut.desc }}</div>
            </div>
          </router-link>
        </div>
      </el-card>

      <el-card class="feed-card">
        <template #header>
          <div class="panel-heading">
            <div>
              <span class="panel-kicker">Recent Messages</span>
              <h3>最新消息</h3>
            </div>
          </div>
        </template>
        <div v-if="recentMessages.length" class="feed-list">
          <article v-for="item in recentMessages" :key="item.id" class="feed-item">
            <div class="feed-main">
              <span class="feed-type">{{ messageTypeMap[item.msgType] || item.msgType }}</span>
              <strong>{{ item.title }}</strong>
              <p>{{ item.content || '无附加内容' }}</p>
            </div>
            <time>{{ item.createdAt }}</time>
          </article>
        </div>
        <div v-else class="empty-copy">暂无消息记录。</div>
      </el-card>

      <el-card class="feed-card">
        <template #header>
          <div class="panel-heading">
            <div>
              <span class="panel-kicker">Workflow Queue</span>
              <h3>当前待办</h3>
            </div>
          </div>
        </template>
        <div v-if="pendingTasks.length" class="feed-list">
          <article v-for="task in pendingTasks" :key="task.id" class="feed-item">
            <div class="feed-main">
              <span class="feed-type">TASK</span>
              <strong>{{ task.taskName }}</strong>
              <p>处理人 {{ task.assigneeId || '-' }} · 状态 {{ task.status || '-' }}</p>
            </div>
            <time>{{ task.claimTime || '待领取' }}</time>
          </article>
        </div>
        <div v-else class="empty-copy">当前没有待办事项。</div>
      </el-card>
    </section>
  </div>
</template>

<script setup lang="ts">
import { Bell, Connection, DataAnalysis, DocumentChecked, Promotion } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
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
  { title: '未读消息', value: unreadCount.value, hint: '顶部消息中心与此同步', icon: Bell, tone: 'tone-brand' },
  { title: '当前待办', value: pendingTasks.value.length, hint: '待办列表需要人工动作', icon: Connection, tone: 'tone-earth' },
  { title: '消息总量', value: totalMessages.value, hint: '当前账户可见消息记录', icon: Promotion, tone: 'tone-neutral' },
  { title: '登录日志', value: loginTotal.value, hint: '审计模块累计登录条目', icon: DocumentChecked, tone: 'tone-brand' },
])

const shortcuts = [
  { title: '消息中心', desc: '查看未读通知和审批提醒', path: '/messages', icon: Bell },
  { title: '我的待办', desc: '处理流程任务与转办操作', path: '/workflow/todo', icon: Connection },
  { title: '流程管理', desc: '查看流程定义与节点状态', path: '/workflow/manage', icon: DataAnalysis },
  { title: '系统管理', desc: '维护账户、角色和菜单权限', path: '/system/users', icon: Promotion },
]

const messageTypeMap: Record<string, string> = {
  TODO: '待办',
  NOTICE: '通知',
  RESULT: '结果',
  ALERT: '告警',
}

async function loadDashboard() {
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

  if (results.some((item) => item.status === 'rejected')) {
    ElMessage.warning('部分看板数据加载失败，已展示可用结果')
  }

  await nextTick()
  renderChart()
}

function renderChart() {
  if (!chartRef.value) {
    return
  }
  chart ??= echarts.init(chartRef.value)
  const source = ['TODO', 'NOTICE', 'RESULT', 'ALERT'].map((type) => ({
    name: messageTypeMap[type] || type,
    value: recentMessages.value.filter((item) => item.msgType === type).length,
  }))
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 0,
      icon: 'circle',
      textStyle: { color: '#58636a' },
    },
    series: [
      {
        type: 'pie',
        radius: ['48%', '72%'],
        avoidLabelOverlap: true,
        label: { color: '#182126', formatter: '{b}: {c}' },
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fffaf4',
          borderWidth: 4,
        },
        color: ['#0f5b52', '#c96f3b', '#d9b898', '#8a9ba8'],
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
.dashboard-page {
  padding: clamp(18px, 2vw, 30px);
  display: grid;
  gap: 20px;
}

.hero-panel {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) 280px;
  gap: 24px;
  padding: clamp(24px, 3vw, 36px);
  border-radius: 30px;
  background:
    radial-gradient(circle at top right, rgba(201, 111, 59, 0.22), transparent 28%),
    linear-gradient(135deg, rgba(18, 58, 55, 0.96), rgba(11, 37, 35, 0.96));
  color: rgba(255, 247, 238, 0.92);
  box-shadow: 0 28px 54px rgba(16, 39, 37, 0.22);
}

.hero-kicker,
.panel-kicker,
.focus-label,
.feed-type {
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.hero-kicker {
  font-size: 12px;
  color: rgba(255, 247, 238, 0.58);
}

.hero-copy h2,
.panel-heading h3 {
  margin: 8px 0 0;
  font-family: var(--om-display-font);
  line-height: 1;
}

.hero-copy h2 {
  font-size: clamp(40px, 5vw, 60px);
}

.hero-copy p {
  max-width: 58ch;
  margin: 14px 0 0;
  color: rgba(255, 247, 238, 0.76);
  line-height: 1.8;
}

.hero-focus {
  padding: 24px;
  border-radius: 24px;
  background: rgba(255, 247, 238, 0.1);
  border: 1px solid rgba(255, 247, 238, 0.12);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.focus-label,
.panel-kicker {
  font-size: 11px;
}

.focus-value {
  margin-top: 14px;
  font-family: var(--om-display-font);
  font-size: 72px;
  line-height: 1;
}

.focus-text {
  color: rgba(255, 247, 238, 0.72);
}

.focus-link {
  display: inline-flex;
  align-self: flex-start;
  margin-top: 18px;
  padding: 12px 18px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.92);
  color: #17312f;
  text-decoration: none;
  font-weight: 700;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stat-tile {
  min-height: 180px;
  padding: 22px;
  border-radius: 26px;
  border: 1px solid rgba(24, 33, 38, 0.08);
  box-shadow: 0 18px 34px rgba(42, 34, 24, 0.09);
}

.tone-brand {
  background:
    radial-gradient(circle at top, rgba(15, 91, 82, 0.18), transparent 48%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.9), rgba(247, 242, 234, 0.84));
}

.tone-earth {
  background:
    radial-gradient(circle at top, rgba(201, 111, 59, 0.2), transparent 48%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.9), rgba(248, 239, 230, 0.92));
}

.tone-neutral {
  background:
    radial-gradient(circle at top, rgba(27, 34, 37, 0.08), transparent 44%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(247, 242, 234, 0.88));
}

.stat-top {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  font-size: 18px;
  color: var(--om-brand);
}

.stat-label {
  color: var(--om-ink-soft);
  font-size: 13px;
}

.stat-value {
  margin-top: 22px;
  font-family: var(--om-display-font);
  font-size: 56px;
  line-height: 1;
}

.stat-hint {
  margin: 14px 0 0;
  color: var(--om-ink-soft);
  line-height: 1.7;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) minmax(300px, 0.9fr);
  gap: 20px;
}

.chart-card,
.shortcut-card,
.feed-card {
  border-radius: 26px;
}

.panel-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.panel-heading h3 {
  font-size: 34px;
}

.panel-meta {
  color: var(--om-ink-soft);
  font-size: 13px;
}

.chart {
  height: 320px;
}

.shortcut-list,
.feed-list {
  display: grid;
  gap: 12px;
}

.shortcut-item,
.feed-item {
  border-radius: 18px;
  text-decoration: none;
  color: inherit;
}

.shortcut-item {
  display: grid;
  grid-template-columns: 48px minmax(0, 1fr);
  gap: 14px;
  align-items: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.68);
  border: 1px solid rgba(24, 33, 38, 0.06);
  transition: transform 0.2s ease, border-color 0.2s ease;
}

.shortcut-item:hover {
  transform: translateY(-2px);
  border-color: rgba(15, 91, 82, 0.24);
}

.shortcut-icon {
  width: 48px;
  height: 48px;
  padding: 12px;
  border-radius: 14px;
  background: rgba(15, 91, 82, 0.08);
  color: var(--om-brand);
}

.shortcut-title {
  font-weight: 700;
}

.shortcut-desc {
  margin-top: 4px;
  color: var(--om-ink-soft);
  line-height: 1.6;
}

.feed-card {
  align-self: start;
}

.feed-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 18px;
  background: rgba(255, 255, 255, 0.68);
  border: 1px solid rgba(24, 33, 38, 0.06);
}

.feed-main strong {
  display: block;
  margin-top: 10px;
  font-size: 16px;
}

.feed-main p {
  margin: 8px 0 0;
  color: var(--om-ink-soft);
  line-height: 1.7;
}

.feed-type {
  color: var(--om-brand);
  font-size: 11px;
  font-weight: 700;
}

.feed-item time,
.empty-copy {
  color: var(--om-ink-soft);
  font-size: 13px;
}

.empty-copy {
  padding: 26px 8px 6px;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .hero-panel {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 700px) {
  .dashboard-page {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .panel-heading,
  .feed-item {
    flex-direction: column;
  }
}
</style>
