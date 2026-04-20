<template>
  <div class="page-container">
    <el-card>
      <template #header><span>资产台账</span></template>
      <el-table :data="assets" v-loading="loading" stripe>
        <el-table-column prop="assetCode" label="资产编号" />
        <el-table-column prop="assetName" label="资产名称" />
        <el-table-column prop="category" label="类别" />
        <el-table-column prop="assetStatus" label="状态" />
        <el-table-column prop="location" label="存放地点" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" @click="handleReceive(row.id)">领用申请</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageAssets, submitReceiveApply, type AssetVO } from '@/api/asset'

const loading = ref(false)
const assets = ref<AssetVO[]>([])

async function loadData() {
  loading.value = true
  try {
    const result = await pageAssets({ pageNum: 1, pageSize: 20 }) as unknown as { total: number; records: AssetVO[] }
    assets.value = result.records
  } finally {
    loading.value = false
  }
}
async function handleReceive(assetId: number) {
  const { value: reason } = await ElMessageBox.prompt('请输入领用原因', '领用申请', { confirmButtonText: '提交', cancelButtonText: '取消' })
  await submitReceiveApply({ assetId, reason })
  ElMessage.success('领用申请已提交')
}
onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 16px; }
</style>
