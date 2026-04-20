<template>
  <div class="page-container">
    <el-card>
      <template #header><span>角色管理</span></template>
      <el-table :data="roles" stripe>
        <el-table-column prop="roleCode" label="角色编码" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="dataScope" label="数据范围" />
        <el-table-column prop="status" label="状态" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listRoles, type RoleVO } from '@/api/system'

const roles = ref<RoleVO[]>([])
onMounted(async () => { roles.value = await listRoles() as unknown as RoleVO[] })
</script>

<style scoped>
.page-container { padding: 16px; }
</style>
