import { defineStore } from 'pinia'
import { loginApi, getCurrentUserApi } from '../api/auth'

const TOKEN_KEY = 'work_time_token'
const USER_KEY = 'work_time_user'

// 登录状态仓库：集中保存 token、用户信息和登录/退出逻辑。
export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: JSON.parse(localStorage.getItem(USER_KEY) || 'null'),
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    userRole: (state) => state.user?.userRole || '',
    userName: (state) => state.user?.userName || '',
  },
  actions: {
    async login(form) {
      const loginResult = await loginApi(form)

      this.token = loginResult.token
      this.user = loginResult

      localStorage.setItem(TOKEN_KEY, this.token)
      localStorage.setItem(USER_KEY, JSON.stringify(this.user))
    },
    async fetchCurrentUser() {
      const currentUser = await getCurrentUserApi()

      this.user = {
        ...this.user,
        ...currentUser,
      }

      localStorage.setItem(USER_KEY, JSON.stringify(this.user))
    },
    updateUserInfo(user) {
      this.user = {
        ...this.user,
        ...user,
      }

      localStorage.setItem(USER_KEY, JSON.stringify(this.user))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    },
  },
})
