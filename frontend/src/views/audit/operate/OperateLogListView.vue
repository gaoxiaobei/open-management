<template>
  <div class="page-container">
    <el-card>
      <template #header><span>操作日志</span></template>

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
        <el-table-column prop="moduleName" label="模块" min-width="140" />
        <el-table-column prop="operationName" label="操作" min-width="140" />
        <el-table-column prop="username" label="用户" min-width="120" />
        <el-table-column prop="requestMethod" label="方法" width="90" />
        <el-table-column prop="requestUrl" label="地址" min-width="220" show-overflow-tooltip />
        <el-table-column prop="ipAddr" label="IP" min-width="140" />
        <el-table-column prop="costTime" label="耗时(ms)" width="110" />
        <el-table-column label="操作时间" min-width="180">
          <template #default="{ row }">
            {{ formatTime(row.operateTime) }}
          </template>
        </el-table-column>
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
import dayjs from 'dayjs'
import { pageOperateLogs, type OperateLogVO } from '@/api/audit'

function formatTime(time?: string) {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'
}

const loading = ref(false)
const tableData = ref<OperateLogVO[]>([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 20,
  userId: undefined as number | undefined,
})

async function loadData() {
  loading.value = true
  try {
    const result = await pageOperateLogs(query)
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
