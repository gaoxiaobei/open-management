<template>
  <el-dialog v-model="visible" title="发现新版本" width="360px" :close-on-click-modal="false">
    <p>新版本已下载完成，重启后即可使用最新版本。</p>
    <template #footer>
      <el-button @click="visible = false">稍后重启</el-button>
      <el-button type="primary" @click="installUpdate">立即重启</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const visible = ref(false)

onMounted(() => {
  window.electronAPI?.onUpdateDownloaded(() => {
    visible.value = true
  })
})

function installUpdate() {
  window.electronAPI?.installUpdate()
}
</script>
