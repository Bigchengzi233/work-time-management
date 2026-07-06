import { defineStore } from 'pinia'
import { listProjectsApi } from '../api/projects'
import { listUserProjectsByUserIdApi } from '../api/userProjects'
import { ROLE_EMPLOYEE, ROLE_MANAGER } from '../utils/role'

const READ_AUTH_KEY_PREFIX = 'work_time_read_auth_notifications_'
const READ_PROJECT_KEY_PREFIX = 'work_time_read_project_notifications_'

// 通知状态仓库：集中处理员工项目授权消息、经理新增项目消息的加载、未读和已阅状态。
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
      this.readAuthorizationIds = loadReadAuthorizationIds(userId)
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
      saveReadAuthorizationIds(this.currentUserId, this.readAuthorizationIds)
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
      saveReadProjectIds(this.currentUserId, this.readProjectIds)
    },
    // 标记当前正在查看的新增项目消息为已阅。
    markSelectedProjectRead() {
      if (this.selectedProject?.projectId) {
        this.markProjectRead(this.selectedProject.projectId)
      }

      this.projectDetailVisible = false
      this.selectedProject = null
    },
    // 清空非员工或退出登录时的通知状态。
    clearAuthorizationNotifications() {
      this.currentUserId = null
      this.readAuthorizationIds = []
      this.selectedAuthorization = null
      this.authorizationDetailVisible = false
      this.authorizationList = []
      this.loading = false
    },
    // 清空非经理或退出登录时的新增项目通知状态。
    clearProjectNotifications() {
      this.currentUserId = null
      this.readProjectIds = []
      this.selectedProject = null
      this.projectDetailVisible = false
      this.projectList = []
      this.loading = false
    },
  },
})

function loadReadAuthorizationIds(userId) {
  try {
    return JSON.parse(localStorage.getItem(`${READ_AUTH_KEY_PREFIX}${userId}`) || '[]')
  } catch {
    return []
  }
}

function saveReadAuthorizationIds(userId, readIds) {
  localStorage.setItem(`${READ_AUTH_KEY_PREFIX}${userId}`, JSON.stringify(readIds))
}

function loadOrInitReadProjectIds(userId, projectList) {
  const storageKey = `${READ_PROJECT_KEY_PREFIX}${userId}`
  const storedValue = localStorage.getItem(storageKey)

  if (storedValue === null) {
    const initialReadIds = projectList.map((item) => item.projectId)
    saveReadProjectIds(userId, initialReadIds)
    return initialReadIds
  }

  try {
    return JSON.parse(storedValue || '[]')
  } catch {
    return []
  }
}

function saveReadProjectIds(userId, readIds) {
  localStorage.setItem(`${READ_PROJECT_KEY_PREFIX}${userId}`, JSON.stringify(readIds))
}
