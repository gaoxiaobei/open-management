<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <div class="logo">
        <span>Open Management</span>
      </div>
      <el-menu
        :router="true"
        :default-active="route.path"
        background-color="#001529"
        text-color="#ffffffa6"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/users">用户管理</el-menu-item>
          <el-menu-item index="/system/roles">角色管理</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/org/dept">
          <el-icon><OfficeBuilding /></el-icon>
          <span>组织架构</span>
        </el-menu-item>
        <el-menu-item index="/hr/employees">
          <el-icon><User /></el-icon>
          <span>员工档案</span>
        </el-menu-item>
        <el-sub-menu index="oa">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>OA 审批</span>
          </template>
          <el-menu-item index="/oa/leave">请假申请</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/asset">
          <el-icon><Box /></el-icon>
          <span>资产管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-name">
              {{ userStore.userInfo?.realName || userStore.userInfo?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

async function handleCommand(command: string) {
  if (command === 'logout') {
    await userStore.doLogout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.layout-aside { background-color: #001529; overflow-y: auto; }
.logo { height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; font-weight: bold; border-bottom: 1px solid #002040; }
.layout-header { background-color: #fff; border-bottom: 1px solid #e8e8e8; display: flex; align-items: center; justify-content: flex-end; padding: 0 20px; }
.header-right { display: flex; align-items: center; gap: 16px; }
.user-name { cursor: pointer; display: flex; align-items: center; gap: 4px; }
.layout-main { background-color: #f0f2f5; overflow-y: auto; }
</style>
