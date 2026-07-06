import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import router from '../router'

// 创建 Axios 实例，统一配置后端接口的基础地址。
const request = axios.create({
  baseURL: 'http://localhost:18080/api',
  timeout: 10000,
})

// 请求拦截器：每次请求后端前，自动把 token 放进 Authorization 请求头。
request.interceptors.request.use((config) => {
  const authStore = useAuthStore()

  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }

  return config
})

// 响应拦截器：统一处理后端 ApiResponse 格式和登录失效问题。
request.interceptors.response.use(
  (response) => {
    const result = response.data

    if (result.code === 200) {
      return result.data
    }

    if (result.code === 401) {
      handleUnauthorized()
      return Promise.reject(new Error(result.message || '登录已过期，请重新登录'))
    }

    ElMessage.error(result.message || '请求失败')
    return Promise.reject(new Error(result.message || '请求失败'))
  },
  (error) => {
    if (error.response?.status === 401) {
      handleUnauthorized()
      return Promise.reject(error)
    }

    ElMessage.error('网络异常，请稍后重试')
    return Promise.reject(error)
  },
)

function handleUnauthorized() {
  const authStore = useAuthStore()
  authStore.logout()

  if (router.currentRoute.value.path !== '/login') {
    router.push('/login')
  }

  ElMessage.error('登录已过期，请重新登录')
}

export default request
