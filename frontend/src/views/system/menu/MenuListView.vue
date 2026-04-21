<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <el-button type="primary" @click="openCreateDialog()">新增菜单</el-button>
        </div>
      </template>

      <el-table :data="menuTree" row-key="id" default-expand-all stripe>
        <el-table-column prop="menuName" label="菜单名称" min-width="180" />
        <el-table-column prop="menuType" label="类型" width="100" />
        <el-table-column prop="path" label="路由路径" min-width="180" />
        <el-table-column prop="component" label="组件" min-width="220" show-overflow-tooltip />
        <el-table-column prop="permissionCode" label="权限标识" min-width="180" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'warning'">
              {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openCreateDialog(row.id)">新增子菜单</el-button>
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增菜单' : '编辑菜单'"
      width="620px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="上级菜单">
              <el-select v-model="form.parentId" clearable placeholder="根节点" style="width: 100%">
                <el-option :value="0" label="根节点" />
                <el-option
                  v-for="item in menuOptions"
                  :key="item.id"
                  :value="item.id"
                  :label="item.label"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单类型" prop="menuType">
              <el-select v-model="form.menuType" style="width: 100%">
                <el-option label="目录" value="DIR" />
                <el-option label="菜单" value="MENU" />
                <el-option label="按钮" value="BUTTON" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="ENABLED">启用</el-radio>
                <el-radio value="DISABLED">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="路径">
              <el-input v-model="form.path" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组件">
              <el-input v-model="form.component" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图标">
              <el-input v-model="form.icon" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="权限标识">
              <el-input v-model="form.permissionCode" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createMenu,
  deleteMenu,
  getMenuTree,
  updateMenu,
  type MenuVO,
} from '@/api/system'

type DialogMode = 'create' | 'edit'

interface MenuForm {
  parentId?: number
  menuName: string
  menuType: string
  path?: string
  component?: string
  icon?: string
  permissionCode?: string
  sortOrder?: number
  status: string
}

interface MenuOption {
  id: number
  label: string
}

const menuTree = ref<MenuVO[]>([])
const dialogVisible = ref(false)
const submitting = ref(false)
const dialogMode = ref<DialogMode>('create')
const formRef = ref<FormInstance>()
const editingId = ref<number>()

const form = reactive<MenuForm>({
  parentId: 0,
  menuName: '',
  menuType: 'MENU',
  path: '',
  component: '',
  icon: '',
  permissionCode: '',
  sortOrder: 0,
  status: 'ENABLED',
})

const rules: FormRules<MenuForm> = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

const menuOptions = computed<MenuOption[]>(() => flattenMenus(menuTree.value))

function flattenMenus(nodes: MenuVO[], prefix = ''): MenuOption[] {
  return nodes.flatMap((node) => {
    const currentLabel = prefix ? `${prefix} / ${node.menuName}` : node.menuName
    const current = node.id ? [{ id: node.id, label: currentLabel }] : []
    const children = node.children?.length ? flattenMenus(node.children, currentLabel) : []
    return current.concat(children)
  })
}

async function loadMenus() {
  menuTree.value = await getMenuTree()
}

function resetForm(parentId = 0) {
  editingId.value = undefined
  form.parentId = parentId
  form.menuName = ''
  form.menuType = 'MENU'
  form.path = ''
  form.component = ''
  form.icon = ''
  form.permissionCode = ''
  form.sortOrder = 0
  form.status = 'ENABLED'
  formRef.value?.clearValidate()
}

function openCreateDialog(parentId = 0) {
  dialogMode.value = 'create'
  resetForm(parentId)
  dialogVisible.value = true
}

function openEditDialog(row: MenuVO) {
  dialogMode.value = 'edit'
  resetForm(row.parentId || 0)
  editingId.value = row.id
  form.parentId = row.parentId || 0
  form.menuName = row.menuName
  form.menuType = row.menuType
  form.path = row.path || ''
  form.component = row.component || ''
  form.icon = row.icon || ''
  form.permissionCode = row.permissionCode || ''
  form.sortOrder = row.sortOrder || 0
  form.status = row.status
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'create') {
      await createMenu(form)
      ElMessage.success('菜单已创建')
    } else if (editingId.value) {
      await updateMenu(editingId.value, form)
      ElMessage.success('菜单已更新')
    }
    dialogVisible.value = false
    await loadMenus()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id?: number) {
  if (!id) {
    return
  }
  await ElMessageBox.confirm('删除菜单后无法恢复，确定继续吗？', '提示', { type: 'warning' })
  await deleteMenu(id)
  ElMessage.success('菜单已删除')
  await loadMenus()
}

onMounted(loadMenus)
</script>

<style scoped>
.page-container {
  padding: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
