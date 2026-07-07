import { defineStore } from 'pinia'
import { listProjectsApi } from '../api/projects'
import { listUserProjectsByUserIdApi } from '../api/userProjects'
import { listMissingWorkTimesByManagerIdApi } from '../api/workTimes'
import { ROLE_EMPLOYEE, ROLE_MANAGER } from '../utils/role'

const READ_AUTH_KEY_PREFIX = 'work_time_read_auth_notifications_'
const READ_PROJECT_KEY_PREFIX = 'work_time_read_project_notifications_'
const READ_REMINDER_KEY_PREFIX = 'work_time_read_reminder_notifications_'
const EMPLOYEE_REMINDER_KEY_PREFIX = 'work_time_employee_reminders_'

// 通知状态仓库：集中处理授权、项目、工时异常和补填提醒的加载、未读和已阅状态。
export const useNotificationStore = defineStore('notifications', {
  state: () => ({
    currentUserId: null,
    currentUserRole: '',
    authorizationList: [],
    readAuthorizationIds: [],
    selectedAuthorization: null,
    authorizationDetailVisible: false,
    projectList: [],
    readProjectIds: [],
    selectedProject: null,
    projectDetailVisible: false,
    missingWorkTimeList: [],
    selectedMissingWorkTime: null,
    missingWorkTimeDetailVisible: false,
    employeeReminderList: [],
    readReminderIds: [],
    selectedEmployeeReminder: null,
    employeeReminderDetailVisible: false,
    loading: false,
  }),
  getters: {
    // 员工只需要看到有效授权且项目启用的授权消息。
    activeAuthorizationNotifications: (state) =>
      state.authorizationList.filter((item) => item.authStatus === 1 && item.projectStatus === 1),
    // 未读授权消息：未点“已阅”的有效授权项目。
    unreadAuthorizationNotifications() {
      return this.activeAuthorizationNotifications.filter(
        (item) => !this.readAuthorizationIds.includes(item.authId),
      )
    },
    unreadAuthorizationCount() {
      return this.unreadAuthorizationNotifications.length
    },
    // 经理只需要看到本部门启用项目的新增提醒。
    activeProjectNotifications: (state) =>
      state.projectList.filter((item) => item.projectStatus === 1),
    // 未读项目消息：管理员新增后，经理还没有点“已阅”的项目。
    unreadProjectNotifications() {
      return this.activeProjectNotifications.filter(
        (item) => !this.readProjectIds.includes(item.projectId),
      )
    },
    unreadProjectCount() {
      return this.unreadProjectNotifications.length
    },
    // 经理工时异常消息：昨天有有效授权项目、但没有填报任何工时的员工。
    unreadMissingWorkTimeNotifications: (state) => state.missingWorkTimeList,
    unreadMissingWorkTimeCount() {
      return this.unreadMissingWorkTimeNotifications.length
    },
    // 员工补填提醒消息：经理点击“提醒员工”后写入员工消息列表。
    unreadEmployeeReminderNotifications() {
      return this.employeeReminderList.filter(
        (item) => !this.readReminderIds.includes(item.reminderId),
      )
    },
    unreadEmployeeReminderCount() {
      return this.unreadEmployeeReminderNotifications.length
    },
  },
  actions: {
    // 加载当前员工的项目授权消息。
    async loadAuthorizationNotifications(userId, userRole) {
      if (!userId || userRole !== ROLE_EMPLOYEE) {
        this.clearAuthorizationNotifications()
        return
      }

      this.currentUserId = userId
      this.currentUserRole = userRole
      this.readAuthorizationIds = loadReadIds(`${READ_AUTH_KEY_PREFIX}${userId}`)
      this.loading = true

      try {
        this.authorizationList = await listUserProjectsByUserIdApi(userId)
      } finally {
        this.loading = false
      }
    },
    // 打开授权消息详情弹窗。
    openAuthorizationDetail(notification) {
      this.selectedAuthorization = notification
      this.authorizationDetailVisible = true
    },
    // 标记指定授权消息为已阅。
    markAuthorizationRead(authId) {
      if (!this.currentUserId || !authId || this.readAuthorizationIds.includes(authId)) {
        return
      }

      this.readAuthorizationIds = [...this.readAuthorizationIds, authId]
      saveIds(`${READ_AUTH_KEY_PREFIX}${this.currentUserId}`, this.readAuthorizationIds)
    },
    // 标记当前正在查看的授权消息为已阅。
    markSelectedAuthorizationRead() {
      if (this.selectedAuthorization?.authId) {
        this.markAuthorizationRead(this.selectedAuthorization.authId)
      }

      this.authorizationDetailVisible = false
      this.selectedAuthorization = null
    },
    // 加载当前部门经理的新增项目消息。
    async loadProjectNotifications(userId, userRole, deptId) {
      if (!userId || userRole !== ROLE_MANAGER || !deptId) {
        this.clearProjectNotifications()
        return
      }

      this.currentUserId = userId
      this.currentUserRole = userRole
      this.loading = true

      try {
        const projects = await listProjectsApi()
        this.projectList = projects.filter((item) => item.deptId === deptId && item.projectStatus === 1)
        this.readProjectIds = loadOrInitReadProjectIds(userId, this.projectList)
      } finally {
        this.loading = false
      }
    },
    // 打开新增项目消息详情弹窗。
    openProjectDetail(notification) {
      this.selectedProject = notification
      this.projectDetailVisible = true
    },
    // 标记指定项目消息为已阅。
    markProjectRead(projectId) {
      if (!this.currentUserId || !projectId || this.readProjectIds.includes(projectId)) {
        return
      }

      this.readProjectIds = [...this.readProjectIds, projectId]
      saveIds(`${READ_PROJECT_KEY_PREFIX}${this.currentUserId}`, this.readProjectIds)
    },
    // 标记当前正在查看的新增项目消息为已阅。
    markSelectedProjectRead() {
      if (this.selectedProject?.projectId) {
        this.markProjectRead(this.selectedProject.projectId)
      }

      this.projectDetailVisible = false
      this.selectedProject = null
    },
    // 加载经理端昨天未填报工时的员工列表。
    async loadMissingWorkTimeNotifications(userId, userRole) {
      if (!userId || userRole !== ROLE_MANAGER) {
        this.clearMissingWorkTimeNotifications()
        return
      }

      this.currentUserId = userId
      this.currentUserRole = userRole
      this.loading = true

      try {
        this.missingWorkTimeList = await listMissingWorkTimesByManagerIdApi(userId)
      } finally {
        this.loading = false
      }
    },
    // 打开经理端工时异常详情弹窗。
    openMissingWorkTimeDetail(notification) {
      this.selectedMissingWorkTime = notification
      this.missingWorkTimeDetailVisible = true
    },
    // 经理给员工发送补填提醒；当前版本不改数据库，提醒保存在浏览器本地消息中。
    sendMissingWorkTimeReminder(notification, manager) {
      if (!notification?.userId || !notification?.workDate || !manager?.userId) {
        return false
      }

      const reminderId = buildReminderId(manager.userId, notification.userId, notification.workDate)
      const storageKey = `${EMPLOYEE_REMINDER_KEY_PREFIX}${notification.userId}`
      const reminders = loadReminderList(notification.userId)

      if (reminders.some((item) => item.reminderId === reminderId)) {
        notification.reminderSent = true
        return false
      }

      const nextReminder = {
        reminderId,
        employeeId: notification.userId,
        employeeName: notification.userName,
        managerId: manager.userId,
        managerName: manager.userName || '部门经理',
        deptName: notification.deptName || manager.deptName || '',
        workDate: notification.workDate,
        createdAt: new Date().toISOString(),
        message: `${notification.workDate} 还没有填写工时，请尽快补填并提交。`,
      }

      localStorage.setItem(storageKey, JSON.stringify([nextReminder, ...reminders]))
      notification.reminderSent = true
      return true
    },
    // 判断某条异常是否已经发送过补填提醒。
    hasSentMissingWorkTimeReminder(notification, managerId) {
      if (!notification?.userId || !notification?.workDate || !managerId) {
        return false
      }

      const reminderId = buildReminderId(managerId, notification.userId, notification.workDate)
      return notification.reminderSent || loadReminderList(notification.userId).some((item) => item.reminderId === reminderId)
    },
    // 加载员工端收到的补填提醒。
    loadEmployeeReminderNotifications(userId, userRole) {
      if (!userId || userRole !== ROLE_EMPLOYEE) {
        this.clearEmployeeReminderNotifications()
        return
      }

      this.currentUserId = userId
      this.currentUserRole = userRole
      this.employeeReminderList = loadReminderList(userId)
      this.readReminderIds = loadReadIds(`${READ_REMINDER_KEY_PREFIX}${userId}`)
    },
    // 打开员工端补填提醒详情弹窗。
    openEmployeeReminderDetail(notification) {
      this.selectedEmployeeReminder = notification
      this.employeeReminderDetailVisible = true
    },
    // 标记员工端补填提醒已阅。
    markEmployeeReminderRead(reminderId) {
      if (!this.currentUserId || !reminderId || this.readReminderIds.includes(reminderId)) {
        return
      }

      this.readReminderIds = [...this.readReminderIds, reminderId]
      saveIds(`${READ_REMINDER_KEY_PREFIX}${this.currentUserId}`, this.readReminderIds)
    },
    // 标记当前正在查看的补填提醒为已阅。
    markSelectedEmployeeReminderRead() {
      if (this.selectedEmployeeReminder?.reminderId) {
        this.markEmployeeReminderRead(this.selectedEmployeeReminder.reminderId)
      }

      this.employeeReminderDetailVisible = false
      this.selectedEmployeeReminder = null
    },
    // 清空非员工或退出登录时的通知状态。
    clearAuthorizationNotifications() {
      this.readAuthorizationIds = []
      this.selectedAuthorization = null
      this.authorizationDetailVisible = false
      this.authorizationList = []
      this.loading = false
    },
    // 清空非经理或退出登录时的新增项目通知状态。
    clearProjectNotifications() {
      this.readProjectIds = []
      this.selectedProject = null
      this.projectDetailVisible = false
      this.projectList = []
      this.loading = false
    },
    // 清空非经理或退出登录时的工时异常通知状态。
    clearMissingWorkTimeNotifications() {
      this.selectedMissingWorkTime = null
      this.missingWorkTimeDetailVisible = false
      this.missingWorkTimeList = []
      this.loading = false
    },
    // 清空非员工或退出登录时的补填提醒状态。
    clearEmployeeReminderNotifications() {
      this.readReminderIds = []
      this.selectedEmployeeReminder = null
      this.employeeReminderDetailVisible = false
      this.employeeReminderList = []
      this.loading = false
    },
    // 退出登录或切换角色时统一清理当前内存状态。
    clearAllNotifications() {
      this.currentUserId = null
      this.currentUserRole = ''
      this.clearAuthorizationNotifications()
      this.clearProjectNotifications()
      this.clearMissingWorkTimeNotifications()
      this.clearEmployeeReminderNotifications()
    },
  },
})

function loadReadIds(storageKey) {
  try {
    return JSON.parse(localStorage.getItem(storageKey) || '[]')
  } catch {
    return []
  }
}

function saveIds(storageKey, readIds) {
  localStorage.setItem(storageKey, JSON.stringify(readIds))
}

function loadOrInitReadProjectIds(userId, projectList) {
  const storageKey = `${READ_PROJECT_KEY_PREFIX}${userId}`
  const storedValue = localStorage.getItem(storageKey)

  if (storedValue === null) {
    const initialReadIds = projectList.map((item) => item.projectId)
    saveIds(storageKey, initialReadIds)
    return initialReadIds
  }

  return loadReadIds(storageKey)
}

function loadReminderList(userId) {
  try {
    return JSON.parse(localStorage.getItem(`${EMPLOYEE_REMINDER_KEY_PREFIX}${userId}`) || '[]')
  } catch {
    return []
  }
}

function buildReminderId(managerId, employeeId, workDate) {
  return `${managerId}-${employeeId}-${workDate}`
}
