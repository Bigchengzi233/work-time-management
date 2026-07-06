<template>
  <section class="management-page">
    <div class="management-header">
      <div>
        <span class="card-eyebrow">基础数据</span>
        <h2>部门管理</h2>
        <p>维护系统中的部门基础信息，用户和项目会关联到部门。</p>
      </div>

      <el-button type="primary" :icon="Plus" @click="openCreateDialog">
        新增部门
      </el-button>
    </div>

    <div class="surface-panel table-panel">
      <el-table
        v-loading="loading"
        :data="departmentList"
        border
        stripe
        empty-text="暂无部门数据"
      >
        <el-table-column prop="deptId" label="部门编号" width="120" />
        <el-table-column prop="deptName" label="部门名称" min-width="220" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="420px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="82px">
        <el-form-item label="部门名称" prop="deptName">
          <el-input
            v-model.trim="form.deptName"
            maxlength="50"
            placeholder="请输入部门名称"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          保存
        </el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  createDepartmentApi,
  deleteDepartmentApi,
  listDepartmentsApi,
  updateDepartmentApi,
} from '../api/departments'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const departmentList = ref([])
const editingDeptId = ref(null)

// 表单数据：字段名和后端 DepartmentCreateDTO / DepartmentUpdateDTO 保持一致。
const form = reactive({
  deptName: '',
})

// 前端基础校验：后端仍会再次校验，前端校验主要提升操作体验。
const rules = {
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { max: 50, message: '部门名称长度不能超过 50 个字符', trigger: 'blur' },
  ],
}

const dialogTitle = computed(() => (editingDeptId.value ? '编辑部门' : '新增部门'))

onMounted(() => {
  loadDepartments()
})

async function loadDepartments() {
  loading.value = true

  try {
    departmentList.value = await listDepartmentsApi()
  } finally {
    loading.value = false
  }
}

function resetForm() {
  editingDeptId.value = null
  form.deptName = ''
  formRef.value?.clearValidate()
}

function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  editingDeptId.value = row.deptId
  form.deptName = row.deptName
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true

  try {
    if (editingDeptId.value) {
      await updateDepartmentApi(editingDeptId.value, {
        deptName: form.deptName,
      })
      ElMessage.success('部门修改成功')
    } else {
      await createDepartmentApi({
        deptName: form.deptName,
      })
      ElMessage.success('部门新增成功')
    }

    dialogVisible.value = false
    await loadDepartments()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定删除部门「${row.deptName}」吗？如果部门下已有用户或项目，后端会阻止删除。`,
      '删除确认',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )
  } catch {
    return
  }

  await deleteDepartmentApi(row.deptId)
  ElMessage.success('部门删除成功')
  await loadDepartments()
}
</script>
