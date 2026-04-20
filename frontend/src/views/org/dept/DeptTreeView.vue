<template>
  <div class="page-container">
    <el-card>
      <template #header><span>部门管理</span></template>
      <el-tree :data="deptTree" :props="{ label: 'deptName', children: 'children' }" default-expand-all node-key="id" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDeptTree, type DeptTreeNode } from '@/api/org'

const deptTree = ref<DeptTreeNode[]>([])
onMounted(async () => { deptTree.value = await getDeptTree() as unknown as DeptTreeNode[] })
</script>

<style scoped>
.page-container { padding: 16px; }
</style>
