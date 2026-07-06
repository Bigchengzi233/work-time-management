<template>
  <section class="dashboard">
    <div class="workspace-hero">
      <div>
        <span class="card-eyebrow">{{ getRoleName(authStore.userRole) }}工作台</span>
        <h2>{{ welcomeTitle }}</h2>
      </div>
      <el-button type="primary" @click="router.push(primaryAction.path)">
        {{ primaryAction.title }}
      </el-button>
    </div>

    <div v-loading="loading" class="dashboard-grid">
      <article v-for="item in summaryCards" :key="item.label" class="surface-card metric-card">
        <span class="metric-icon" :class="item.iconClass">
          <el-icon><component :is="item.icon" /></el-icon>
        </span>
        <span class="card-eyebrow">{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </article>
    </div>

    <div class="dashboard-layout">
      <article class="surface-panel task-panel">
        <div class="panel-heading">
          <div>
            <span class="card-eyebrow">快捷入口</span>
            <h2>常用操作</h2>
          </div>
        </div>

        <div class="action-list">
          <button
            v-for="action in quickActions"
            :key="action.title"
            class="action-item"
            type="button"
            @click="router.push(action.path)"
          >
            <span>{{ action.title }}</span>
          </button>
        </div>
      </article>

      <article v-if="showTodoPanel" class="surface-panel todo-panel">
        <div class="panel-heading">
          <div>
            <span class="card-eyebrow">待办事项</span>
          </div>
        </div>

        <div v-if="todoGroups.length > 0" class="todo-list">
          <section v-for="group in todoGroups" :key="group.title" class="todo-group">
            <h3>{{ group.title }}</h3>
            <button
              v-for="item in group.items"
              :key="item.key"
              class="todo-item"
              type="button"
              @click="handleTodoItemClick(item)"
            >
              <span>{{ item.title }}</span>
              <small>{{ item.desc }}</small>
            </button>
          </section>
        </div>
        <el-empty v-else :description="emptyTodoDescription" :image-size="84" />
      </article>

      <article v-if="showAuthorizedProjectsPanel" class="surface-panel authorized-project-panel">
        <div class="panel-heading">
          <div>
            <span class="card-eyebrow">项目授权</span>
            <h2>当前授权项目</h2>
          </div>
        </div>

        <div v-if="activeAuthorizedProjects.length > 0" class="authorized-project-list">
          <button
            v-for="project in activeAuthorizedProjects"
            :key="project.authId"
            class="authorized-project-item"
            type="button"
            @click="router.push('/work-times')"
          >
            <span>{{ project.projectName }}</span>
            <small>{{ project.projectDeptName || authStore.user?.deptName || '暂无部门' }}</small>
          </button>
        </div>
        <el-empty v-else description="暂无可填报项目" :image-size="84" />
      </article>

      <article class="surface-panel recent-panel">
        <div class="panel-heading">
          <div>
            <span class="card-eyebrow">最近操作</span>
            <h2>工时操作记录</h2>
          </div>
        </div>

        <div v-if="recentLogs.length > 0" class="recent-log-list">
          <div v-for="item in recentLogs" :key="item.logId" class="recent-log-item">
            <span>{{ getLogTypeText(item.operationType) }}</span>
            <div>
              <strong>{{ item.workUserName || item.operatorName }}</strong>
              <p>{{ item.projectName || item.operationDesc || '工时操作' }}</p>
            </div>
            <time>{{ formatShortDateTime(item.operationTime) }}</time>
          </div>
        </div>
        <el-empty v-else description="暂无操作记录" :image-size="84" />
      </article>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Clock, Collection, DocumentChecked, Files, OfficeBuilding, Timer, User } from '@element-plus/icons-vue'
import { listDepartmentsApi } from '../api/departments'
import { listProjectsApi } from '../api/projects'
import {
  getCompanyStatisticsApi,
  getDepartmentStatisticsApi,
  getPersonalStatisticsApi,
} from '../api/statistics'
import { listUsersApi } from '../api/users'
import { listWorkTimeLogsApi } from '../api/workTimeLogs'
import { listPendingWorkTimesByManagerIdApi, listWorkTimesByUserIdApi } from '../api/workTimes'
import { useAuthStore } from '../stores/auth'
import { useNotificationStore } from '../stores/notifications'
import { getRoleName, ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_MANAGER } from '../utils/role'

const router = useRouter()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()
const loading = ref(false)
const dashboardData = ref({
  users: [],
  projects: [],
  departments: [],
  workTimes: [],
  pendingWorkTimes: [],
  userProjects: [],
  statistics: null,
  logs: [],
})

const STATUS_PENDING = 1
const STATUS_APPROVED = 2
const STATUS_REJECTED = 3

const roleConfig = {
  [ROLE_ADMIN]: {
    title: '公司工作台',
    primaryAction: {
      title: '进入基础数据',
      path: '/base-data',
    },
    actions: [
      { title: '基础数据管理', path: '/base-data' },
      { title: '统计分析', path: '/statistics' },
      { title: '个人中心', path: '/profile' },
    ],
  },
  [ROLE_MANAGER]: {
    title: '部门工作台',
    primaryAction: {
      title: '进入工时审批',
      path: '/work-times',
    },
    actions: [
      { title: '项目授权管理', path: '/user-projects' },
      { title: '工时管理', path: '/work-times' },
      { title: '统计分析', path: '/statistics' },
    ],
  },
  [ROLE_EMPLOYEE]: {
    title: '我的工作台',
    primaryAction: {
      title: '进入工时填报',
      path: '/work-times',
    },
    actions: [
      { title: '工时管理', path: '/work-times' },
      { title: '统计分析', path: '/statistics' },
      { title: '个人中心', path: '/profile' },
    ],
  },
}

// 当前角色没有匹配时，默认按员工工作台展示，避免页面空白。
const currentConfig = computed(() => roleConfig[authStore.userRole] || roleConfig[ROLE_EMPLOYEE])

const welcomeTitle = computed(() => currentConfig.value.title)
const primaryAction = computed(() => currentConfig.value.primaryAction)
const quickActions = computed(() => currentConfig.value.actions)
const showTodoPanel = computed(() => authStore.userRole !== ROLE_ADMIN)
const showAuthorizedProjectsPanel = computed(() => authStore.userRole === ROLE_EMPLOYEE)
const emptyTodoDescription = computed(() => (
  authStore.userRole === ROLE_MANAGER ? '暂无待审批工时' : '暂无消息提醒'
))

const totalHoursText = computed(() => `${formatHours(dashboardData.value.statistics?.totalHours)} 小时`)
const pendingCount = computed(() => dashboardData.value.pendingWorkTimes.length)
const employeeWorkTimes = computed(() => dashboardData.value.workTimes.filter((item) => item.isDeleted === 0))
const approvedCount = computed(() => employeeWorkTimes.value.filter((item) => item.status === STATUS_APPROVED).length)
const employeePendingCount = computed(() => employeeWorkTimes.value.filter((item) => item.status === STATUS_PENDING).length)
const activeAuthorizedProjects = computed(() => {
  if (
    authStore.userRole === ROLE_EMPLOYEE
    && notificationStore.currentUserId === authStore.user?.userId
  ) {
    return notificationStore.activeAuthorizationNotifications
  }

  return dashboardData.value.userProjects.filter((item) => item.authStatus === 1 && item.projectStatus === 1)
})

const summaryCards = computed(() => {
  if (authStore.userRole === ROLE_ADMIN) {
    return [
      { label: '公司人数', value: `${dashboardData.value.users.length} 人`, icon: User, iconClass: 'metric-blue' },
      { label: '公司项目数', value: `${dashboardData.value.projects.length} 个`, icon: Files, iconClass: 'metric-green' },
      { label: '公司本月总工时', value: totalHoursText.value, icon: Timer, iconClass: 'metric-orange' },
      { label: '公司部门数', value: `${dashboardData.value.departments.length} 个`, icon: OfficeBuilding, iconClass: 'metric-purple' },
    ]
  }

  if (authStore.userRole === ROLE_MANAGER) {
    return [
      { label: '待审批', value: `${pendingCount.value} 条`, icon: DocumentChecked, iconClass: 'metric-orange' },
      { label: '部门本月工时', value: totalHoursText.value, icon: Timer, iconClass: 'metric-blue' },
      { label: '当前部门', value: authStore.user?.deptName || '暂无部门', icon: OfficeBuilding, iconClass: 'metric-green' },
    ]
  }

  return [
    { label: '本月已填工时', value: totalHoursText.value, icon: Clock, iconClass: 'metric-blue' },
    { label: '当前授权项目', value: `${activeAuthorizedProjects.value.length} 个`, icon: Files, iconClass: 'metric-purple' },
    { label: '待审批', value: `${employeePendingCount.value} 条`, icon: DocumentChecked, iconClass: 'metric-orange' },
    { label: '已通过', value: `${approvedCount.value} 条`, icon: Collection, iconClass: 'metric-green' },
  ]
})

const todoGroups = computed(() => {
  if (authStore.userRole === ROLE_MANAGER) {
    const pendingItems = dashboardData.value.pendingWorkTimes.slice(0, 5).map((item) => ({
      key: `pending-${item.workId}`,
      type: 'work-time',
      title: `${item.userName} · ${item.workHours} 小时`,
      desc: `${item.projectName} / ${item.workDate}`,
      path: '/work-times',
    }))

    const projectItems = notificationStore.unreadProjectNotifications.map((item) => ({
      key: `project-${item.projectId}`,
      type: 'project',
      title: '新增项目待授权',
      desc: `${item.projectName} / ${item.deptName || authStore.user?.deptName || '暂无部门'}`,
      notification: item,
    }))

    return [
      { title: '待审批工时', items: pendingItems },
      { title: '待授权项目', items: projectItems },
    ].filter((group) => group.items.length > 0)
  }

  if (authStore.userRole === ROLE_EMPLOYEE) {
    const authorizationItems = notificationStore.unreadAuthorizationNotifications.map((item) => ({
      key: `authorization-${item.authId}`,
      type: 'authorization',
      title: '新增授权项目',
      desc: `${item.projectName} / ${item.projectDeptName || authStore.user?.deptName || '暂无部门'}`,
      notification: item,
    }))

    const rejectedItems = employeeWorkTimes.value
      .filter((item) => item.status === STATUS_REJECTED)
      .slice(0, 5)
      .map((item) => ({
        key: `rejected-${item.workId}`,
        type: 'work-time',
        title: `${item.projectName} · ${item.workHours} 小时`,
        desc: `${item.workDate} 已驳回，需修改后重新提交`,
        path: '/work-times',
      }))

    return [
      { title: '项目授权消息', items: authorizationItems },
      { title: '被驳回工时', items: rejectedItems },
    ].filter((group) => group.items.length > 0)
  }

  return []
})

const recentLogs = computed(() => dashboardData.value.logs.slice(0, 5))

onMounted(() => {
  loadDashboardData()
})

async function loadDashboardData() {
  if (!authStore.user?.userId) {
    return
  }

  loading.value = true
  const [startDate, endDate] = [getMonthStartDate(), getTodayDate()]

  try {
    if (authStore.userRole === ROLE_ADMIN) {
      const [users, projects, departments, statistics, logs] = await Promise.all([
        listUsersApi(),
        listProjectsApi(),
        listDepartmentsApi(),
        getCompanyStatisticsApi({ adminId: authStore.user.userId, startDate, endDate }),
        listWorkTimeLogsApi(),
      ])

      dashboardData.value = {
        users,
        projects,
        departments,
        statistics,
        logs,
        workTimes: [],
        pendingWorkTimes: [],
        userProjects: [],
      }
      return
    }

    if (authStore.userRole === ROLE_MANAGER) {
      const [pendingWorkTimes, statistics, logs] = await Promise.all([
        listPendingWorkTimesByManagerIdApi(authStore.user.userId),
        getDepartmentStatisticsApi({ managerId: authStore.user.userId, startDate, endDate }),
        listWorkTimeLogsApi(),
        notificationStore.loadProjectNotifications(authStore.user.userId, authStore.userRole, authStore.user.deptId),
      ])

      dashboardData.value = {
        ...dashboardData.value,
        pendingWorkTimes,
        statistics,
        logs,
        workTimes: [],
        userProjects: [],
      }
      return
    }

    const [workTimes, statistics, logs] = await Promise.all([
      listWorkTimesByUserIdApi(authStore.user.userId),
      getPersonalStatisticsApi({ userId: authStore.user.userId, startDate, endDate }),
      listWorkTimeLogsApi(),
      notificationStore.loadAuthorizationNotifications(authStore.user.userId, authStore.userRole),
    ])

    dashboardData.value = {
      ...dashboardData.value,
      workTimes,
      userProjects: notificationStore.authorizationList,
      statistics,
      logs,
      pendingWorkTimes: [],
    }
  } finally {
    loading.value = false
  }
}

function getTodayDate() {
  return formatDate(new Date())
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

function handleTodoItemClick(item) {
  if (item.type === 'authorization') {
    notificationStore.openAuthorizationDetail(item.notification)
    return
  }

  if (item.type === 'project') {
    notificationStore.openProjectDetail(item.notification)
    return
  }

  router.push(item.path)
}

function getLogTypeText(operationType) {
  const typeMap = {
    0: '新建',
    1: '修改',
    2: '提交',
    3: '审批通过',
    4: '驳回',
  }

  return typeMap[operationType] || '操作'
}

function formatShortDateTime(value) {
  if (!value) {
    return '-'
  }

  return String(value).replace('T', ' ').slice(5, 16)
}

</script>
