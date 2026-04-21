<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>字典类型</span>
              <el-button type="primary" @click="openTypeDialog()">新增类型</el-button>
            </div>
          </template>

          <el-form :inline="true" :model="typeQuery" @submit.prevent="loadTypes">
            <el-form-item label="字典编码">
              <el-input v-model="typeQuery.dictType" placeholder="请输入字典编码" clearable />
            </el-form-item>
            <el-form-item label="字典名称">
              <el-input v-model="typeQuery.dictName" placeholder="请输入字典名称" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadTypes">查询</el-button>
              <el-button @click="resetTypeQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table
            :data="dictTypes"
            v-loading="typeLoading"
            stripe
            highlight-current-row
            @current-change="handleTypeSelect"
          >
            <el-table-column prop="dictType" label="字典编码" min-width="140" />
            <el-table-column prop="dictName" label="字典名称" min-width="140" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'ENABLED' ? 'success' : 'warning'">
                  {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="openTypeDialog(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDeleteType(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="typeQuery.pageNum"
            v-model:page-size="typeQuery.pageSize"
            :total="typeTotal"
            layout="total, sizes, prev, pager, next"
            style="margin-top: 16px; justify-content: flex-end"
            @change="loadTypes"
          />
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>字典项 {{ selectedType ? `· ${selectedType.dictName}` : '' }}</span>
              <el-button type="primary" :disabled="!selectedType" @click="openItemDialog()">新增字典项</el-button>
            </div>
          </template>

          <el-empty v-if="!selectedType" description="请选择左侧字典类型" />
          <template v-else>
            <el-table :data="dictItems" v-loading="itemLoading" stripe>
              <el-table-column prop="itemLabel" label="标签" min-width="140" />
              <el-table-column prop="itemValue" label="键值" min-width="140" />
              <el-table-column prop="sortOrder" label="排序" width="80" />
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'ENABLED' ? 'success' : 'warning'">
                    {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" @click="openItemDialog(row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="handleDeleteItem(row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </template>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="typeDialogVisible" :title="typeEditingId ? '编辑字典类型' : '新增字典类型'" width="520px">
      <el-form ref="typeFormRef" :model="typeForm" :rules="typeRules" label-width="90px">
        <el-form-item label="字典编码" prop="dictType">
          <el-input v-model="typeForm.dictType" :disabled="!!typeEditingId" />
        </el-form-item>
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="typeForm.dictName" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="typeForm.status">
            <el-radio value="ENABLED">启用</el-radio>
            <el-radio value="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="typeForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="typeSubmitting" @click="submitTypeForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="itemDialogVisible" :title="itemEditingId ? '编辑字典项' : '新增字典项'" width="520px">
      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="90px">
        <el-form-item label="标签" prop="itemLabel">
          <el-input v-model="itemForm.itemLabel" />
        </el-form-item>
        <el-form-item label="键值" prop="itemValue">
          <el-input v-model="itemForm.itemValue" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="itemForm.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="itemForm.status">
            <el-radio value="ENABLED">启用</el-radio>
            <el-radio value="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="itemSubmitting" @click="submitItemForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createDictItem,
  createDictType,
  deleteDictItem,
  deleteDictType,
  getDictItems,
  pageDictTypes,
  updateDictItem,
  updateDictType,
  type DictItemVO,
  type DictTypeVO,
} from '@/api/system'

const typeLoading = ref(false)
const itemLoading = ref(false)
const typeSubmitting = ref(false)
const itemSubmitting = ref(false)
const typeDialogVisible = ref(false)
const itemDialogVisible = ref(false)
const dictTypes = ref<DictTypeVO[]>([])
const dictItems = ref<DictItemVO[]>([])
const selectedType = ref<DictTypeVO>()
const typeTotal = ref(0)
const typeEditingId = ref<number>()
const itemEditingId = ref<number>()
const typeFormRef = ref<FormInstance>()
const itemFormRef = ref<FormInstance>()

const typeQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  dictType: '',
  dictName: '',
})

const typeForm = reactive<DictTypeVO>({
  dictType: '',
  dictName: '',
  status: 'ENABLED',
  remark: '',
})

const itemForm = reactive<DictItemVO>({
  dictType: '',
  itemLabel: '',
  itemValue: '',
  sortOrder: 0,
  status: 'ENABLED',
})

const typeRules: FormRules<DictTypeVO> = {
  dictType: [{ required: true, message: '请输入字典编码', trigger: 'blur' }],
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
}

const itemRules: FormRules<DictItemVO> = {
  itemLabel: [{ required: true, message: '请输入标签', trigger: 'blur' }],
  itemValue: [{ required: true, message: '请输入键值', trigger: 'blur' }],
}

async function loadTypes() {
  typeLoading.value = true
  try {
    const result = await pageDictTypes(typeQuery)
    dictTypes.value = result.records
    typeTotal.value = result.total
    if (!selectedType.value && result.records.length) {
      await handleTypeSelect(result.records[0])
    }
  } finally {
    typeLoading.value = false
  }
}

async function loadItems() {
  if (!selectedType.value?.dictType) {
    dictItems.value = []
    return
  }
  itemLoading.value = true
  try {
    dictItems.value = await getDictItems(selectedType.value.dictType, true)
  } finally {
    itemLoading.value = false
  }
}

function resetTypeQuery() {
  typeQuery.dictType = ''
  typeQuery.dictName = ''
  typeQuery.pageNum = 1
  loadTypes()
}

async function handleTypeSelect(row?: DictTypeVO) {
  selectedType.value = row
  await loadItems()
}

function resetTypeForm() {
  typeEditingId.value = undefined
  typeForm.dictType = ''
  typeForm.dictName = ''
  typeForm.status = 'ENABLED'
  typeForm.remark = ''
  typeFormRef.value?.clearValidate()
}

function resetItemForm() {
  itemEditingId.value = undefined
  itemForm.dictType = selectedType.value?.dictType || ''
  itemForm.itemLabel = ''
  itemForm.itemValue = ''
  itemForm.sortOrder = 0
  itemForm.status = 'ENABLED'
  itemFormRef.value?.clearValidate()
}

function openTypeDialog(row?: DictTypeVO) {
  resetTypeForm()
  if (row?.id) {
    typeEditingId.value = row.id
    typeForm.dictType = row.dictType
    typeForm.dictName = row.dictName
    typeForm.status = row.status
    typeForm.remark = row.remark || ''
  }
  typeDialogVisible.value = true
}

function openItemDialog(row?: DictItemVO) {
  resetItemForm()
  if (row?.id) {
    itemEditingId.value = row.id
    itemForm.dictType = row.dictType
    itemForm.itemLabel = row.itemLabel
    itemForm.itemValue = row.itemValue
    itemForm.sortOrder = row.sortOrder || 0
    itemForm.status = row.status
  }
  itemDialogVisible.value = true
}

async function submitTypeForm() {
  await typeFormRef.value?.validate()
  typeSubmitting.value = true
  try {
    if (typeEditingId.value) {
      await updateDictType(typeEditingId.value, typeForm)
      ElMessage.success('字典类型已更新')
    } else {
      await createDictType(typeForm)
      ElMessage.success('字典类型已创建')
    }
    typeDialogVisible.value = false
    await loadTypes()
  } finally {
    typeSubmitting.value = false
  }
}

async function submitItemForm() {
  await itemFormRef.value?.validate()
  itemSubmitting.value = true
  try {
    if (itemEditingId.value) {
      await updateDictItem(itemEditingId.value, itemForm)
      ElMessage.success('字典项已更新')
    } else {
      await createDictItem(itemForm)
      ElMessage.success('字典项已创建')
    }
    itemDialogVisible.value = false
    await loadItems()
  } finally {
    itemSubmitting.value = false
  }
}

async function handleDeleteType(id?: number) {
  if (!id) {
    return
  }
  await ElMessageBox.confirm('删除字典类型后将无法恢复，确定继续吗？', '提示', { type: 'warning' })
  await deleteDictType(id)
  ElMessage.success('字典类型已删除')
  if (selectedType.value?.id === id) {
    selectedType.value = undefined
    dictItems.value = []
  }
  await loadTypes()
}

async function handleDeleteItem(id?: number) {
  if (!id) {
    return
  }
  await ElMessageBox.confirm('删除字典项后将无法恢复，确定继续吗？', '提示', { type: 'warning' })
  await deleteDictItem(id)
  ElMessage.success('字典项已删除')
  await loadItems()
}

onMounted(loadTypes)
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
