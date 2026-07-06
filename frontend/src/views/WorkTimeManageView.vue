<template>
  <section v-if="authStore.userRole !== ROLE_EMPLOYEE" class="surface-panel placeholder-panel">
    <span class="card-eyebrow">工时管理</span>
    <h2>经理审批功能待接入</h2>
    <p>当前阶段先完成员工工时填报。部门经理审批页面会在下一阶段实现。</p>
  </section>

  <section v-else class="management-page">
    <div class="management-header">
      <div>
        <span class="card-eyebrow">工时管理</span>
        <h2>我的工时</h2>
        <p>填报本人已授权项目的每日工时，并提交给部门经理审批。</p>
      </div>

      <el-button type="primary" :icon="Plus" @click="openCreateDialog">
        新增工时
      </el-button>
    </div>

    <div class="surface-panel table-panel">
      <el-table v-loading="loading" :data="workTimeList" border stripe empty-text="暂无工时数据">
        <el-table-column prop="workId" label="工时编号" width="110" />
        <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="workDate" label="工作日期" width="130" />
        <el-table-column prop="workHours" label="工时数" width="100" />
        <el-table-column prop="workDesc" label="工作描述" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" effect="plain">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right" class-name="table-action-cell">
          <template #default="{ row }">
            <el-button link type="primary" :disabled="!canEdit(row)" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button link type="primary" :disabled="!canSubmit(row)" @click="handleSubmitWorkTime(row)">
              提交
            </el-button>
            <el-button link type="danger" :disabled="!canDelete(row)" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="86px">
        <el-form-item label="项目" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择已授权项目" filterable>
            <el-option
              v-for="project in projectOptions"
              :key="project.projectId"
              :label="project.projectName"
              :value="project.projectId"
            />
          </el-select>
        </el-form-item>

        <div class="form-grid">
          <el-form-item label="工作日期" prop="workDate">
            <el-date-picker
              v-model="form.workDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择工作日期"
              :disabled-date="disableFutureDate"
            />
          </el-form-item>

          <el-form-item label="工时数" prop="workHours">
            <el-input-number
              v-model="form.workHours"
              :min="0.5"
              :max="24"
              :step="0.5"
              :precision="1"
              controls-position="right"
            />
          </el-form-item>
        </div>

        <el-form-item label="工作描述" prop="workDesc">
          <el-input
            v-model.trim="form.workDesc"
            type="textarea"
            maxlength="500"
            :rows="4"
            placeholder="请输入工作内容，可为空"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSave">
          保存草稿
        </el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { listUserProjectsByUserIdApi } from '../api/userProjects'
import {
  createWorkTimeApi,
  deleteWorkTimeApi,
  listWorkTimesByUserIdApi,
  submitWorkTimeApi,
  updateWorkTimeApi,
} from '../api/workTimes'
import { useAuthStore } from '../stores/auth'
import { ROLE_EMPLOYEE } from '../utils/role'

const STATUS_DRAFT = 0
const STATUS_PENDING = 1
const STATUS_APPROVED = 2
const STATUS_REJECTED = 3

const authStore = useAuthStore()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const workTimeList = ref([])
const userProjectList = ref([])
const editingWorkId = ref(null)

// 表单数据：字段名和后端 WorkTimeCreateDTO / WorkTimeUpdateDTO 保持一致。
const form = reactive({
  projectId: null,
  workDate: '',
  workHours: 8,
  workDesc: '',
})

const rules = {
  projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
  workDate: [{ required: true, message: '请选择工作日期', trigger: 'change' }],
  workHours: [
    { required: true, message: '请输入工时数', trigger: 'blur' },
    { type: 'number', min: 0.5, max: 24, message: '工时数范围为 0.5 到 24', trigger: 'blur' },
  ],
  workDesc: [{ max: 500, message: '工作描述不能超过 500 个字符', trigger: 'blur' }],
}

const dialogTitle = computed(() => (editingWorkId.value ? '编辑工时' : '新增工时'))

// 项目下拉框：只展示当前员工有效授权且项目启用的项目。
const projectOptions = computed(() =>
  userProjectList.value
    .filter((item) => item.authStatus === 1 && item.projectStatus === 1)
    .map((item) => ({
      projectId: item.projectId,
      projectName: item.projectName,
    })),
)

onMounted(() => {
  if (authStore.userRole === ROLE_EMPLOYEE) {
    loadPageData()
  }
})

async function loadPageData() {
  loading.value = true

  try {
    const userId = authStore.user.userId
    const [workTimes, userProjects] = await Promise.all([
      listWorkTimesByUserIdApi(userId),
      listUserProjectsByUserIdApi(userId),
    ])
    workTimeList.value = workTimes.filter((item) => item.isDeleted === 0)
    userProjectList.value = userProjects
  } finally {
    loading.value = false
  }
}

function resetForm() {
  editingWorkId.value = null
  form.projectId = null
  form.workDate = ''
  form.workHours = 8
  form.workDesc = ''
  formRef.value?.clearValidate()
}

function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  editingWorkId.value = row.workId
  form.projectId = row.projectId
  form.workDate = row.workDate
  form.workHours = Number(row.workHours)
  form.workDesc = row.workDesc || ''
  dialogVisible.value = true
}

async function handleSave() {
  await formRef.value.validate()
  submitLoading.value = true

  const payload = {
    userId: authStore.user.userId,
    projectId: form.projectId,
    workDate: form.workDate,
    workHours: form.workHours,
    workDesc: form.workDesc,
  }

  try {
    if (editingWorkId.value) {
      await updateWorkTimeApi(editingWorkId.value, {
        projectId: payload.projectId,
        workDate: payload.workDate,
        workHours: payload.workHours,
        workDesc: payload.workDesc,
      })
      ElMessage.success('工时修改成功')
    } else {
      await createWorkTimeApi(payload)
      ElMessage.success('工时草稿新增成功')
    }

    dialogVisible.value = false
    await loadPageData()
  } finally {
    submitLoading.value = false
  }
}

async function handleSubmitWorkTime(row) {
  try {
    await ElMessageBox.confirm(`确定提交工时编号 ${row.workId} 进入审批吗？`, '提交审批确认', {
      confirmButtonText: '提交审批',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }

  await submitWorkTimeApi(row.workId)
  ElMessage.success('已提交审批')
  await loadPageData()
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除工时编号 ${row.workId} 吗？删除后将不在列表显示。`, '删除确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }

  await deleteWorkTimeApi(row.workId, authStore.user.userId)
  ElMessage.success('工时删除成功')
  await loadPageData()
}

function canEdit(row) {
  return row.status === STATUS_DRAFT || row.status === STATUS_REJECTED
}

function canDelete(row) {
  return row.status === STATUS_DRAFT || row.status === STATUS_REJECTED
}

function canSubmit(row) {
  return row.status === STATUS_DRAFT
}

function getStatusText(status) {
  const statusMap = {
    [STATUS_DRAFT]: '草稿',
    [STATUS_PENDING]: '待审批',
    [STATUS_APPROVED]: '审批通过',
    [STATUS_REJECTED]: '已驳回',
  }

  return statusMap[status] || '未知状态'
}

function getStatusTagType(status) {
  const typeMap = {
    [STATUS_DRAFT]: 'info',
    [STATUS_PENDING]: 'warning',
    [STATUS_APPROVED]: 'success',
    [STATUS_REJECTED]: 'danger',
  }

  return typeMap[status] || 'info'
}

function disableFutureDate(date) {
  return date.getTime() > Date.now()
}
</script>
