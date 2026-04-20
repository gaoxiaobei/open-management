<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>请假申请</span>
          <el-button type="primary" @click="dialogVisible = true">发起请假</el-button>
        </div>
      </template>
      <el-table :data="applies" v-loading="loading" stripe>
        <el-table-column prop="applyNo" label="申请单号" />
        <el-table-column prop="leaveType" label="请假类型" />
        <el-table-column prop="startTime" label="开始时间" />
        <el-table-column prop="endTime" label="结束时间" />
        <el-table-column prop="leaveDays" label="天数" />
        <el-table-column prop="applyStatus" label="状态" />
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" title="发起请假" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="请假类型">
          <el-select v-model="form.leaveType">
            <el-option label="年假" value="ANNUAL" />
            <el-option label="事假" value="PERSONAL" />
            <el-option label="病假" value="SICK" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="form.reason" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { myLeaveApplies, submitLeaveApply, type LeaveApplyVO } from '@/api/oa'

const loading = ref(false)
const applies = ref<LeaveApplyVO[]>([])
const dialogVisible = ref(false)
const form = reactive({ leaveType: 'ANNUAL', startTime: '', endTime: '', reason: '' })

async function loadData() {
  loading.value = true
  try {
    const result = await myLeaveApplies({ pageNum: 1, pageSize: 20 }) as unknown as { total: number; records: LeaveApplyVO[] }
    applies.value = result.records
  } finally {
    loading.value = false
  }
}
async function handleSubmit() {
  await submitLeaveApply(form)
  ElMessage.success('提交成功')
  dialogVisible.value = false
  loadData()
}
onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
