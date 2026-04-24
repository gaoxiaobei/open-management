<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>文件中心</span>
          <el-upload
            :show-file-list="false"
            :before-upload="handleUpload"
            action=""
            accept="*"
          >
            <el-button type="primary">上传文件</el-button>
          </el-upload>
        </div>
      </template>
      <el-empty v-if="files.length === 0 && !loading" description="暂无文件" />
      <el-table v-else :data="files" v-loading="loading" stripe>
        <el-table-column prop="originalName" label="文件名" min-width="240" />
        <el-table-column prop="contentType" label="类型" width="160" />
        <el-table-column label="大小" width="120">
          <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column prop="bizType" label="业务类型" width="120" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handlePreview(row)">预览</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listFiles, uploadFile, getFileDownloadUrl, deleteFile, type FileVO } from '@/api/file'

const loading = ref(false)
const files = ref<FileVO[]>([])

function formatSize(bytes: number) {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

async function loadData() {
  loading.value = true
  try {
    files.value = await listFiles()
  } finally {
    loading.value = false
  }
}

async function handleUpload(file: File) {
  try {
    await uploadFile(file)
    ElMessage.success('上传成功')
    await loadData()
  } catch {
    // error handled by interceptor
  }
  return false
}

async function handlePreview(row: FileVO) {
  window.open(getFileDownloadUrl(row.id), '_blank')
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该文件吗？', '提示', { type: 'warning' })
  await deleteFile(id)
  ElMessage.success('文件已删除')
  await loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container { padding: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
