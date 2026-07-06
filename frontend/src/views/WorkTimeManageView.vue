<template>
  <section v-if="authStore.userRole === ROLE_MANAGER" class="management-page">
    <div class="management-header">
      <div>
        <span class="card-eyebrow">工时审批</span>
        <h2>待审批工时</h2>
      </div>

      <el-button :icon="Refresh" @click="loadPageData">
        刷新列表
      </el-button>
    </div>

    <div class="surface-panel table-panel">
      <div class="list-filter-bar">
        <el-input
          v-model.trim="managerQueryForm.keyword"
          class="list-filter-input"
          clearable
          placeholder="搜索员工、项目或描述"
          @input="resetCurrentPage"
          @clear="resetCurrentPage"
        />
      </div>

      <div class="table-toolbar batch-toolbar">
        <span>共 {{ filteredPendingWorkTimeList.length }} 条待审批</span>
        <div>
          <el-button :disabled="selectedWorkTimeList.length === 0" @click="clearSelection">
            清除选择
          </el-button>
          <el-button
            type="primary"
            :disabled="selectedWorkTimeList.length === 0"
            @click="openBatchDialog('approve')"
          >
            批量通过
          </el-button>
          <el-button
            type="danger"
            :disabled="selectedWorkTimeList.length === 0"
            @click="openBatchDialog('reject')"
          >
            批量驳回
          </el-button>
        </div>
        <strong v-if="selectedWorkTimeList.length > 0">已选 {{ selectedWorkTimeList.length }} 条</strong>
      </div>

      <el-table
        ref="pendingTableRef"
        v-loading="loading"
        :data="pagedPendingWorkTimeList"
        border
        stripe
        row-key="workId"
        empty-text="暂无待审批工时"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="48" />
        <el-table-column prop="workId" label="工时编号" width="110" />
        <el-table-column prop="userName" label="员工姓名" width="120" />
        <el-table-column prop="userDeptName" label="员工部门" min-width="150" show-overflow-tooltip />
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
        <el-table-column label="操作" min-width="250" fixed="right" class-name="table-action-cell">
          <template #default="{ row }">
            <el-button link type="primary" @click="openApproveDialog(row)">
              审批通过
            </el-button>
            <el-button link type="danger" @click="openRejectDialog(row)">
              驳回
            </el-button>
            <el-button link type="primary" @click="openLogDialog(row)">
              日志
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="filteredPendingWorkTimeList.length"
          background
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>

    <el-dialog v-model="approvalDialogVisible" :title="approvalDialogTitle" width="580px" destroy-on-close>
      <el-form ref="approvalFormRef" :model="approvalForm" :rules="approvalRules" label-width="86px">
        <div v-if="activeWorkTime" class="approval-summary">
          <div>
            <span>员工</span>
            <strong>{{ activeWorkTime.userName }}</strong>
          </div>
          <div>
            <span>项目</span>
            <strong>{{ activeWorkTime.projectName }}</strong>
          </div>
          <div>
            <span>日期</span>
            <strong>{{ activeWorkTime.workDate }}</strong>
          </div>
          <div>
            <span>工时</span>
            <strong>{{ activeWorkTime.workHours }}</strong>
          </div>
        </div>

        <el-form-item v-if="approvalMode === 'approve'" label="审批说明" prop="operationDesc">
          <el-input
            v-model.trim="approvalForm.operationDesc"
            type="textarea"
            maxlength="500"
            :rows="4"
            placeholder="可填写审批说明，也可以留空"
            show-word-limit
          />
        </el-form-item>

        <el-form-item v-else label="驳回原因" prop="rejectReason">
          <el-input
            v-model.trim="approvalForm.rejectReason"
            type="textarea"
            maxlength="500"
            :rows="4"
            placeholder="请输入驳回原因，方便员工修改后重新提交"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="approvalDialogVisible = false">取消</el-button>
        <el-button
          :type="approvalMode === 'approve' ? 'primary' : 'danger'"
          :loading="approvalLoading"
          @click="handleApprovalSave"
        >
          {{ approvalMode === 'approve' ? '确认通过' : '确认驳回' }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="batchDialogVisible" :title="batchDialogTitle" width="580px" destroy-on-close>
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="86px">
        <div class="approval-summary">
          <div>
            <span>已选数量</span>
            <strong>{{ selectedWorkTimeList.length }} 条</strong>
          </div>
          <div>
            <span>审批人</span>
            <strong>{{ authStore.user?.userName || '-' }}</strong>
          </div>
        </div>

        <el-form-item v-if="batchMode === 'approve'" label="审批说明" prop="operationDesc">
          <el-input
            v-model.trim="batchForm.operationDesc"
            type="textarea"
            maxlength="500"
            :rows="4"
            placeholder="可填写统一审批说明，也可以留空"
            show-word-limit
          />
        </el-form-item>

        <el-form-item v-else label="驳回原因" prop="rejectReason">
          <el-input
            v-model.trim="batchForm.rejectReason"
            type="textarea"
            maxlength="500"
            :rows="4"
            placeholder="请输入统一驳回原因，所有选中工时都会使用该原因"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button
          :type="batchMode === 'approve' ? 'primary' : 'danger'"
          :loading="batchLoading"
          @click="handleBatchSave"
        >
          {{ batchMode === 'approve' ? '确认批量通过' : '确认批量驳回' }}
        </el-button>
      </template>
    </el-dialog>
  </section>

  <section v-else-if="authStore.userRole === ROLE_EMPLOYEE" class="management-page">
    <div class="management-header">
      <div>
        <span class="card-eyebrow">工时管理</span>
        <h2>我的工时</h2>
      </div>

      <el-button type="primary" :icon="Plus" @click="openCreateDialog">
        新增工时
      </el-button>
    </div>

    <div class="surface-panel table-panel">
      <div class="list-filter-bar">
        <el-select
          v-model="employeeQueryForm.projectId"
          class="list-filter-select"
          clearable
          filterable
          placeholder="全部项目"
          @change="resetCurrentPage"
          @clear="resetCurrentPage"
        >
          <el-option
            v-for="project in projectOptions"
            :key="project.projectId"
            :label="project.projectName"
            :value="project.projectId"
          />
        </el-select>
        <el-select
          v-model="employeeQueryForm.status"
          class="list-filter-select"
          clearable
          placeholder="全部状态"
          @change="resetCurrentPage"
          @clear="resetCurrentPage"
        >
          <el-option label="草稿" :value="STATUS_DRAFT" />
          <el-option label="待审批" :value="STATUS_PENDING" />
          <el-option label="审批通过" :value="STATUS_APPROVED" />
          <el-option label="已驳回" :value="STATUS_REJECTED" />
        </el-select>
        <el-date-picker
          v-model="employeeQueryForm.dateRange"
          class="list-filter-date"
          type="daterange"
          value-format="YYYY-MM-DD"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          range-separator="至"
          @change="resetCurrentPage"
        />
      </div>

      <div class="table-toolbar">
        <span>共 {{ filteredWorkTimeList.length }} 条工时记录</span>
      </div>

      <el-table v-loading="loading" :data="pagedWorkTimeList" border stripe empty-text="暂无工时数据">
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
        <el-table-column label="操作" min-width="310" fixed="right" class-name="table-action-cell">
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
            <el-button link type="primary" @click="openLogDialog(row)">
              日志
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="filteredWorkTimeList.length"
          background
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
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

  <section v-else class="surface-panel placeholder-panel">
    <span class="card-eyebrow">工时管理</span>
    <h2>暂无可用功能</h2>
    <p>当前角色暂时没有工时管理操作入口。</p>
  </section>

  <el-dialog v-model="logDialogVisible" title="工时操作日志" width="760px" destroy-on-close>
    <div v-if="activeLogWorkTime" class="approval-summary log-summary">
      <div>
        <span>工时编号</span>
        <strong>{{ activeLogWorkTime.workId }}</strong>
      </div>
      <div>
        <span>项目</span>
        <strong>{{ activeLogWorkTime.projectName }}</strong>
      </div>
      <div>
        <span>日期</span>
        <strong>{{ activeLogWorkTime.workDate }}</strong>
      </div>
      <div>
        <span>状态</span>
        <strong>{{ getStatusText(activeLogWorkTime.status) }}</strong>
      </div>
    </div>

    <el-table v-loading="logLoading" :data="logList" border stripe empty-text="暂无操作日志">
      <el-table-column prop="logId" label="日志编号" width="100" />
      <el-table-column label="操作类型" width="120">
        <template #default="{ row }">
          <el-tag :type="getLogTagType(row.operationType)" effect="plain">
            {{ getLogTypeText(row.operationType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operatorName" label="操作人" width="120" />
      <el-table-column prop="operationTime" label="操作时间" width="180" />
      <el-table-column prop="operationDesc" label="操作说明" min-width="220" show-overflow-tooltip />
    </el-table>

    <template #footer>
      <el-button @click="logDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { listUserProjectsByUserIdApi } from '../api/userProjects'
import { listWorkTimeLogsByWorkIdApi } from '../api/workTimeLogs'
import {
  approveWorkTimeApi,
  createWorkTimeApi,
  deleteWorkTimeApi,
  listPendingWorkTimesByManagerIdApi,
  listWorkTimesByUserIdApi,
  rejectWorkTimeApi,
  submitWorkTimeApi,
  updateWorkTimeApi,
} from '../api/workTimes'
import { useAuthStore } from '../stores/auth'
import { ROLE_EMPLOYEE, ROLE_MANAGER } from '../utils/role'
import { normalizeSearchText, paginateList } from '../utils/table'

const STATUS_DRAFT = 0
const STATUS_PENDING = 1
const STATUS_APPROVED = 2
const STATUS_REJECTED = 3

const authStore = useAuthStore()
const loading = ref(false)
const submitLoading = ref(false)
const approvalLoading = ref(false)
const batchLoading = ref(false)
const logLoading = ref(false)
const dialogVisible = ref(false)
const approvalDialogVisible = ref(false)
const batchDialogVisible = ref(false)
const logDialogVisible = ref(false)
const formRef = ref()
const approvalFormRef = ref()
const batchFormRef = ref()
const pendingTableRef = ref()
const workTimeList = ref([])
const pendingWorkTimeList = ref([])
const selectedWorkTimeList = ref([])
const logList = ref([])
const userProjectList = ref([])
const editingWorkId = ref(null)
const activeWorkTime = ref(null)
const activeLogWorkTime = ref(null)
const approvalMode = ref('approve')
const batchMode = ref('approve')
const managerQueryForm = reactive({
  keyword: '',
})
const employeeQueryForm = reactive({
  projectId: null,
  status: null,
  dateRange: [],
})
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
})

// 表单数据：字段名和后端 WorkTimeCreateDTO / WorkTimeUpdateDTO 保持一致。
const form = reactive({
  projectId: null,
  workDate: '',
  workHours: 8,
  workDesc: '',
})

// 审批表单：通过时传 operationDesc，驳回时传 rejectReason。
const approvalForm = reactive({
  operationDesc: '',
  rejectReason: '',
})

// 批量审批表单：前端复用单条审批接口，逐条发送相同审批说明或驳回原因。
const batchForm = reactive({
  operationDesc: '',
  rejectReason: '',
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

const approvalRules = {
  operationDesc: [{ max: 500, message: '审批说明不能超过 500 个字符', trigger: 'blur' }],
  rejectReason: [
    { required: true, message: '请输入驳回原因', trigger: 'blur' },
    { max: 500, message: '驳回原因不能超过 500 个字符', trigger: 'blur' },
  ],
}

const batchRules = {
  operationDesc: [{ max: 500, message: '审批说明不能超过 500 个字符', trigger: 'blur' }],
  rejectReason: [
    { required: true, message: '请输入驳回原因', trigger: 'blur' },
    { max: 500, message: '驳回原因不能超过 500 个字符', trigger: 'blur' },
  ],
}

const dialogTitle = computed(() => (editingWorkId.value ? '编辑工时' : '新增工时'))
const approvalDialogTitle = computed(() => (approvalMode.value === 'approve' ? '审批通过' : '驳回工时'))
const batchDialogTitle = computed(() => (batchMode.value === 'approve' ? '批量通过工时' : '批量驳回工时'))
const filteredPendingWorkTimeList = computed(() => {
  const keyword = normalizeSearchText(managerQueryForm.keyword)

  if (!keyword) {
    return pendingWorkTimeList.value
  }

  return pendingWorkTimeList.value.filter((item) =>
    [item.workId, item.userName, item.userDeptName, item.projectName, item.workDesc]
      .some((value) => normalizeSearchText(value).includes(keyword)),
  )
})
const filteredWorkTimeList = computed(() =>
  workTimeList.value.filter((item) => {
    const [startDate, endDate] = employeeQueryForm.dateRange || []
    const matchProject = !employeeQueryForm.projectId || item.projectId === employeeQueryForm.projectId
    const matchStatus = employeeQueryForm.status === null || item.status === employeeQueryForm.status
    const matchDate = !startDate || !endDate || (item.workDate >= startDate && item.workDate <= endDate)
    return matchProject && matchStatus && matchDate
  }),
)
const pagedPendingWorkTimeList = computed(() =>
  paginateList(filteredPendingWorkTimeList.value, pagination.currentPage, pagination.pageSize),
)
const pagedWorkTimeList = computed(() =>
  paginateList(filteredWorkTimeList.value, pagination.currentPage, pagination.pageSize),
)

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
  if (authStore.userRole === ROLE_EMPLOYEE || authStore.userRole === ROLE_MANAGER) {
    loadPageData()
  }
})

async function loadPageData() {
  loading.value = true

  try {
    if (authStore.userRole === ROLE_MANAGER) {
      const managerId = authStore.user.userId
      pendingWorkTimeList.value = await listPendingWorkTimesByManagerIdApi(managerId)
      selectedWorkTimeList.value = []
      return
    }

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

function resetCurrentPage() {
  pagination.currentPage = 1
}

function resetForm() {
  editingWorkId.value = null
  form.projectId = null
  form.workDate = ''
  form.workHours = 8
  form.workDesc = ''
  formRef.value?.clearValidate()
}

function resetApprovalForm() {
  approvalForm.operationDesc = ''
  approvalForm.rejectReason = ''
  approvalFormRef.value?.clearValidate()
}

function resetBatchForm() {
  batchForm.operationDesc = ''
  batchForm.rejectReason = ''
  batchFormRef.value?.clearValidate()
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

function openApproveDialog(row) {
  activeWorkTime.value = row
  approvalMode.value = 'approve'
  resetApprovalForm()
  approvalDialogVisible.value = true
}

function openRejectDialog(row) {
  activeWorkTime.value = row
  approvalMode.value = 'reject'
  resetApprovalForm()
  approvalDialogVisible.value = true
}

function handleSelectionChange(selection) {
  selectedWorkTimeList.value = selection
}

function clearSelection() {
  pendingTableRef.value?.clearSelection()
  selectedWorkTimeList.value = []
}

function openBatchDialog(mode) {
  if (selectedWorkTimeList.value.length === 0) {
    ElMessage.warning('请先选择需要审批的工时')
    return
  }

  batchMode.value = mode
  resetBatchForm()
  batchDialogVisible.value = true
}

async function openLogDialog(row) {
  activeLogWorkTime.value = row
  logDialogVisible.value = true
  logLoading.value = true

  try {
    logList.value = await listWorkTimeLogsByWorkIdApi(row.workId)
  } finally {
    logLoading.value = false
  }
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

async function handleApprovalSave() {
  await approvalFormRef.value.validate()
  approvalLoading.value = true

  try {
    if (approvalMode.value === 'approve') {
      await approveWorkTimeApi(activeWorkTime.value.workId, {
        managerId: authStore.user.userId,
        operationDesc: approvalForm.operationDesc,
      })
      ElMessage.success('工时已审批通过')
    } else {
      await rejectWorkTimeApi(activeWorkTime.value.workId, {
        managerId: authStore.user.userId,
        rejectReason: approvalForm.rejectReason,
      })
      ElMessage.success('工时已驳回')
    }

    approvalDialogVisible.value = false
    await loadPageData()
  } finally {
    approvalLoading.value = false
  }
}

async function handleBatchSave() {
  await batchFormRef.value.validate()
  batchLoading.value = true

  try {
    const managerId = authStore.user.userId

    if (batchMode.value === 'approve') {
      // 当前后端没有批量接口，所以这里并发调用单条审批接口，保持后端业务校验不变。
      await Promise.all(
        selectedWorkTimeList.value.map((item) =>
          approveWorkTimeApi(item.workId, {
            managerId,
            operationDesc: batchForm.operationDesc,
          }),
        ),
      )
      ElMessage.success(`${selectedWorkTimeList.value.length} 条工时已批量通过`)
    } else {
      await Promise.all(
        selectedWorkTimeList.value.map((item) =>
          rejectWorkTimeApi(item.workId, {
            managerId,
            rejectReason: batchForm.rejectReason,
          }),
        ),
      )
      ElMessage.success(`${selectedWorkTimeList.value.length} 条工时已批量驳回`)
    }

    batchDialogVisible.value = false
    clearSelection()
    await loadPageData()
  } finally {
    batchLoading.value = false
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

function getLogTypeText(operationType) {
  const operationMap = {
    0: '新建',
    1: '修改',
    2: '提交',
    3: '审批通过',
    4: '驳回',
  }

  return operationMap[operationType] || '未知操作'
}

function getLogTagType(operationType) {
  const typeMap = {
    0: 'info',
    1: 'primary',
    2: 'warning',
    3: 'success',
    4: 'danger',
  }

  return typeMap[operationType] || 'info'
}

function disableFutureDate(date) {
  return date.getTime() > Date.now()
}
</script>
