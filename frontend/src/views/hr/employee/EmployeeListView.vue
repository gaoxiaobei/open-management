<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>员工档案</span>
          <el-button type="primary">新增员工</el-button>
        </div>
      </template>
      <el-table :data="employees" v-loading="loading" stripe>
        <el-table-column prop="empNo" label="员工编号" />
        <el-table-column prop="empName" label="姓名" />
        <el-table-column prop="deptName" label="部门" />
        <el-table-column prop="positionName" label="岗位" />
        <el-table-column prop="hireDate" label="入职日期" />
        <el-table-column prop="empStatus" label="状态" />
        <el-table-column prop="mobile" label="手机号" />
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" layout="total, sizes, prev, pager, next" @change="loadData" style="margin-top:16px;justify-content:flex-end" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { pageEmployees, type EmployeeVO } from '@/api/hr'

const loading = ref(false)
const employees = ref<EmployeeVO[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 20 })

async function loadData() {
  loading.value = true
  try {
    const result = await pageEmployees(query) as unknown as { total: number; records: EmployeeVO[] }
    employees.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}
onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
