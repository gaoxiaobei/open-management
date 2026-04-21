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
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
            <el-option label="锁定" value="LOCKED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="姓名" min-width="120" />
        <el-table-column prop="mobile" label="手机号" min-width="140" />
        <el-table-column label="部门" min-width="140">
          <template #default="{ row }">
            {{ resolveDeptName(row.deptId) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagMap[row.status] || 'info'">
              {{ statusLabelMap[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button
              size="small"
              :type="row.status === 'ENABLED' ? 'warning' : 'success'"
              @click="toggleStatus(row)"
            >
              {{ row.status === 'ENABLED' ? '停用' : '启用' }}
            </el-button>
            <el-button size="small" type="warning" @click="handleResetPassword(row.id)">重置密码</el-button>
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
      :title="dialogMode === 'create' ? '新增用户' : '编辑用户'"
      width="640px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" :disabled="dialogMode === 'edit'" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="form.realName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.mobile" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门">
              <el-select v-model="form.deptId" placeholder="请选择部门" clearable style="width: 100%">
                <el-option
                  v-for="dept in deptOptions"
                  :key="dept.id"
                  :label="dept.label"
                  :value="dept.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.roleIds" multiple placeholder="请选择角色" clearable style="width: 100%">
                <el-option
                  v-for="role in roleOptions"
                  :key="role.id"
                  :label="role.roleName"
                  :value="role.id!"
                />
              </el-select>
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
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  changeUserStatus,
  createUser,
  deleteUser,
  getUserById,
  listAllRoles,
  pageUsers,
  resetPassword,
  updateUser,
  type RoleVO,
  type UserCreateParams,
  type UserVO,
} from '@/api/system'
import { getDeptTree, type DeptTreeNode } from '@/api/org'

type DialogMode = 'create' | 'edit'

interface DeptOption {
  id: number
  label: string
}

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const tableData = ref<UserVO[]>([])
const total = ref(0)
const roleOptions = ref<RoleVO[]>([])
const deptTree = ref<DeptTreeNode[]>([])
const deptOptions = ref<DeptOption[]>([])
const formRef = ref<FormInstance>()
const editingId = ref<number>()

const query = reactive({
  pageNum: 1,
  pageSize: 20,
  username: '',
  realName: '',
  status: '',
})

const form = reactive<UserCreateParams>({
  username: '',
  realName: '',
  mobile: '',
  email: '',
  deptId: undefined,
  roleIds: [],
})

const rules: FormRules<UserCreateParams> = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
}

const statusLabelMap: Record<string, string> = {
  ENABLED: '启用',
  DISABLED: '禁用',
  LOCKED: '锁定',
}

const statusTagMap: Record<string, 'success' | 'warning' | 'danger' | 'info'> = {
  ENABLED: 'success',
  DISABLED: 'warning',
  LOCKED: 'danger',
}

async function loadData() {
  loading.value = true
  try {
    const result = await pageUsers(query)
    tableData.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.username = ''
  query.realName = ''
  query.status = ''
  query.pageNum = 1
  loadData()
}

function resetForm() {
  editingId.value = undefined
  form.username = ''
  form.realName = ''
  form.mobile = ''
  form.email = ''
  form.deptId = undefined
  form.positionId = undefined
  form.roleIds = []
  formRef.value?.clearValidate()
}

function flattenDeptTree(nodes: DeptTreeNode[], prefix = ''): DeptOption[] {
  return nodes.flatMap((node) => {
    const currentLabel = prefix ? `${prefix} / ${node.deptName}` : node.deptName
    const current = [{ id: node.id, label: currentLabel }]
    const children = node.children?.length ? flattenDeptTree(node.children, currentLabel) : []
    return current.concat(children)
  })
}

function resolveDeptName(deptId?: number) {
  if (!deptId) {
    return ''
  }
  return deptOptions.value.find((item) => item.id === deptId)?.label || ''
}

async function bootstrapOptions() {
  const [roles, depts] = await Promise.all([listAllRoles(), getDeptTree()])
  roleOptions.value = roles
  deptTree.value = depts
  deptOptions.value = flattenDeptTree(depts)
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

async function openEditDialog(row: UserVO) {
  dialogMode.value = 'edit'
  resetForm()
  const detail = await getUserById(row.id)
  editingId.value = row.id
  form.username = detail.username
  form.realName = detail.realName
  form.mobile = detail.mobile || ''
  form.email = detail.email || ''
  form.deptId = detail.deptId
  form.positionId = detail.positionId
  form.roleIds = detail.roleIds || []
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'create') {
      await createUser(form)
      ElMessage.success('用户已创建，初始密码为 Admin@123')
    } else if (editingId.value) {
      await updateUser(editingId.value, form)
      ElMessage.success('用户信息已更新')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    submitting.value = false
  }
}

async function handleResetPassword(id: number) {
  await ElMessageBox.confirm('确定将该用户密码重置为默认密码 Admin@123 吗？', '提示', {
    type: 'warning',
  })
  await resetPassword(id)
  ElMessage.success('密码已重置')
}

async function toggleStatus(row: UserVO) {
  const nextStatus = row.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'
  await changeUserStatus(row.id, nextStatus)
  ElMessage.success(`用户已${nextStatus === 'ENABLED' ? '启用' : '停用'}`)
  await loadData()
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('删除后不可恢复，确定继续吗？', '提示', { type: 'warning' })
  await deleteUser(id)
  ElMessage.success('用户已删除')
  await loadData()
}

onMounted(async () => {
  await bootstrapOptions()
  await loadData()
})
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
