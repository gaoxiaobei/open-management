<template>
  <div class="page-container">
    <el-card>
      <template #header><span>登录日志</span></template>

      <el-form :inline="true" :model="query" @submit.prevent="loadData">
        <el-form-item label="用户ID">
          <el-input-number v-model="query.userId" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="ipAddr" label="IP" min-width="140" />
        <el-table-column prop="browser" label="浏览器" min-width="140" />
        <el-table-column prop="os" label="操作系统" min-width="140" />
        <el-table-column prop="loginStatus" label="状态" width="100" />
        <el-table-column prop="msg" label="结果" min-width="180" show-overflow-tooltip />
        <el-table-column prop="loginTime" label="登录时间" min-width="180" />
      </el-table>

      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end"
        @change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { pageLoginLogs, type LoginLogVO } from '@/api/audit'

const loading = ref(false)
const tableData = ref<LoginLogVO[]>([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 20,
  userId: undefined as number | undefined,
})

async function loadData() {
  loading.value = true
  try {
    const result = await pageLoginLogs(query)
    tableData.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.userId = undefined
  query.pageNum = 1
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-container {
  padding: 16px;
}
</style>
