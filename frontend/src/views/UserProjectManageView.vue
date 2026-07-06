<template>
  <section class="management-page">
    <div class="management-header">
      <div>
        <span class="card-eyebrow">项目授权</span>
        <h2>项目授权管理</h2>
        <p>为本部门员工分配可填报工时的项目，禁用项目不能设置为有效授权。</p>
      </div>

      <el-button type="primary" :icon="Plus" @click="openCreateDialog">
        新增授权
      </el-button>
    </div>

    <div class="surface-panel table-panel">
      <el-table v-loading="loading" :data="authList" border stripe empty-text="暂无授权数据">
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

const authStore = useAuthStore()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const authList = ref([])
const userList = ref([])
const projectList = ref([])
const editingAuthId = ref(null)

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
    userList.value = users
    projectList.value = projects
  } finally {
    loading.value = false
  }
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
