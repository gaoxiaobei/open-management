<template>
  <div class="shell">
    <transition name="overlay-fade">
      <div v-if="mobileNavOpen" class="shell-overlay" @click="mobileNavOpen = false" />
    </transition>

    <!-- 侧边导航 -->
    <aside class="shell-sidebar" :class="{ 'is-open': mobileNavOpen }">
      <div class="sidebar-brand">
        <div class="brand-logo">
          <span class="brand-logo-text">政</span>
        </div>
        <div class="brand-info">
          <div class="brand-name">开放管理平台</div>
          <div class="brand-sub">Open Management</div>
        </div>
      </div>

      <div class="sidebar-divider" />

      <nav class="sidebar-nav">
        <div v-for="section in navSections" :key="section.title" class="nav-group">
          <div class="nav-group-title">{{ section.title }}</div>
          <router-link
            v-for="item in section.items"
            :key="item.path"
            :to="item.path"
            class="nav-item"
            :class="{ 'is-active': route.path === item.path }"
            @click="mobileNavOpen = false"
          >
            <el-icon class="nav-item-icon"><component :is="item.icon" /></el-icon>
            <span class="nav-item-label">{{ item.label }}</span>
            <span v-if="item.path === '/messages' && messageStore.unreadCount > 0" class="nav-badge">
              {{ messageStore.unreadCount > 99 ? '99+' : messageStore.unreadCount }}
            </span>
          </router-link>
        </div>
      </nav>

      <div class="sidebar-footer">
        <div class="sidebar-footer-text">{{ clockLabel }}</div>
        <div class="sidebar-footer-sub">系统运行正常</div>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="shell-body">
      <!-- 顶部工具栏 -->
      <header class="shell-header">
        <div class="header-left">
          <button type="button" class="mobile-menu-btn" aria-label="菜单" @click="mobileNavOpen = true">
            <el-icon><Menu /></el-icon>
          </button>
          <div class="header-breadcrumb">
            <span class="breadcrumb-section">{{ sectionTitle }}</span>
            <span class="breadcrumb-sep">›</span>
            <span class="breadcrumb-page">{{ pageTitle }}</span>
          </div>
        </div>

        <div class="header-right">
          <div class="header-clock">{{ clockLabel }}</div>

          <button
            type="button"
            class="header-icon-btn"
            aria-label="消息中心"
            @click="router.push('/messages')"
          >
            <el-badge :value="messageStore.unreadCount" :hidden="!messageStore.unreadCount" :max="99">
              <el-icon><Bell /></el-icon>
            </el-badge>
          </button>

          <el-dropdown @command="handleCommand" trigger="click">
            <button class="header-user-btn" type="button">
              <div class="user-avatar">{{ displayName.charAt(0) }}</div>
              <span class="user-name">{{ displayName }}</span>
              <el-icon class="user-arrow"><ArrowDown /></el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 页面内容 -->
      <main class="shell-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'
import dayjs from 'dayjs'
import {
  ArrowDown, Bell, Box, Connection, Document, DocumentChecked,
  Menu, OfficeBuilding, Odometer, Setting, User, FolderOpened,
  List, SwitchButton
} from '@element-plus/icons-vue'
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMessageStore } from '@/stores/message'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const messageStore = useMessageStore()
const mobileNavOpen = ref(false)
const clockLabel = ref(dayjs().format('YYYY-MM-DD HH:mm'))
let timer: number | undefined

interface NavItem {
  path: string
  label: string
  icon: Component
}

interface NavSection {
  title: string
  items: NavItem[]
}

const navSections: NavSection[] = [
  {
    title: '工作台',
    items: [
      { path: '/dashboard', label: '首页总览', icon: Odometer },
      { path: '/messages', label: '消息中心', icon: Bell },
      { path: '/workflow/todo', label: '我的待办', icon: Connection },
      { path: '/workflow/manage', label: '流程管理', icon: List },
    ],
  },
  {
    title: '系统管理',
    items: [
      { path: '/system/users', label: '用户管理', icon: User },
      { path: '/system/roles', label: '角色管理', icon: Setting },
      { path: '/system/menus', label: '菜单管理', icon: Document },
      { path: '/system/dicts', label: '字典管理', icon: List },
      { path: '/system/configs', label: '参数配置', icon: Setting },
      { path: '/org/dept', label: '部门管理', icon: OfficeBuilding },
      { path: '/org/positions', label: '岗位管理', icon: User },
      { path: '/audit/login-logs', label: '登录日志', icon: DocumentChecked },
      { path: '/audit/operate-logs', label: '操作日志', icon: DocumentChecked },
      { path: '/files', label: '文件中心', icon: FolderOpened },
    ],
  },
  {
    title: '业务管理',
    items: [
      { path: '/hr/employees', label: '员工档案', icon: User },
      { path: '/oa/leave', label: '请假申请', icon: Document },
      { path: '/asset', label: '资产管理', icon: Box },
    ],
  },
]

const pageTitle = computed(() => String(route.meta.title || '管理平台'))
const sectionTitle = computed(() => {
  const match = navSections.find((s) => s.items.some((item) => item.path === route.path))
  return match?.title || '系统'
})
const displayName = computed(() => userStore.userInfo?.realName || userStore.userInfo?.username || '用户')

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
})

onMounted(() => {
  timer = window.setInterval(() => {
    clockLabel.value = dayjs().format('YYYY-MM-DD HH:mm')
  }, 60000)
  if (userStore.isLoggedIn()) {
    messageStore.refreshUnreadCount()
  }
})

onBeforeUnmount(() => {
  if (timer !== undefined) {
    window.clearInterval(timer)
    timer = undefined
  }
})
</script>

<style scoped>
.shell {
  display: flex;
  min-height: 100vh;
  background: var(--om-bg);
}

/* ─── 侧边栏 ─── */
.shell-sidebar {
  width: var(--om-sidebar-width);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: var(--om-sidebar-bg);
  border-right: 1px solid rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  height: 100vh;
  overflow: hidden;
  z-index: 100;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 16px 14px;
  flex-shrink: 0;
}

.brand-logo {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.brand-logo-text {
  font-family: var(--om-font-serif);
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
  line-height: 1;
}

.brand-name {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  line-height: 1.3;
  letter-spacing: 0.02em;
}

.brand-sub {
  font-size: 11px;
  color: var(--om-sidebar-text-muted);
  margin-top: 2px;
  letter-spacing: 0.04em;
}

.sidebar-divider {
  height: 1px;
  background: var(--om-sidebar-border);
  margin: 0 12px;
  flex-shrink: 0;
}

.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
  scrollbar-width: thin;
  scrollbar-color: rgba(255,255,255,0.15) transparent;
}

.sidebar-nav::-webkit-scrollbar {
  width: 4px;
}

.sidebar-nav::-webkit-scrollbar-thumb {
  background: rgba(255,255,255,0.15);
  border-radius: 2px;
}

.nav-group {
  margin-bottom: 4px;
}

.nav-group-title {
  padding: 10px 16px 4px;
  font-size: 11px;
  font-weight: 600;
  color: var(--om-sidebar-text-muted);
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  color: var(--om-sidebar-text);
  text-decoration: none;
  font-size: 13px;
  transition: background 0.12s;
  position: relative;
  cursor: pointer;
}

.nav-item:hover {
  background: var(--om-sidebar-hover-bg);
  text-decoration: none;
  color: #ffffff;
}

.nav-item.is-active {
  background: var(--om-sidebar-active-bg);
  color: #ffffff;
  font-weight: 500;
}

.nav-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: #ffffff;
}

.nav-item-icon {
  font-size: 15px;
  flex-shrink: 0;
  opacity: 0.8;
}

.nav-item.is-active .nav-item-icon {
  opacity: 1;
}

.nav-item-label {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.nav-badge {
  background: var(--om-accent);
  color: #ffffff;
  font-size: 10px;
  font-weight: 600;
  padding: 1px 5px;
  border-radius: 2px;
  line-height: 1.4;
  flex-shrink: 0;
}

.sidebar-footer {
  padding: 12px 16px;
  border-top: 1px solid var(--om-sidebar-border);
  flex-shrink: 0;
}

.sidebar-footer-text {
  font-size: 12px;
  color: var(--om-sidebar-text-muted);
  font-variant-numeric: tabular-nums;
}

.sidebar-footer-sub {
  font-size: 11px;
  color: rgba(255,255,255,0.3);
  margin-top: 2px;
}

/* ─── 主体 ─── */
.shell-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

/* ─── 顶部工具栏 ─── */
.shell-header {
  height: var(--om-header-height);
  background: var(--om-header-bg);
  border-bottom: 1px solid var(--om-header-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 50;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.mobile-menu-btn {
  display: none;
  width: 32px;
  height: 32px;
  border: 1px solid var(--om-border);
  background: transparent;
  border-radius: var(--om-radius-md);
  cursor: pointer;
  align-items: center;
  justify-content: center;
  color: var(--om-text-secondary);
}

.header-breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.breadcrumb-section {
  color: var(--om-text-muted);
}

.breadcrumb-sep {
  color: var(--om-text-disabled);
  font-size: 12px;
}

.breadcrumb-page {
  color: var(--om-text);
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-clock {
  font-size: 12px;
  color: var(--om-text-muted);
  font-variant-numeric: tabular-nums;
  padding: 0 8px;
  border-right: 1px solid var(--om-border-light);
  margin-right: 4px;
}

.header-icon-btn {
  width: 32px;
  height: 32px;
  border: 1px solid var(--om-border);
  background: transparent;
  border-radius: var(--om-radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--om-text-secondary);
  transition: all 0.12s;
}

.header-icon-btn:hover {
  background: var(--om-primary-muted);
  border-color: var(--om-primary);
  color: var(--om-primary);
}

.header-user-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px 4px 4px;
  border: 1px solid var(--om-border);
  background: transparent;
  border-radius: var(--om-radius-md);
  cursor: pointer;
  color: var(--om-text);
  transition: all 0.12s;
}

.header-user-btn:hover {
  background: var(--om-primary-muted);
  border-color: var(--om-primary);
}

.user-avatar {
  width: 24px;
  height: 24px;
  background: var(--om-primary);
  color: #ffffff;
  border-radius: 2px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.user-name {
  font-size: 13px;
  font-weight: 500;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-arrow {
  font-size: 11px;
  color: var(--om-text-muted);
}

/* ─── 内容区 ─── */
.shell-content {
  flex: 1;
  min-width: 0;
  overflow: auto;
}

/* ─── 遮罩 ─── */
.shell-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 90;
}

.overlay-fade-enter-active,
.overlay-fade-leave-active {
  transition: opacity 0.2s;
}

.overlay-fade-enter-from,
.overlay-fade-leave-to {
  opacity: 0;
}

/* ─── 响应式 ─── */
@media (max-width: 1024px) {
  .shell-sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    transform: translateX(-100%);
    transition: transform 0.22s ease;
  }

  .shell-sidebar.is-open {
    transform: translateX(0);
  }

  .mobile-menu-btn {
    display: flex;
  }

  .header-clock {
    display: none;
  }
}

@media (max-width: 640px) {
  .shell-header {
    padding: 0 12px;
  }

  .user-name {
    display: none;
  }
}
</style>
