<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <div class="logo">Open Management</div>
      <el-menu :router="true" :default-active="route.path" background-color="#001529" text-color="#ffffffa6" active-text-color="#ffffff">
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>工作台</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <div class="header-title">Open Management</div>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<style scoped>
.layout-container { height: 100vh; }
.layout-aside { background-color: #001529; }
.logo { height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; font-weight: bold; }
.layout-header { background: #fff; border-bottom: 1px solid #e8e8e8; display: flex; align-items: center; padding: 0 20px; }
.header-title { font-weight: bold; font-size: 16px; }
.layout-main { background: #f0f2f5; }
</style>
