<template>
  <section class="management-page statistics-page">
    <div class="management-header">
      <div>
        <span class="card-eyebrow">统计分析</span>
        <h2>{{ pageTitle }}</h2>
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
            :shortcuts="dateShortcuts"
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
      </article>

      <article class="surface-card statistics-card">
        <span class="card-eyebrow">审批通过工时</span>
        <strong>{{ totalHoursText }}</strong>
      </article>

      <article class="surface-card statistics-card">
        <span class="card-eyebrow">明细数量</span>
        <strong>{{ detailCount }} 条</strong>
      </article>

      <article class="surface-card statistics-card">
        <span class="card-eyebrow">平均工时</span>
        <strong>{{ averageHoursText }}</strong>
      </article>
    </div>

    <div class="statistics-chart-grid">
      <article class="surface-panel chart-panel">
        <div class="panel-heading">
          <div>
            <span class="card-eyebrow">趋势</span>
            <h2>工时趋势</h2>
          </div>
        </div>
        <v-chart class="statistics-chart" :option="trendChartOption" autoresize />
      </article>

      <article class="surface-panel chart-panel">
        <div class="panel-heading">
          <div>
            <span class="card-eyebrow">项目</span>
            <h2>项目占比</h2>
          </div>
        </div>
        <v-chart class="statistics-chart" :option="projectPieOption" autoresize />
      </article>
    </div>

    <div class="surface-panel table-panel">
      <div class="statistics-table-heading">
        <div>
          <span class="card-eyebrow">统计明细</span>
          <h2>审批通过工时明细</h2>
        </div>
        <div class="statistics-table-tools">
          <el-input
            v-model.trim="detailKeyword"
            class="list-filter-input"
            clearable
            placeholder="搜索员工、部门、项目"
            @input="resetCurrentPage"
            @clear="resetCurrentPage"
          />
          <p>{{ dateRangeText }}</p>
        </div>
      </div>

      <el-table v-loading="loading" :data="pagedDetailList" border stripe empty-text="暂无统计数据">
        <el-table-column prop="workId" label="工时编号" width="110" />
        <el-table-column prop="userName" label="员工姓名" width="120" />
        <el-table-column prop="deptName" label="所属部门" min-width="150" show-overflow-tooltip />
        <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="workDate" label="工作日期" width="130" />
        <el-table-column prop="workHours" label="工时数" width="100" />
        <el-table-column prop="workDesc" label="工作描述" min-width="240" show-overflow-tooltip />
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="filteredDetailList.length"
          background
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import VChart from 'vue-echarts'
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
import { normalizeSearchText, paginateList } from '../utils/table'

use([CanvasRenderer, LineChart, PieChart, GridComponent, LegendComponent, TooltipComponent])

const authStore = useAuthStore()
const loading = ref(false)
const statisticsResult = ref(null)
const departmentOptions = ref([])
const userOptions = ref([])
const detailKeyword = ref('')

// 默认查询当前月份：方便用户进入页面后直接看到最近统计数据。
const queryForm = reactive({
  adminScopeType: 'company',
  deptId: null,
  userId: null,
  dateRange: [getMonthStartDate(), getTodayDate()],
})
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
})

const dateShortcuts = [
  {
    text: '本周',
    value: () => [getWeekStartDate(), new Date()],
  },
  {
    text: '本月',
    value: () => [new Date(new Date().getFullYear(), new Date().getMonth(), 1), new Date()],
  },
  {
    text: '本季',
    value: () => [getQuarterStartDate(), new Date()],
  },
  {
    text: '本年',
    value: () => [new Date(new Date().getFullYear(), 0, 1), new Date()],
  },
]

const detailList = computed(() => statisticsResult.value?.details || [])
const filteredDetailList = computed(() => {
  const keyword = normalizeSearchText(detailKeyword.value)

  if (!keyword) {
    return detailList.value
  }

  return detailList.value.filter((item) =>
    [item.workId, item.userName, item.deptName, item.projectName, item.workDesc]
      .some((value) => normalizeSearchText(value).includes(keyword)),
  )
})
const pagedDetailList = computed(() =>
  paginateList(filteredDetailList.value, pagination.currentPage, pagination.pageSize),
)
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

const trendChartOption = computed(() => {
  const trendMap = detailList.value.reduce((map, item) => {
    const date = item.workDate || '未知日期'
    map.set(date, Number(map.get(date) || 0) + Number(item.workHours || 0))
    return map
  }, new Map())
  const dates = Array.from(trendMap.keys()).sort()

  return {
    tooltip: {
      trigger: 'axis',
      valueFormatter: (value) => `${formatHours(value)} 小时`,
    },
    grid: {
      left: 42,
      right: 18,
      top: 26,
      bottom: 36,
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisTick: { show: false },
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
    },
    series: [
      {
        name: '工时',
        type: 'line',
        smooth: true,
        areaStyle: {},
        data: dates.map((date) => Number(trendMap.get(date) || 0).toFixed(1)),
      },
    ],
  }
})

const projectPieOption = computed(() => {
  const projectMap = detailList.value.reduce((map, item) => {
    const projectName = item.projectName || '未知项目'
    map.set(projectName, Number(map.get(projectName) || 0) + Number(item.workHours || 0))
    return map
  }, new Map())

  return {
    tooltip: {
      trigger: 'item',
      valueFormatter: (value) => `${formatHours(value)} 小时`,
    },
    legend: {
      bottom: 0,
      type: 'scroll',
    },
    series: [
      {
        name: '项目工时',
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '42%'],
        avoidLabelOverlap: true,
        data: Array.from(projectMap.entries()).map(([name, value]) => ({
          name,
          value: Number(value.toFixed(1)),
        })),
      },
    ],
  }
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
  resetCurrentPage()
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

function getWeekStartDate() {
  const today = new Date()
  const day = today.getDay() || 7
  return new Date(today.getFullYear(), today.getMonth(), today.getDate() - day + 1)
}

function getQuarterStartDate() {
  const today = new Date()
  const quarterStartMonth = Math.floor(today.getMonth() / 3) * 3
  return new Date(today.getFullYear(), quarterStartMonth, 1)
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
  resetCurrentPage()
}

function resetCurrentPage() {
  pagination.currentPage = 1
}
</script>
