<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-title">{{ card.title }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const statCards = ref([
  { title: '在职员工', value: '—' },
  { title: '待审批', value: '—' },
  { title: '资产数量', value: '—' },
  { title: '今日登录', value: '—' },
])
</script>

<style scoped>
.dashboard { padding: 16px; }
.stat-card { margin-bottom: 16px; }
.stat-content { text-align: center; }
.stat-value { font-size: 32px; font-weight: bold; color: #1677ff; }
.stat-title { margin-top: 8px; color: #666; font-size: 14px; }
</style>
