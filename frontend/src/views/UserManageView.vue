<template>
  <section class="management-page nested-management-page">
    <div class="management-header compact-management-header">
      <div>
        <span class="card-eyebrow">基础数据</span>
        <h2>用户管理</h2>
      </div>

      <el-button type="primary" :icon="Plus" @click="openCreateDialog">
        新增用户
      </el-button>
    </div>

    <div class="surface-panel table-panel">
      <div class="list-filter-bar">
        <el-input
          v-model.trim="queryForm.keyword"
          class="list-filter-input"
          clearable
          placeholder="搜索姓名、手机号或邮箱"
          @input="resetCurrentPage"
          @clear="resetCurrentPage"
        />
        <el-select
          v-model="queryForm.userRole"
          class="list-filter-select"
          clearable
          placeholder="全部角色"
          @change="resetCurrentPage"
          @clear="resetCurrentPage"
        >
          <el-option label="管理员" :value="ROLE_ADMIN" />
          <el-option label="部门经理" :value="ROLE_MANAGER" />
          <el-option label="员工" :value="ROLE_EMPLOYEE" />
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
        <el-select
          v-model="queryForm.employmentStatus"
          class="list-filter-select"
          clearable
          placeholder="全部在职状态"
          @change="resetCurrentPage"
          @clear="resetCurrentPage"
        >
          <el-option label="在职" :value="EMPLOYMENT_ACTIVE" />
          <el-option label="离职" :value="EMPLOYMENT_INACTIVE" />
        </el-select>
      </div>

      <div class="table-toolbar">
        <span>共 {{ filteredUserList.length }} 条用户记录</span>
      </div>

      <el-table v-loading="loading" :data="pagedUserList" border stripe empty-text="暂无用户数据">
        <el-table-column prop="userId" label="用户编号" width="110" />
        <el-table-column prop="userName" label="用户姓名" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.userRole)" effect="plain">
              {{ getRoleName(row.userRole) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deptName" label="所属部门" min-width="160" />
        <el-table-column label="在职状态" width="150">
          <template #default="{ row }">
            <el-switch
              v-model="row.employmentStatus"
              :active-value="EMPLOYMENT_ACTIVE"
              :inactive-value="EMPLOYMENT_INACTIVE"
              active-text="在职"
              inactive-text="离职"
              inline-prompt
              :disabled="row.userRole === ROLE_ADMIN"
              @change="handleEmploymentStatusChange(row)"
            />
          </template>
        </el-table-column>
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
          :total="filteredUserList.length"
          background
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="86px">
        <div class="form-grid">
          <el-form-item label="用户姓名" prop="userName">
            <el-input
              v-model.trim="form.userName"
              maxlength="50"
              placeholder="请输入用户姓名"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model.trim="form.phone"
              maxlength="11"
              placeholder="请输入手机号"
              clearable
            />
          </el-form-item>

          <el-form-item v-if="!editingUserId" label="初始密码" prop="password">
            <el-input
              v-model="form.password"
              maxlength="50"
              placeholder="默认初始密码为 123456"
              show-password
              type="password"
            />
          </el-form-item>

          <el-form-item v-else class="password-reset-form-item" label="重置密码">
            <div class="password-reset-panel">
              <div>
                <strong>重置为 123456</strong>
                <span>员工忘记密码时，管理员可一键恢复初始密码。</span>
              </div>
              <el-button :loading="resetPasswordLoading" @click="handleResetPassword">
                重置密码
              </el-button>
            </div>
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input
              v-model.trim="form.email"
              maxlength="100"
              placeholder="请输入邮箱，可为空"
              clearable
            />
          </el-form-item>

          <el-form-item label="角色" prop="userRole">
            <el-select v-model="form.userRole" placeholder="请选择角色">
              <el-option
                v-for="role in assignableRoleOptions"
                :key="role.value"
                :label="role.label"
                :value="role.value"
                :disabled="role.disabled"
              />
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

          <el-form-item label="在职状态">
            <el-radio-group v-model="form.employmentStatus" :disabled="form.userRole === ROLE_ADMIN">
              <el-radio-button :label="EMPLOYMENT_ACTIVE">在职</el-radio-button>
              <el-radio-button :label="EMPLOYMENT_INACTIVE">离职</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </div>
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
  createUserApi,
  deleteUserApi,
  listUserEmploymentStatusesApi,
  listUsersApi,
  resetUserPasswordApi,
  updateUserApi,
  updateUserEmploymentStatusApi,
} from '../api/users'
import { getRoleName, ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_MANAGER } from '../utils/role'
import { normalizeSearchText, paginateList } from '../utils/table'

const loading = ref(false)
const submitLoading = ref(false)
const resetPasswordLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const userList = ref([])
const departmentList = ref([])
const editingUserId = ref(null)
const queryForm = reactive({
  keyword: '',
  userRole: '',
  deptId: null,
  employmentStatus: '',
})
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
})
const INITIAL_PASSWORD = '123456'
const EMPLOYMENT_ACTIVE = 'active'
const EMPLOYMENT_INACTIVE = 'inactive'

// 表单数据：字段名和后端 UserCreateDTO / UserUpdateDTO 保持一致。
const form = reactive({
  userName: '',
  phone: '',
  password: '',
  email: '',
  userRole: '',
  deptId: null,
  employmentStatus: EMPLOYMENT_ACTIVE,
})

// 密码规则：新增用户必须填写密码；编辑用户时密码可为空，空字符串表示不修改密码。
function validatePassword(rule, value, callback) {
  if (!editingUserId.value && !value) {
    callback(new Error('请输入初始密码'))
    return
  }

  if (value && (value.length < 6 || value.length > 50)) {
    callback(new Error('密码长度应为 6 到 50 个字符'))
    return
  }

  callback()
}

// 邮箱规则：允许为空；如果填写，则必须符合基础邮箱格式。
function validateEmail(rule, value, callback) {
  if (!value) {
    callback()
    return
  }

  const emailRegExp = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegExp.test(value)) {
    callback(new Error('邮箱格式不正确'))
    return
  }

  callback()
}

const rules = {
  userName: [
    { required: true, message: '请输入用户姓名', trigger: 'blur' },
    { max: 50, message: '用户姓名长度不能超过 50 个字符', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  password: [{ validator: validatePassword, trigger: 'blur' }],
  email: [
    { max: 100, message: '邮箱长度不能超过 100 个字符', trigger: 'blur' },
    { validator: validateEmail, trigger: 'blur' },
  ],
  userRole: [{ required: true, message: '请选择角色', trigger: 'change' }],
  deptId: [{ required: true, message: '请选择所属部门', trigger: 'change' }],
}

const dialogTitle = computed(() => (editingUserId.value ? '编辑用户' : '新增用户'))
// 用户管理里不能再新增管理员，系统只保留初始化时的唯一管理员。
// 如果正在编辑现有管理员，则展示“管理员”但禁用它，避免把管理员角色改掉。
const assignableRoleOptions = computed(() => {
  const options = [
    { label: '部门经理', value: ROLE_MANAGER },
    { label: '员工', value: ROLE_EMPLOYEE },
  ]

  if (editingUserId.value && form.userRole === ROLE_ADMIN) {
    return [
      { label: '管理员', value: ROLE_ADMIN, disabled: true },
      ...options,
    ]
  }

  return options
})
const filteredUserList = computed(() => {
  const keyword = normalizeSearchText(queryForm.keyword)

  return userList.value.filter((item) => {
    const matchKeyword = !keyword || [item.userName, item.phone, item.email, item.deptName]
      .some((value) => normalizeSearchText(value).includes(keyword))
    const matchRole = queryForm.userRole === '' || item.userRole === queryForm.userRole
    const matchDept = !queryForm.deptId || item.deptId === queryForm.deptId
    const matchEmploymentStatus = !queryForm.employmentStatus || item.employmentStatus === queryForm.employmentStatus
    return matchKeyword && matchRole && matchDept && matchEmploymentStatus
  })
})
const pagedUserList = computed(() =>
  paginateList(filteredUserList.value, pagination.currentPage, pagination.pageSize),
)

onMounted(() => {
  loadPageData()
})

async function loadPageData() {
  loading.value = true

  try {
    const [users, departments, employmentStatusMap] = await Promise.all([
      listUsersApi(),
      listDepartmentsApi(),
      listUserEmploymentStatusesApi(),
    ])
    userList.value = users.map((user) => appendEmploymentStatus(user, employmentStatusMap))
    departmentList.value = departments
  } finally {
    loading.value = false
  }
}

function resetCurrentPage() {
  pagination.currentPage = 1
}

function resetForm() {
  editingUserId.value = null
  form.userName = ''
  form.phone = ''
  form.password = INITIAL_PASSWORD
  form.email = ''
  form.userRole = ROLE_EMPLOYEE
  form.deptId = null
  form.employmentStatus = EMPLOYMENT_ACTIVE
  formRef.value?.clearValidate()
}

function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  editingUserId.value = row.userId
  form.userName = row.userName
  form.phone = row.phone
  form.password = ''
  form.email = row.email || ''
  form.userRole = row.userRole
  form.deptId = row.deptId
  form.employmentStatus = row.employmentStatus || EMPLOYMENT_ACTIVE
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true

  const payload = {
    userName: form.userName,
    phone: form.phone,
    password: editingUserId.value ? '' : form.password,
    email: form.email,
    userRole: form.userRole,
    deptId: form.deptId,
  }

  try {
    if (editingUserId.value) {
      await updateUserApi(editingUserId.value, payload)
      await updateUserEmploymentStatusApi(editingUserId.value, form.employmentStatus)
      ElMessage.success('用户修改成功')
    } else {
      const createdUser = await createUserApi(payload)
      if (createdUser?.userId) {
        await updateUserEmploymentStatusApi(createdUser.userId, form.employmentStatus)
      }
      ElMessage.success('用户新增成功')
    }

    dialogVisible.value = false
    await loadPageData()
  } finally {
    submitLoading.value = false
  }
}

async function handleResetPassword() {
  if (!editingUserId.value) {
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定将用户「${form.userName}」的登录密码重置为 ${INITIAL_PASSWORD} 吗？`,
      '重置密码确认',
      {
        confirmButtonText: '重置密码',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )
  } catch {
    return
  }

  resetPasswordLoading.value = true

  try {
    await resetUserPasswordApi(editingUserId.value)
    ElMessage.success(`密码已重置为 ${INITIAL_PASSWORD}`)
  } finally {
    resetPasswordLoading.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定删除用户「${row.userName}」吗？如果用户已有工时报单，后端会阻止删除。`,
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

  await deleteUserApi(row.userId)
  ElMessage.success('用户删除成功')
  await loadPageData()
}

function getRoleTagType(userRole) {
  const tagTypeMap = {
    [ROLE_ADMIN]: 'danger',
    [ROLE_MANAGER]: 'warning',
    [ROLE_EMPLOYEE]: 'info',
  }

  return tagTypeMap[userRole] || 'info'
}

function appendEmploymentStatus(user, statusMap = {}) {
  return {
    ...user,
    employmentStatus: statusMap[user.userId] || EMPLOYMENT_ACTIVE,
  }
}

async function handleEmploymentStatusChange(row) {
  const nextStatus = row.employmentStatus || EMPLOYMENT_ACTIVE
  const previousStatus = nextStatus === EMPLOYMENT_ACTIVE ? EMPLOYMENT_INACTIVE : EMPLOYMENT_ACTIVE

  try {
    await updateUserEmploymentStatusApi(row.userId, nextStatus)
    ElMessage.success(`已将「${row.userName}」标记为${nextStatus === EMPLOYMENT_ACTIVE ? '在职' : '离职'}`)
  } catch {
    row.employmentStatus = previousStatus
  }
}
</script>
