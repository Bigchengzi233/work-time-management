<template>
  <section class="management-page nested-management-page">
    <div class="management-header compact-management-header">
      <div>
        <span class="card-eyebrow">基础数据</span>
        <h2>项目管理</h2>
      </div>

      <el-button type="primary" :icon="Plus" @click="openCreateDialog">
        新增项目
      </el-button>
    </div>

    <div class="surface-panel table-panel">
      <div class="list-filter-bar">
        <el-input
          v-model.trim="queryForm.keyword"
          class="list-filter-input"
          clearable
          placeholder="搜索项目编号或名称"
          @input="resetCurrentPage"
          @clear="resetCurrentPage"
        />
        <el-select
          v-model="queryForm.projectStatus"
          class="list-filter-select"
          clearable
          placeholder="全部状态"
          @change="resetCurrentPage"
          @clear="resetCurrentPage"
        >
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-select
          v-model="queryForm.deptId"
          class="list-filter-select"
          clearable
          filterable
          placeholder="全部部门"
          @change="resetCurrentPage"
          @clear="resetCurrentPage"
        >
          <el-option
            v-for="department in departmentList"
            :key="department.deptId"
            :label="department.deptName"
            :value="department.deptId"
          />
        </el-select>
      </div>

      <div class="table-toolbar">
        <span>共 {{ filteredProjectList.length }} 条项目记录</span>
      </div>

      <el-table v-loading="loading" :data="pagedProjectList" border stripe empty-text="暂无项目数据">
        <el-table-column prop="projectId" label="项目编号" width="110" />
        <el-table-column prop="projectName" label="项目名称" min-width="220" show-overflow-tooltip />
        <el-table-column label="项目状态" width="130">
          <template #default="{ row }">
            <el-switch
              v-model="row.projectStatus"
              :active-value="1"
              :inactive-value="0"
              active-text="启用"
              inactive-text="禁用"
              inline-prompt
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="deptName" label="所属部门" min-width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="filteredProjectList.length"
          background
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="86px">
        <el-form-item label="项目名称" prop="projectName">
          <el-input
            v-model.trim="form.projectName"
            maxlength="100"
            placeholder="请输入项目名称"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="项目状态" prop="projectStatus">
          <el-select v-model="form.projectStatus" placeholder="请选择项目状态">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属部门" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择部门" filterable>
            <el-option
              v-for="department in departmentList"
              :key="department.deptId"
              :label="department.deptName"
              :value="department.deptId"
            />
          </el-select>
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
import { listDepartmentsApi } from '../api/departments'
import {
  createProjectApi,
  deleteProjectApi,
  listProjectsApi,
  updateProjectApi,
} from '../api/projects'
import { normalizeSearchText, paginateList } from '../utils/table'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const projectList = ref([])
const departmentList = ref([])
const editingProjectId = ref(null)
const queryForm = reactive({
  keyword: '',
  projectStatus: null,
  deptId: null,
})
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
})

// 表单数据：字段名和后端 ProjectCreateDTO / ProjectUpdateDTO 保持一致。
const form = reactive({
  projectName: '',
  projectStatus: 1,
  deptId: null,
})

// 前端基础校验：后端仍会再次校验，前端主要负责提前给用户提示。
const rules = {
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { max: 100, message: '项目名称长度不能超过 100 个字符', trigger: 'blur' },
  ],
  projectStatus: [{ required: true, message: '请选择项目状态', trigger: 'change' }],
  deptId: [{ required: true, message: '请选择所属部门', trigger: 'change' }],
}

const dialogTitle = computed(() => (editingProjectId.value ? '编辑项目' : '新增项目'))
const filteredProjectList = computed(() => {
  const keyword = normalizeSearchText(queryForm.keyword)

  return projectList.value.filter((item) => {
    const matchKeyword = !keyword || [item.projectId, item.projectName, item.deptName]
      .some((value) => normalizeSearchText(value).includes(keyword))
    const matchStatus = queryForm.projectStatus === null || item.projectStatus === queryForm.projectStatus
    const matchDept = !queryForm.deptId || item.deptId === queryForm.deptId
    return matchKeyword && matchStatus && matchDept
  })
})
const pagedProjectList = computed(() =>
  paginateList(filteredProjectList.value, pagination.currentPage, pagination.pageSize),
)

onMounted(() => {
  loadPageData()
})

async function loadPageData() {
  loading.value = true

  try {
    const [projects, departments] = await Promise.all([listProjectsApi(), listDepartmentsApi()])
    projectList.value = projects
    departmentList.value = departments
  } finally {
    loading.value = false
  }
}

function resetCurrentPage() {
  pagination.currentPage = 1
}

function resetForm() {
  editingProjectId.value = null
  form.projectName = ''
  form.projectStatus = 1
  form.deptId = null
  formRef.value?.clearValidate()
}

function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  editingProjectId.value = row.projectId
  form.projectName = row.projectName
  form.projectStatus = row.projectStatus
  form.deptId = row.deptId
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true

  const payload = {
    projectName: form.projectName,
    projectStatus: form.projectStatus,
    deptId: form.deptId,
  }

  try {
    if (editingProjectId.value) {
      await updateProjectApi(editingProjectId.value, payload)
      ElMessage.success('项目修改成功')
    } else {
      await createProjectApi(payload)
      ElMessage.success('项目新增成功')
    }

    dialogVisible.value = false
    await loadPageData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定删除项目「${row.projectName}」吗？如果项目已有授权或工时数据，后端会阻止删除。`,
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

  await deleteProjectApi(row.projectId)
  ElMessage.success('项目删除成功')
  await loadPageData()
}

async function handleStatusChange(row) {
  try {
    await updateProjectApi(row.projectId, {
      projectName: row.projectName,
      projectStatus: row.projectStatus,
      deptId: row.deptId,
    })
    ElMessage.success(`项目已${getProjectStatusText(row.projectStatus)}`)
  } catch (error) {
    await loadPageData()
    throw error
  }
}

function getProjectStatusText(projectStatus) {
  return projectStatus === 1 ? '启用' : '禁用'
}
</script>
