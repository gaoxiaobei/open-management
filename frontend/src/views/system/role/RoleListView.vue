<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="openCreateDialog">新增角色</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="query" @submit.prevent="loadData">
        <el-form-item label="角色名称">
          <el-input v-model="query.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="query.roleCode" placeholder="请输入角色编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="roles" v-loading="loading" stripe>
        <el-table-column prop="roleCode" label="角色编码" min-width="140" />
        <el-table-column prop="roleName" label="角色名称" min-width="140" />
        <el-table-column prop="dataScope" label="数据范围" min-width="120" />
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'warning'">
              {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="primary" plain @click="openMenuDrawer(row)">菜单权限</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增角色' : '编辑角色'"
      width="560px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" :disabled="dialogMode === 'edit'" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="数据范围" prop="dataScope">
          <el-select v-model="form.dataScope" style="width: 100%">
            <el-option label="全部数据" value="ALL" />
            <el-option label="本部门" value="DEPT" />
            <el-option label="仅本人" value="SELF" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="ENABLED">启用</el-radio>
            <el-radio value="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="menuDrawerVisible" title="角色菜单权限" size="420px">
      <div class="drawer-header">
        <div>{{ currentRole?.roleName || '-' }}</div>
        <el-button link type="primary" @click="checkAllMenus">全选</el-button>
      </div>
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        :props="{ label: 'menuName', children: 'children' }"
        show-checkbox
        node-key="id"
        default-expand-all
      />
      <template #footer>
        <div class="drawer-footer">
          <el-button @click="menuDrawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="menuSubmitting" @click="submitRoleMenus">保存权限</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type TreeInstance } from 'element-plus'
import {
  assignRoleMenus,
  createRole,
  deleteRole,
  getMenuTree,
  getRoleById,
  getRoleMenuIds,
  pageRoles,
  updateRole,
  type MenuVO,
  type RoleVO,
} from '@/api/system'

type DialogMode = 'create' | 'edit'

const loading = ref(false)
const submitting = ref(false)
const menuSubmitting = ref(false)
const dialogVisible = ref(false)
const menuDrawerVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const roles = ref<RoleVO[]>([])
const menuTree = ref<MenuVO[]>([])
const total = ref(0)
const editingId = ref<number>()
const currentRole = ref<RoleVO>()
const formRef = ref<FormInstance>()
const menuTreeRef = ref<TreeInstance>()

const query = reactive({
  pageNum: 1,
  pageSize: 20,
  roleName: '',
  roleCode: '',
  status: '',
})

const form = reactive<RoleVO>({
  roleCode: '',
  roleName: '',
  dataScope: 'DEPT',
  status: 'ENABLED',
  remark: '',
})

const rules: FormRules<RoleVO> = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  dataScope: [{ required: true, message: '请选择数据范围', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

async function loadData() {
  loading.value = true
  try {
    const result = await pageRoles(query)
    roles.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

async function loadMenus() {
  menuTree.value = await getMenuTree()
}

function resetQuery() {
  query.roleName = ''
  query.roleCode = ''
  query.status = ''
  query.pageNum = 1
  loadData()
}

function resetForm() {
  editingId.value = undefined
  form.roleCode = ''
  form.roleName = ''
  form.dataScope = 'DEPT'
  form.status = 'ENABLED'
  form.remark = ''
  formRef.value?.clearValidate()
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

async function openEditDialog(row: RoleVO) {
  dialogMode.value = 'edit'
  resetForm()
  const detail = await getRoleById(row.id!)
  editingId.value = row.id
  form.roleCode = detail.roleCode
  form.roleName = detail.roleName
  form.dataScope = detail.dataScope
  form.status = detail.status
  form.remark = detail.remark || ''
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'create') {
      await createRole(form)
      ElMessage.success('角色已创建')
    } else if (editingId.value) {
      await updateRole(editingId.value, form)
      ElMessage.success('角色已更新')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id?: number) {
  if (!id) {
    return
  }
  await ElMessageBox.confirm('删除角色后将移除关联的菜单与用户关系，确定继续吗？', '提示', {
    type: 'warning',
  })
  await deleteRole(id)
  ElMessage.success('角色已删除')
  await loadData()
}

async function openMenuDrawer(row: RoleVO) {
  currentRole.value = row
  if (!menuTree.value.length) {
    await loadMenus()
  }
  menuDrawerVisible.value = true
  await nextTick()
  menuTreeRef.value?.setCheckedKeys(await getRoleMenuIds(row.id!))
}

function checkAllMenus() {
  menuTreeRef.value?.setCheckedKeys(flattenMenuIds(menuTree.value))
}

function flattenMenuIds(nodes: MenuVO[]): number[] {
  return nodes.flatMap((node) => {
    const current = node.id ? [node.id] : []
    const children = node.children?.length ? flattenMenuIds(node.children) : []
    return current.concat(children)
  })
}

async function submitRoleMenus() {
  if (!currentRole.value?.id) {
    return
  }
  menuSubmitting.value = true
  try {
    const checkedKeys = menuTreeRef.value?.getCheckedKeys(false) || []
    const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || []
    const menuIds = [...new Set([...checkedKeys, ...halfCheckedKeys])].map((item) => Number(item))
    await assignRoleMenus(currentRole.value.id, menuIds)
    ElMessage.success('菜单权限已保存')
    menuDrawerVisible.value = false
  } finally {
    menuSubmitting.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadData(), loadMenus()])
})
</script>

<style scoped>
.page-container {
  padding: 16px;
}

.card-header,
.drawer-header,
.drawer-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
