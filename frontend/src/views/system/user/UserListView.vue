<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="openCreateDialog">新增用户</el-button>
        </div>
      </template>
      <el-form :inline="true" :model="query" @submit.prevent="loadData">
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="query.realName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="mobile" label="手机号" />
        <el-table-column prop="deptName" label="部门" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'danger'">
              {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="warning" @click="handleResetPassword(row.id)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, sizes, prev, pager, next" @change="loadData" style="margin-top:16px;justify-content:flex-end" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageUsers, resetPassword, type UserVO } from '@/api/system'

const loading = ref(false)
const tableData = ref<UserVO[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 20, username: '', realName: '' })

async function loadData() {
  loading.value = true
  try {
    const result = await pageUsers(query) as unknown as { total: number; records: UserVO[] }
    tableData.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}
function resetQuery() { query.username = ''; query.realName = ''; query.pageNum = 1; loadData() }
function openCreateDialog() { /* TODO */ }
function openEditDialog(_row: UserVO) { /* TODO */ }
async function handleResetPassword(id: number) {
  await ElMessageBox.confirm('确定重置该用户密码？', '提示', { type: 'warning' })
  await resetPassword(id)
  ElMessage.success('密码已重置')
}
onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
