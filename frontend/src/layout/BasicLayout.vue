<template>
  <div class="shell">
    <transition name="fade">
      <button v-if="mobileNavOpen" class="shell-backdrop" @click="mobileNavOpen = false" />
    </transition>

    <aside class="shell-sidebar" :class="{ 'is-open': mobileNavOpen }">
      <div class="brand-block">
        <div class="brand-mark">OM</div>
        <div>
          <div class="brand-kicker">Enterprise Console</div>
          <div class="brand-title">Open Management</div>
        </div>
      </div>

      <div class="brand-copy">
        审批、组织、审计、资产与消息协同统一收口，作为一套运营工作台使用。
      </div>

      <nav class="nav-sections">
        <section v-for="section in navSections" :key="section.title" class="nav-section">
          <div class="nav-section-title">{{ section.title }}</div>
          <router-link
            v-for="item in section.items"
            :key="item.path"
            :to="item.path"
            class="nav-link"
            :class="{ active: route.path === item.path }"
          >
            <div class="nav-icon">
              <component :is="item.icon" />
            </div>
            <div>
              <div class="nav-label">{{ item.label }}</div>
              <div class="nav-caption">{{ item.caption }}</div>
            </div>
          </router-link>
        </section>
      </nav>

      <div class="sidebar-note">
        <span class="sidebar-note-label">Session</span>
        <strong>{{ messageStore.unreadCount }}</strong>
        <span>unread items tracked live</span>
      </div>
    </aside>

    <div class="shell-main">
      <header class="shell-header">
        <div class="header-leading">
          <button class="nav-toggle" @click="mobileNavOpen = true">
            <el-icon><Menu /></el-icon>
          </button>
          <div>
            <div class="header-kicker">{{ sectionTitle }}</div>
            <h1 class="header-title">{{ pageTitle }}</h1>
            <p class="header-copy">{{ pageCaption }}</p>
          </div>
        </div>

        <div class="header-tools">
          <div class="status-pill">
            <span class="status-dot" />
            {{ clockLabel }}
          </div>

          <el-badge :value="messageStore.unreadCount" :hidden="!messageStore.unreadCount">
            <button class="icon-button" @click="router.push('/messages')">
              <el-icon><Bell /></el-icon>
            </button>
          </el-badge>

          <el-dropdown @command="handleCommand">
            <button class="user-chip">
              <span class="user-chip-copy">
                <strong>{{ displayName }}</strong>
                <small>Workspace operator</small>
              </span>
              <el-icon><ArrowDown /></el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="shell-content">
        <div class="content-shell">
          <router-view />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import dayjs from 'dayjs'
import { ArrowDown, Bell, Box, Connection, Document, DocumentChecked, Menu, OfficeBuilding, Odometer, Setting, User } from '@element-plus/icons-vue'
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMessageStore } from '@/stores/message'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const messageStore = useMessageStore()
const mobileNavOpen = ref(false)
const clockLabel = ref(dayjs().format('YYYY.MM.DD ddd HH:mm'))
let timer: number | undefined

interface NavItem {
  path: string
  label: string
  caption: string
  icon: Component
}

interface NavSection {
  title: string
  items: NavItem[]
}

const navSections: NavSection[] = [
  {
    title: 'Overview',
    items: [
      { path: '/dashboard', label: '工作台', caption: '今日运行态势', icon: Odometer },
      { path: '/messages', label: '消息中心', caption: '通知与提醒收口', icon: Bell },
      { path: '/workflow/todo', label: '我的待办', caption: '当前审批动作', icon: Connection },
    ],
  },
  {
    title: 'Administration',
    items: [
      { path: '/system/users', label: '系统管理', caption: '用户、角色、配置', icon: Setting },
      { path: '/org/dept', label: '组织架构', caption: '部门与岗位结构', icon: OfficeBuilding },
      { path: '/audit/login-logs', label: '日志审计', caption: '登录与操作追踪', icon: DocumentChecked },
    ],
  },
  {
    title: 'Business',
    items: [
      { path: '/hr/employees', label: '员工档案', caption: '人事基础信息', icon: User },
      { path: '/oa/leave', label: 'OA 审批', caption: '请假与流程申请', icon: Document },
      { path: '/asset', label: '资产管理', caption: '台账与领用记录', icon: Box },
    ],
  },
]

const currentNavItem = computed(() =>
  navSections.flatMap((section) => section.items).find((item) => item.path === route.path),
)

const pageTitle = computed(() => String(route.meta.title || 'Open Management'))
const sectionTitle = computed(() => {
  const match = navSections.find((section) => section.items.some((item) => item.path === route.path))
  return match?.title || 'Workspace'
})
const pageCaption = computed(() => currentNavItem.value?.caption || '管理当前页面数据与流程状态。')
const displayName = computed(() => userStore.userInfo?.realName || userStore.userInfo?.username || '系统用户')

async function handleCommand(command: string) {
  if (command === 'logout') {
    await userStore.doLogout()
    messageStore.reset()
    router.push('/login')
  }
}

watch(() => route.fullPath, () => {
  mobileNavOpen.value = false
  if (userStore.isLoggedIn()) {
    messageStore.refreshUnreadCount()
  }
}, { immediate: false })

onMounted(() => {
  timer = window.setInterval(() => {
    clockLabel.value = dayjs().format('YYYY.MM.DD ddd HH:mm')
  }, 60000)

  if (userStore.isLoggedIn()) {
    messageStore.refreshUnreadCount()
  }
})

onBeforeUnmount(() => {
  if (timer) {
    window.clearInterval(timer)
  }
})
</script>

<style scoped>
.shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
}

.shell-sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  padding: 28px 22px;
  display: flex;
  flex-direction: column;
  gap: 22px;
  background:
    radial-gradient(circle at top, rgba(201, 111, 59, 0.16), transparent 34%),
    linear-gradient(180deg, #143a37 0%, #102f2c 38%, #0d2623 100%);
  color: rgba(255, 247, 238, 0.9);
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  z-index: 20;
}

.brand-block {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-mark {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #f6e0c9, #cf7e4f);
  color: #17312f;
  font-family: var(--om-display-font);
  font-size: 24px;
  font-weight: 700;
}

.brand-kicker,
.nav-section-title,
.sidebar-note-label,
.header-kicker,
.nav-caption,
.user-chip-copy small {
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.brand-kicker {
  font-size: 11px;
  opacity: 0.64;
}

.brand-title {
  margin-top: 4px;
  font-family: var(--om-display-font);
  font-size: 28px;
  line-height: 1;
}

.brand-copy {
  margin: 2px 0 4px;
  color: rgba(255, 247, 238, 0.72);
  line-height: 1.7;
  font-size: 14px;
}

.nav-sections {
  display: flex;
  flex-direction: column;
  gap: 18px;
  overflow-y: auto;
}

.nav-section {
  display: grid;
  gap: 10px;
}

.nav-section-title {
  color: rgba(255, 247, 238, 0.42);
  font-size: 11px;
  padding-left: 8px;
}

.nav-link {
  display: grid;
  grid-template-columns: 44px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
  padding: 14px 16px;
  border-radius: 18px;
  color: inherit;
  text-decoration: none;
  transition: transform 0.2s ease, background-color 0.2s ease, border-color 0.2s ease;
  border: 1px solid rgba(255, 255, 255, 0.06);
  background: rgba(255, 255, 255, 0.03);
}

.nav-link:hover,
.nav-link.active {
  transform: translateX(4px);
  background: linear-gradient(135deg, rgba(246, 224, 201, 0.16), rgba(201, 111, 59, 0.14));
  border-color: rgba(246, 224, 201, 0.18);
}

.nav-icon {
  width: 44px;
  height: 44px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.08);
  font-size: 18px;
}

.nav-label {
  font-size: 15px;
  font-weight: 600;
}

.nav-caption {
  margin-top: 4px;
  color: rgba(255, 247, 238, 0.56);
  font-size: 10px;
}

.sidebar-note {
  margin-top: auto;
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
  display: grid;
  gap: 8px;
}

.sidebar-note strong {
  font-family: var(--om-display-font);
  font-size: 42px;
  line-height: 1;
}

.sidebar-note span:last-child {
  color: rgba(255, 247, 238, 0.68);
  font-size: 13px;
}

.shell-main {
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.shell-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  padding: 28px 32px 18px;
  backdrop-filter: blur(18px);
  background: rgba(245, 239, 228, 0.76);
  border-bottom: 1px solid rgba(24, 33, 38, 0.06);
}

.header-leading {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.nav-toggle,
.icon-button,
.user-chip {
  border: 0;
  cursor: pointer;
}

.nav-toggle {
  display: none;
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.8);
  color: var(--om-ink);
  box-shadow: 0 10px 22px rgba(42, 34, 24, 0.08);
}

.header-kicker {
  color: var(--om-brand);
  font-size: 11px;
  font-weight: 700;
}

.header-title {
  margin: 4px 0 0;
  font-family: var(--om-display-font);
  font-size: clamp(30px, 3.2vw, 44px);
  line-height: 0.98;
}

.header-copy {
  margin: 8px 0 0;
  color: var(--om-ink-soft);
}

.header-tools {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-pill,
.icon-button,
.user-chip {
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.76);
  border: 1px solid rgba(24, 33, 38, 0.07);
  box-shadow: 0 12px 28px rgba(42, 34, 24, 0.08);
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 12px 18px;
  color: var(--om-ink-soft);
  font-size: 13px;
  white-space: nowrap;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0f5b52, #c96f3b);
  box-shadow: 0 0 0 6px rgba(15, 91, 82, 0.08);
}

.icon-button {
  width: 48px;
  height: 48px;
  display: grid;
  place-items: center;
  color: var(--om-ink);
  font-size: 18px;
}

.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 8px 10px 8px 16px;
  color: var(--om-ink);
}

.user-chip-copy {
  display: grid;
  text-align: left;
}

.user-chip-copy strong {
  font-size: 14px;
}

.user-chip-copy small {
  color: var(--om-ink-soft);
  font-size: 10px;
}

.shell-content {
  min-width: 0;
  padding: 0 20px 20px;
}

.content-shell {
  min-height: calc(100vh - 140px);
  border: 1px solid rgba(255, 255, 255, 0.34);
  border-radius: 30px;
  background: rgba(255, 252, 248, 0.42);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.5);
}

.shell-backdrop {
  position: fixed;
  inset: 0;
  z-index: 15;
  border: 0;
  background: rgba(14, 20, 22, 0.38);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 1100px) {
  .shell {
    grid-template-columns: 1fr;
  }

  .shell-sidebar {
    position: fixed;
    inset: 0 auto 0 0;
    width: min(86vw, 320px);
    transform: translateX(-100%);
    transition: transform 0.24s ease;
  }

  .shell-sidebar.is-open {
    transform: translateX(0);
  }

  .nav-toggle {
    display: grid;
    place-items: center;
  }
}

@media (max-width: 820px) {
  .shell-header {
    flex-direction: column;
    align-items: stretch;
    padding: 22px 16px 12px;
  }

  .header-tools {
    flex-wrap: wrap;
  }

  .shell-content {
    padding: 0 10px 10px;
  }

  .content-shell {
    border-radius: 24px;
  }
}
</style>
