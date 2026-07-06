<template>
  <section class="management-page">
    <div class="management-header">
      <div>
        <span class="card-eyebrow">项目授权</span>
        <h2>项目授权管理</h2>
      </div>

      <el-button type="primary" :icon="Plus" @click="openCreateDialog">
        新增授权
      </el-button>
    </div>

    <div class="surface-panel table-panel">
      <div class="list-filter-bar">
        <el-input
          v-model.trim="queryForm.keyword"
          class="list-filter-input"
          clearable
          placeholder="搜索员工或项目"
          @input="resetCurrentPage"
          @clear="resetCurrentPage"
        />
        <el-select
          v-model="queryForm.authStatus"
          class="list-filter-select"
          clearable
          placeholder="全部授权状态"
          @change="resetCurrentPage"
          @clear="resetCurrentPage"
        >
          <el-option label="有效授权" :value="1" />
          <el-option label="取消授权" :value="0" />
        </el-select>
        <el-select
          v-model="queryForm.projectStatus"
          class="list-filter-select"
          clearable
          placeholder="全部项目状态"
          @change="resetCurrentPage"
          @clear="resetCurrentPage"
        >
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </div>

      <div class="table-toolbar batch-toolbar">
        <span>共 {{ filteredAuthList.length }} 条授权记录</span>
        <div>
          <el-button :disabled="selectedAuthList.length === 0" @click="clearSelection">
            清除选择
          </el-button>
          <el-button
            type="primary"
            :disabled="selectedAuthList.length === 0"
            :loading="batchLoading"
            @click="handleBatchEnable"
          >
            批量设为有效
          </el-button>
          <el-button
            type="danger"
            :disabled="selectedAuthList.length === 0"
            :loading="batchLoading"
            @click="handleBatchCancel"
          >
            批量取消授权
          </el-button>
        </div>
        <strong v-if="selectedAuthList.length > 0">已选 {{ selectedAuthList.length }} 条</strong>
      </div>

      <el-table
        ref="authTableRef"
        v-loading="loading"
        :data="pagedAuthList"
        border
        stripe
        row-key="authId"
        empty-text="暂无授权数据"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="48" />
        <el-table-column prop="authId" label="授权编号" width="110" />
        <el-table-column prop="userName" label="员工姓名" min-width="120" />
        <el-table-column prop="userDeptName" label="员工部门" min-width="160" />
        <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="projectDeptName" label="项目部门" min-width="160" />
        <el-table-column label="项目状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.projectStatus === 1 ? 'success' : 'info'" effect="plain">
              {{ row.projectStatus === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="授权状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.authStatus === 1 ? 'success' : 'info'" effect="plain">
              {{ getAuthStatusText(row.authStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="授权时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.authTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" class-name="table-action-cell">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEditDialog(row)">修改状态</el-button>
            <el-button
              link
              type="danger"
              :disabled="row.authStatus === 0"
              @click="handleCancelAuth(row)"
            >
              取消授权
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="filteredAuthList.length"
          background
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="86px">
        <el-form-item v-if="!editingAuthId" label="员工" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择本部门员工" filterable>
            <el-option
              v-for="user in employeeOptions"
              :key="user.userId"
              :label="`${user.userName}（${user.phone}）`"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>

        <el-form-item v-if="!editingAuthId" label="项目" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择本部门项目" filterable>
            <el-option
              v-for="project in projectOptions"
              :key="project.projectId"
              :disabled="project.projectStatus === 0 && form.authStatus === 1"
              :label="`${project.projectName}（${project.projectStatus === 1 ? '启用' : '禁用'}）`"
              :value="project.projectId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="授权状态" prop="authStatus">
          <el-select v-model="form.authStatus" placeholder="请选择授权状态">
            <el-option label="有效授权" :value="1" />
            <el-option label="取消授权" :value="0" />
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
import { listProjectsApi } from '../api/projects'
import {
  cancelUserProjectApi,
  createUserProjectApi,
  listUserProjectsApi,
  updateUserProjectApi,
} from '../api/userProjects'
import { listUsersApi } from '../api/users'
import { useAuthStore } from '../stores/auth'
import { ROLE_EMPLOYEE } from '../utils/role'
import { normalizeSearchText, paginateList } from '../utils/table'

const authStore = useAuthStore()
const loading = ref(false)
const submitLoading = ref(false)
const batchLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const authTableRef = ref()
const authList = ref([])
const selectedAuthList = ref([])
const userList = ref([])
const projectList = ref([])
const editingAuthId = ref(null)
const queryForm = reactive({
  keyword: '',
  authStatus: null,
  projectStatus: null,
})
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
})

// 表单数据：字段名和后端 UserProjectCreateDTO / UserProjectUpdateDTO 保持一致。
const form = reactive({
  userId: null,
  projectId: null,
  authStatus: 1,
})

const rules = {
  userId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
  authStatus: [{ required: true, message: '请选择授权状态', trigger: 'change' }],
}

const dialogTitle = computed(() => (editingAuthId.value ? '修改授权状态' : '新增授权'))
const filteredAuthList = computed(() => {
  const keyword = normalizeSearchText(queryForm.keyword)

  return authList.value.filter((item) => {
    const matchKeyword = !keyword || [
      item.authId,
      item.userName,
      item.userDeptName,
      item.projectName,
      item.projectDeptName,
    ].some((value) => normalizeSearchText(value).includes(keyword))
    const matchAuthStatus = queryForm.authStatus === null || item.authStatus === queryForm.authStatus
    const matchProjectStatus = queryForm.projectStatus === null || item.projectStatus === queryForm.projectStatus
    return matchKeyword && matchAuthStatus && matchProjectStatus
  })
})
const pagedAuthList = computed(() =>
  paginateList(filteredAuthList.value, pagination.currentPage, pagination.pageSize),
)

// 员工下拉框：只保留当前经理部门的普通员工。
const employeeOptions = computed(() =>
  userList.value.filter(
    (user) => user.userRole === ROLE_EMPLOYEE && user.deptId === authStore.user?.deptId,
  ),
)

// 项目下拉框：只保留当前经理部门项目。
const projectOptions = computed(() =>
  projectList.value.filter((project) => project.deptId === authStore.user?.deptId),
)

onMounted(() => {
  loadPageData()
})

async function loadPageData() {
  loading.value = true

  try {
    const [auths, users, projects] = await Promise.all([
      listUserProjectsApi(),
      listUsersApi(),
      listProjectsApi(),
    ])
    authList.value = auths
    selectedAuthList.value = []
    userList.value = users
    projectList.value = projects
  } finally {
    loading.value = false
  }
}

function resetCurrentPage() {
  pagination.currentPage = 1
}

function resetForm() {
  editingAuthId.value = null
  form.userId = null
  form.projectId = null
  form.authStatus = 1
  formRef.value?.clearValidate()
}

function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  editingAuthId.value = row.authId
  form.userId = row.userId
  form.projectId = row.projectId
  form.authStatus = row.authStatus
  dialogVisible.value = true
}

function handleSelectionChange(selection) {
  selectedAuthList.value = selection
}

function clearSelection() {
  authTableRef.value?.clearSelection()
  selectedAuthList.value = []
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true

  try {
    if (editingAuthId.value) {
      await updateUserProjectApi(editingAuthId.value, {
        authStatus: form.authStatus,
      })
      ElMessage.success('授权状态修改成功')
    } else {
      await createUserProjectApi({
        userId: form.userId,
        projectId: form.projectId,
        authStatus: form.authStatus,
      })
      ElMessage.success('项目授权新增成功')
    }

    dialogVisible.value = false
    await loadPageData()
  } finally {
    submitLoading.value = false
  }
}

async function handleCancelAuth(row) {
  try {
    await ElMessageBox.confirm(
      `确定取消「${row.userName}」对「${row.projectName}」的项目授权吗？`,
      '取消授权确认',
      {
        confirmButtonText: '取消授权',
        cancelButtonText: '返回',
        type: 'warning',
      },
    )
  } catch {
    return
  }

  await cancelUserProjectApi(row.authId)
  ElMessage.success('已取消授权')
  await loadPageData()
}

async function handleBatchEnable() {
  if (selectedAuthList.value.length === 0) {
    ElMessage.warning('请先选择需要处理的授权记录')
    return
  }

  const disabledProjectAuth = selectedAuthList.value.find((item) => item.projectStatus === 0)
  if (disabledProjectAuth) {
    ElMessage.warning('选中的记录包含禁用项目，不能批量设为有效授权')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定将选中的 ${selectedAuthList.value.length} 条授权设为有效吗？`,
      '批量设为有效',
      {
        confirmButtonText: '设为有效',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )
  } catch {
    return
  }

  batchLoading.value = true

  try {
    // 批量授权复用单条修改接口，后端仍会校验部门范围和业务规则。
    await Promise.all(
      selectedAuthList.value.map((item) =>
        updateUserProjectApi(item.authId, {
          authStatus: 1,
        }),
      ),
    )
    ElMessage.success(`${selectedAuthList.value.length} 条授权已设为有效`)
    clearSelection()
    await loadPageData()
  } finally {
    batchLoading.value = false
  }
}

async function handleBatchCancel() {
  if (selectedAuthList.value.length === 0) {
    ElMessage.warning('请先选择需要处理的授权记录')
    return
  }

  const validAuthList = selectedAuthList.value.filter((item) => item.authStatus !== 0)
  if (validAuthList.length === 0) {
    ElMessage.warning('选中的授权记录已经全部取消')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定批量取消选中的 ${validAuthList.length} 条有效授权吗？`,
      '批量取消授权',
      {
        confirmButtonText: '取消授权',
        cancelButtonText: '返回',
        type: 'warning',
      },
    )
  } catch {
    return
  }

  batchLoading.value = true

  try {
    await Promise.all(validAuthList.map((item) => cancelUserProjectApi(item.authId)))
    ElMessage.success(`${validAuthList.length} 条授权已取消`)
    clearSelection()
    await loadPageData()
  } finally {
    batchLoading.value = false
  }
}

function getAuthStatusText(authStatus) {
  return authStatus === 1 ? '有效授权' : '取消授权'
}

function formatDateTime(value) {
  if (!value) {
    return '-'
  }

  return String(value).replace('T', ' ')
}
</script>
