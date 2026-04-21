<template>
  <div class="page-container">
    <el-row :gutter="16" class="summary-row">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="summary-card">
            <div class="summary-value">{{ unreadCount }}</div>
            <div class="summary-label">未读消息</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="summary-card">
            <div class="summary-value">{{ todoCount }}</div>
            <div class="summary-label">待办消息</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="summary-card">
            <div class="summary-value">{{ noticeCount }}</div>
            <div class="summary-label">通知消息</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header><span>消息中心</span></template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="msgType" label="类型" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isRead ? 'info' : 'danger'">
              {{ row.isRead ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="接收时间" min-width="180" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :disabled="!!row.isRead" @click="handleMarkRead(row.id)">标记已读</el-button>
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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useMessageStore } from '@/stores/message'
import {
  markMessageRead,
  pageMyMessages,
  type MessageVO,
} from '@/api/message'

const loading = ref(false)
const tableData = ref<MessageVO[]>([])
const total = ref(0)
const messageStore = useMessageStore()

const query = reactive({
  pageNum: 1,
  pageSize: 20,
})

const unreadCount = computed(() => messageStore.unreadCount)
const todoCount = computed(() => tableData.value.filter((item) => item.msgType === 'TODO').length)
const noticeCount = computed(() => tableData.value.filter((item) => item.msgType === 'NOTICE').length)

async function loadData() {
  loading.value = true
  try {
    const messages = await pageMyMessages(query)
    tableData.value = messages.records
    total.value = messages.total
    await messageStore.refreshUnreadCount()
  } finally {
    loading.value = false
  }
}

async function handleMarkRead(id: number) {
  await markMessageRead(id)
  messageStore.decrementUnreadCount()
  ElMessage.success('消息已标记为已读')
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page-container {
  padding: 16px;
}

.summary-row {
  margin-bottom: 16px;
}

.summary-card {
  text-align: center;
  padding: 8px 0;
}

.summary-value {
  font-size: 28px;
  font-weight: 600;
  color: #1d4ed8;
}

.summary-label {
  margin-top: 6px;
  color: #64748b;
}
</style>
