<template>
  <header class="topbar">
    <div class="topbar-title">
      <span class="topbar-kicker">员工工时管理</span>
      <h1 class="page-title">{{ pageTitle }}</h1>
      <el-breadcrumb class="topbar-breadcrumb" separator="/">
        <el-breadcrumb-item v-for="item in breadcrumbItems" :key="item.path" :to="item.to">
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="user-panel">
      <el-tooltip v-if="showNotificationButton" :content="notificationText" placement="bottom">
        <button class="topbar-icon-button" type="button" @click="handleNotificationClick">
          <el-badge :value="notificationCount" :hidden="notificationCount === 0" :max="99">
            <el-icon><Bell /></el-icon>
          </el-badge>
        </button>
      </el-tooltip>

      <el-dropdown trigger="click" @command="handleUserCommand">
        <button class="user-dropdown-trigger" type="button">
          <div class="user-avatar">{{ userInitial }}</div>
          <div class="user-meta">
            <span class="user-name">{{ authStore.user?.userName || '未命名用户' }}</span>
            <span class="user-role">{{ getRoleName(authStore.userRole) }}</span>
          </div>
          <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
        </button>

        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>

  <el-dialog
    v-model="notificationStore.authorizationDetailVisible"
    title="项目授权消息"
    width="460px"
    destroy-on-close
  >
    <div v-if="notificationStore.selectedAuthorization" class="notification-detail">
      <span class="notification-detail-icon">项</span>
      <div>
        <h3>新增授权项目</h3>
        <p>
          部门经理已为你授权项目
          <strong>「{{ notificationStore.selectedAuthorization.projectName }}」</strong>
        </p>
      </div>
      <dl>
        <div>
          <dt>项目部门</dt>
          <dd>{{ notificationStore.selectedAuthorization.projectDeptName || '暂无部门' }}</dd>
        </div>
        <div>
          <dt>授权时间</dt>
          <dd>{{ formatDateTime(notificationStore.selectedAuthorization.authTime) }}</dd>
        </div>
      </dl>
    </div>

    <template #footer>
      <el-button @click="notificationStore.authorizationDetailVisible = false">稍后处理</el-button>
      <el-button @click="goWorkTimes">去填报工时</el-button>
      <el-button type="primary" @click="notificationStore.markSelectedAuthorizationRead">
        已阅
      </el-button>
    </template>
  </el-dialog>

  <el-dialog
    v-model="notificationStore.projectDetailVisible"
    title="新增项目提醒"
    width="460px"
    destroy-on-close
  >
    <div v-if="notificationStore.selectedProject" class="notification-detail">
      <span class="notification-detail-icon">项</span>
      <div>
        <h3>新增待授权项目</h3>
        <p>
          管理员已新增项目
          <strong>「{{ notificationStore.selectedProject.projectName }}」</strong>
          ，请尽快授权给员工。
        </p>
      </div>
      <dl>
        <div>
          <dt>所属部门</dt>
          <dd>{{ notificationStore.selectedProject.deptName || authStore.user?.deptName || '暂无部门' }}</dd>
        </div>
        <div>
          <dt>项目状态</dt>
          <dd>{{ notificationStore.selectedProject.projectStatus === 1 ? '启用' : '禁用' }}</dd>
        </div>
      </dl>
    </div>

    <template #footer>
      <el-button @click="notificationStore.projectDetailVisible = false">稍后处理</el-button>
      <el-button @click="goUserProjects">去授权员工</el-button>
      <el-button type="primary" @click="notificationStore.markSelectedProjectRead">
        已阅
      </el-button>
    </template>
  </el-dialog>

  <el-dialog
    v-model="notificationStore.missingWorkTimeDetailVisible"
    title="工时异常提醒"
    width="460px"
    destroy-on-close
  >
    <div v-if="notificationStore.selectedMissingWorkTime" class="notification-detail">
      <span class="notification-detail-icon">异</span>
      <div>
        <h3>员工昨日未填报工时</h3>
        <p>
          <strong>{{ notificationStore.selectedMissingWorkTime.userName }}</strong>
          在 {{ notificationStore.selectedMissingWorkTime.workDate }} 没有填写工时，可提醒员工尽快补填。
        </p>
      </div>
      <dl>
        <div>
          <dt>所属部门</dt>
          <dd>{{ notificationStore.selectedMissingWorkTime.deptName || authStore.user?.deptName || '暂无部门' }}</dd>
        </div>
        <div>
          <dt>可填项目</dt>
          <dd>{{ notificationStore.selectedMissingWorkTime.activeProjectCount || 0 }} 个</dd>
        </div>
      </dl>
    </div>

    <template #footer>
      <el-button @click="notificationStore.missingWorkTimeDetailVisible = false">稍后处理</el-button>
      <el-button
        type="primary"
        :disabled="hasSentReminder(notificationStore.selectedMissingWorkTime)"
        @click="sendSelectedMissingWorkTimeReminder"
      >
        {{ hasSentReminder(notificationStore.selectedMissingWorkTime) ? '已提醒' : '提醒员工' }}
      </el-button>
    </template>
  </el-dialog>

  <el-dialog
    v-model="notificationStore.employeeReminderDetailVisible"
    title="工时补填提醒"
    width="460px"
    destroy-on-close
  >
    <div v-if="notificationStore.selectedEmployeeReminder" class="notification-detail">
      <span class="notification-detail-icon">提</span>
      <div>
        <h3>请补填昨日工时</h3>
        <p>
          {{ notificationStore.selectedEmployeeReminder.managerName }} 提醒你：
          {{ notificationStore.selectedEmployeeReminder.message }}
        </p>
      </div>
      <dl>
        <div>
          <dt>异常日期</dt>
          <dd>{{ notificationStore.selectedEmployeeReminder.workDate }}</dd>
        </div>
        <div>
          <dt>提醒时间</dt>
          <dd>{{ formatDateTime(notificationStore.selectedEmployeeReminder.createdAt) }}</dd>
        </div>
      </dl>
    </div>

    <template #footer>
      <el-button @click="notificationStore.employeeReminderDetailVisible = false">稍后处理</el-button>
      <el-button @click="goWorkTimes">去补填工时</el-button>
      <el-button type="primary" @click="notificationStore.markSelectedEmployeeReminderRead">
        已阅
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown, Bell } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { listPendingWorkTimesByManagerIdApi, listWorkTimesByUserIdApi } from '../api/workTimes'
import { useAuthStore } from '../stores/auth'
import { useNotificationStore } from '../stores/notifications'
import { getRoleName, ROLE_EMPLOYEE, ROLE_MANAGER } from '../utils/role'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()
const managerPendingCount = ref(0)
const employeeRejectedCount = ref(0)
let notificationTimer = null

// 顶部标题直接取当前路由的 meta.title。
const pageTitle = computed(() => route.meta.title || '员工工时管理系统')

// 头像文字取用户姓名首字，缺省时展示“工”。
const userInitial = computed(() => authStore.user?.userName?.slice(0, 1) || '工')

// 管理员当前没有真实通知来源，所以只给经理和员工展示通知入口。
const showNotificationButton = computed(() =>
  authStore.userRole === ROLE_MANAGER || authStore.userRole === ROLE_EMPLOYEE,
)

const notificationCount = computed(() => {
  if (authStore.userRole === ROLE_MANAGER) {
    return managerPendingCount.value
      + notificationStore.unreadProjectCount
      + notificationStore.unreadMissingWorkTimeCount
  }

  if (authStore.userRole === ROLE_EMPLOYEE) {
    return employeeRejectedCount.value
      + notificationStore.unreadAuthorizationCount
      + notificationStore.unreadEmployeeReminderCount
  }

  return 0
})

// 面包屑只保留业务路径层级，首页本身不重复显示两次。
const breadcrumbItems = computed(() => {
  const items = [{ title: '首页', path: '/', to: { path: '/' } }]

  if (route.path !== '/') {
    items.push({
      title: pageTitle.value,
      path: route.path,
      to: undefined,
    })
  }

  return items
})

// 经理关注待审批数量，员工关注已驳回数量，管理员暂时不展示待办角标。
const notificationText = computed(() => {
  if (authStore.userRole === ROLE_MANAGER) {
    const projectCount = notificationStore.unreadProjectCount
    const exceptionCount = notificationStore.unreadMissingWorkTimeCount
    const pendingCount = managerPendingCount.value
    const textParts = []

    if (exceptionCount > 0) {
      textParts.push(`${exceptionCount} 名员工工时异常`)
    }

    if (projectCount > 0) {
      textParts.push(`${projectCount} 个项目待授权`)
    }

    if (pendingCount > 0) {
      textParts.push(`${pendingCount} 条工时待审批`)
    }

    return textParts.length > 0 ? textParts.join('，') : '暂无通知'
  }

  if (authStore.userRole === ROLE_EMPLOYEE) {
    const authCount = notificationStore.unreadAuthorizationCount
    const reminderCount = notificationStore.unreadEmployeeReminderCount
    const rejectedCount = employeeRejectedCount.value
    const textParts = []

    if (reminderCount > 0) {
      textParts.push(`${reminderCount} 条工时补填提醒`)
    }

    if (authCount > 0) {
      textParts.push(`${authCount} 条项目授权消息`)
    }

    if (rejectedCount > 0) {
      textParts.push(`${rejectedCount} 条工时被驳回`)
    }

    return textParts.length > 0 ? textParts.join('，') : '暂无通知'
  }

  return '暂无通知'
})

onMounted(() => {
  loadNotificationCount()
  notificationTimer = window.setInterval(loadNotificationCount, 30000)
})

onBeforeUnmount(() => {
  if (notificationTimer) {
    window.clearInterval(notificationTimer)
  }
})

watch(
  () => [route.fullPath, authStore.userRole, authStore.user?.userId],
  () => {
    loadNotificationCount()
  },
)

async function loadNotificationCount() {
  if (!authStore.user?.userId) {
    managerPendingCount.value = 0
    employeeRejectedCount.value = 0
    notificationStore.clearAllNotifications()
    return
  }

  if (authStore.userRole === ROLE_MANAGER) {
    notificationStore.clearAuthorizationNotifications()
    notificationStore.clearEmployeeReminderNotifications()
    const [pendingList] = await Promise.all([
      listPendingWorkTimesByManagerIdApi(authStore.user.userId),
      notificationStore.loadProjectNotifications(authStore.user.userId, authStore.userRole, authStore.user.deptId),
      notificationStore.loadMissingWorkTimeNotifications(authStore.user.userId, authStore.userRole),
    ])
    managerPendingCount.value = pendingList.length
    employeeRejectedCount.value = 0
    return
  }

  if (authStore.userRole === ROLE_EMPLOYEE) {
    notificationStore.clearProjectNotifications()
    notificationStore.clearMissingWorkTimeNotifications()
    const [workTimeList] = await Promise.all([
      listWorkTimesByUserIdApi(authStore.user.userId),
      notificationStore.loadAuthorizationNotifications(authStore.user.userId, authStore.userRole),
      notificationStore.loadEmployeeReminderNotifications(authStore.user.userId, authStore.userRole),
    ])
    managerPendingCount.value = 0
    employeeRejectedCount.value = workTimeList.filter((item) => item.status === 3 && item.isDeleted === 0).length
    return
  }

  managerPendingCount.value = 0
  employeeRejectedCount.value = 0
  notificationStore.clearAllNotifications()
}

function handleNotificationClick() {
  if (authStore.userRole === ROLE_MANAGER) {
    const firstMissingWorkTime = notificationStore.unreadMissingWorkTimeNotifications[0]
    const firstUnreadProject = notificationStore.unreadProjectNotifications[0]

    if (firstMissingWorkTime) {
      notificationStore.openMissingWorkTimeDetail(firstMissingWorkTime)
      return
    }

    if (firstUnreadProject) {
      notificationStore.openProjectDetail(firstUnreadProject)
      return
    }

    router.push('/work-times')
    return
  }

  if (authStore.userRole === ROLE_EMPLOYEE) {
    const firstUnreadReminder = notificationStore.unreadEmployeeReminderNotifications[0]
    const firstUnreadAuthorization = notificationStore.unreadAuthorizationNotifications[0]

    if (firstUnreadReminder) {
      notificationStore.openEmployeeReminderDetail(firstUnreadReminder)
      return
    }

    if (firstUnreadAuthorization) {
      notificationStore.openAuthorizationDetail(firstUnreadAuthorization)
      return
    }

    router.push('/work-times')
  }
}

function goWorkTimes() {
  notificationStore.authorizationDetailVisible = false
  notificationStore.employeeReminderDetailVisible = false
  router.push('/work-times')
}

function goUserProjects() {
  notificationStore.projectDetailVisible = false
  router.push('/user-projects')
}

function formatDateTime(value) {
  if (!value) {
    return '-'
  }

  return String(value).replace('T', ' ').slice(0, 16)
}

function hasSentReminder(notification) {
  return notificationStore.hasSentMissingWorkTimeReminder(notification, authStore.user?.userId)
}

function sendSelectedMissingWorkTimeReminder() {
  const sent = notificationStore.sendMissingWorkTimeReminder(
    notificationStore.selectedMissingWorkTime,
    authStore.user,
  )

  if (sent) {
    ElMessage.success('已发送补填提醒')
    return
  }

  ElMessage.info('该员工已收到本次补填提醒')
}

function handleUserCommand(command) {
  if (command === 'profile') {
    router.push('/profile')
    return
  }

  if (command === 'logout') {
    handleLogout()
  }
}

function handleLogout() {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>
