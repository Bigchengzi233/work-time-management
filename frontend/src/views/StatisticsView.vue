<template>
  <section class="management-page statistics-page">
    <div class="management-header">
      <div>
        <span class="card-eyebrow">统计分析</span>
        <h2>{{ pageTitle }}</h2>
        <p>{{ pageDesc }}</p>
      </div>

      <el-button :icon="Refresh" @click="loadStatistics">
        刷新统计
      </el-button>
    </div>

    <div class="surface-panel statistics-filter-panel">
      <el-form :model="queryForm" class="statistics-filter-form" label-width="76px">
        <el-form-item v-if="authStore.userRole === ROLE_ADMIN" label="统计维度">
          <el-select v-model="queryForm.adminScopeType" class="statistics-scope-select" @change="handleAdminScopeChange">
            <el-option label="公司总体" value="company" />
            <el-option label="按部门" value="department" />
            <el-option label="按员工" value="personal" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="showDepartmentSelector" label="选择部门">
          <el-select v-model="queryForm.deptId" class="statistics-scope-select" placeholder="请选择部门" filterable>
            <el-option
              v-for="department in departmentOptions"
              :key="department.deptId"
              :label="department.deptName"
              :value="department.deptId"
            />
          </el-select>
        </el-form-item>

        <el-form-item v-if="showUserSelector" label="选择员工">
          <el-select v-model="queryForm.userId" class="statistics-scope-select" placeholder="请选择员工" filterable>
            <el-option
              v-for="user in employeeOptions"
              :key="user.userId"
              :label="`${user.userName}（${user.deptName}）`"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="日期范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            range-separator="至"
          />
        </el-form-item>

        <el-button type="primary" :loading="loading" @click="loadStatistics">
          查询
        </el-button>
      </el-form>
    </div>

    <div class="statistics-summary-grid">
      <article class="surface-card statistics-card">
        <span class="card-eyebrow">统计范围</span>
        <strong>{{ statisticsResult?.scopeName || '-' }}</strong>
        <p>{{ scopeHelpText }}</p>
      </article>

      <article class="surface-card statistics-card">
        <span class="card-eyebrow">审批通过工时</span>
        <strong>{{ totalHoursText }}</strong>
        <p>只统计审批通过且未删除的工时报单。</p>
      </article>

      <article class="surface-card statistics-card">
        <span class="card-eyebrow">明细数量</span>
        <strong>{{ detailCount }} 条</strong>
        <p>当前日期范围内进入统计口径的工时记录数。</p>
      </article>

      <article class="surface-card statistics-card">
        <span class="card-eyebrow">平均工时</span>
        <strong>{{ averageHoursText }}</strong>
        <p>总工时除以明细数量，仅用于快速参考。</p>
      </article>
    </div>

    <div class="surface-panel table-panel">
      <div class="statistics-table-heading">
        <div>
          <span class="card-eyebrow">统计明细</span>
          <h2>审批通过工时明细</h2>
        </div>
        <p>{{ dateRangeText }}</p>
      </div>

      <el-table v-loading="loading" :data="detailList" border stripe empty-text="暂无统计数据">
        <el-table-column prop="workId" label="工时编号" width="110" />
        <el-table-column prop="userName" label="员工姓名" width="120" />
        <el-table-column prop="deptName" label="所属部门" min-width="150" show-overflow-tooltip />
        <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="workDate" label="工作日期" width="130" />
        <el-table-column prop="workHours" label="工时数" width="100" />
        <el-table-column prop="workDesc" label="工作描述" min-width="240" show-overflow-tooltip />
      </el-table>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import {
  getCompanyStatisticsApi,
  getDepartmentStatisticsByDeptApi,
  getDepartmentStatisticsApi,
  getPersonalStatisticsApi,
} from '../api/statistics'
import { listDepartmentsApi } from '../api/departments'
import { listUsersApi } from '../api/users'
import { useAuthStore } from '../stores/auth'
import { ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_MANAGER } from '../utils/role'

const authStore = useAuthStore()
const loading = ref(false)
const statisticsResult = ref(null)
const departmentOptions = ref([])
const userOptions = ref([])

// 默认查询当前月份：方便用户进入页面后直接看到最近统计数据。
const queryForm = reactive({
  adminScopeType: 'company',
  deptId: null,
  userId: null,
  dateRange: [getMonthStartDate(), getTodayDate()],
})

const detailList = computed(() => statisticsResult.value?.details || [])
const detailCount = computed(() => detailList.value.length)
const employeeOptions = computed(() => userOptions.value.filter((user) => user.userRole === ROLE_EMPLOYEE))
const showDepartmentSelector = computed(() =>
  authStore.userRole === ROLE_ADMIN && queryForm.adminScopeType === 'department',
)
const showUserSelector = computed(() =>
  authStore.userRole === ROLE_ADMIN && queryForm.adminScopeType === 'personal',
)

const pageTitle = computed(() => {
  if (authStore.userRole === ROLE_ADMIN) {
    const adminTitleMap = {
      company: '公司工时统计',
      department: '部门工时统计',
      personal: '员工个人工时统计',
    }

    return adminTitleMap[queryForm.adminScopeType]
  }

  const titleMap = {
    [ROLE_ADMIN]: '公司工时统计',
    [ROLE_MANAGER]: '部门工时统计',
    [ROLE_EMPLOYEE]: '个人工时统计',
  }

  return titleMap[authStore.userRole] || '工时统计'
})

const pageDesc = computed(() => {
  if (authStore.userRole === ROLE_ADMIN) {
    const adminDescMap = {
      company: '查看公司全部部门审批通过后的工时投入情况。',
      department: '选择一个部门，查看该部门审批通过后的工时投入情况。',
      personal: '选择一名员工，查看该员工审批通过后的工时记录和总工时。',
    }

    return adminDescMap[queryForm.adminScopeType]
  }

  const descMap = {
    [ROLE_ADMIN]: '查看公司全部部门审批通过后的工时投入情况。',
    [ROLE_MANAGER]: '查看本人负责部门内审批通过后的工时投入情况。',
    [ROLE_EMPLOYEE]: '查看本人审批通过后的工时记录和总工时。',
  }

  return descMap[authStore.userRole] || '查看审批通过后的工时统计数据。'
})

const scopeHelpText = computed(() => {
  if (authStore.userRole === ROLE_ADMIN) {
    const adminHelpMap = {
      company: '管理员查看公司总体数据。',
      department: '管理员按部门下钻查看数据。',
      personal: '管理员按员工下钻查看数据。',
    }

    return adminHelpMap[queryForm.adminScopeType]
  }

  const helpMap = {
    [ROLE_ADMIN]: '管理员查看公司总体数据。',
    [ROLE_MANAGER]: '部门经理查看本部门数据。',
    [ROLE_EMPLOYEE]: '员工查看本人数据。',
  }

  return helpMap[authStore.userRole] || '根据当前登录角色控制统计范围。'
})

const totalHoursText = computed(() => `${formatHours(statisticsResult.value?.totalHours)} 小时`)

const averageHoursText = computed(() => {
  if (detailCount.value === 0) {
    return '0.0 小时'
  }

  const averageHours = Number(statisticsResult.value?.totalHours || 0) / detailCount.value
  return `${averageHours.toFixed(1)} 小时`
})

const dateRangeText = computed(() => {
  const [startDate, endDate] = queryForm.dateRange || []
  if (!startDate || !endDate) {
    return '请选择日期范围'
  }

  return `${startDate} 至 ${endDate}`
})

onMounted(() => {
  initPage()
})

async function initPage() {
  if (authStore.userRole === ROLE_ADMIN) {
    await loadAdminOptions()
  }

  await loadStatistics()
}

async function loadAdminOptions() {
  const [departments, users] = await Promise.all([
    listDepartmentsApi(),
    listUsersApi(),
  ])

  departmentOptions.value = departments
  userOptions.value = users
}

async function loadStatistics() {
  const [startDate, endDate] = queryForm.dateRange || []

  if (!startDate || !endDate) {
    ElMessage.warning('请选择日期范围')
    return
  }

  loading.value = true

  try {
    if (authStore.userRole === ROLE_ADMIN) {
      if (queryForm.adminScopeType === 'department') {
        if (!queryForm.deptId) {
          ElMessage.warning('请选择部门')
          return
        }

        statisticsResult.value = await getDepartmentStatisticsByDeptApi({
          adminId: authStore.user.userId,
          deptId: queryForm.deptId,
          startDate,
          endDate,
        })
        return
      }

      if (queryForm.adminScopeType === 'personal') {
        if (!queryForm.userId) {
          ElMessage.warning('请选择员工')
          return
        }

        statisticsResult.value = await getPersonalStatisticsApi({
          userId: queryForm.userId,
          startDate,
          endDate,
        })
        return
      }

      statisticsResult.value = await getCompanyStatisticsApi({
        adminId: authStore.user.userId,
        startDate,
        endDate,
      })
      return
    }

    if (authStore.userRole === ROLE_MANAGER) {
      statisticsResult.value = await getDepartmentStatisticsApi({
        managerId: authStore.user.userId,
        startDate,
        endDate,
      })
      return
    }

    if (authStore.userRole === ROLE_EMPLOYEE) {
      statisticsResult.value = await getPersonalStatisticsApi({
        userId: authStore.user.userId,
        startDate,
        endDate,
      })
    }
  } finally {
    loading.value = false
  }
}

function getTodayDate() {
  const today = new Date()
  return formatDate(today)
}

function getMonthStartDate() {
  const today = new Date()
  return formatDate(new Date(today.getFullYear(), today.getMonth(), 1))
}

function formatDate(date) {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}

function formatHours(value) {
  return Number(value || 0).toFixed(1)
}

function handleAdminScopeChange() {
  statisticsResult.value = null
  queryForm.deptId = null
  queryForm.userId = null
}
</script>
