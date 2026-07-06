import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_MANAGER } from '../utils/role'
import LoginView from '../views/LoginView.vue'
import MainLayout from '../views/MainLayout.vue'
import DashboardView from '../views/DashboardView.vue'
import BaseDataView from '../views/BaseDataView.vue'
import ProfileView from '../views/ProfileView.vue'
import UserProjectManageView from '../views/UserProjectManageView.vue'
import WorkTimeManageView from '../views/WorkTimeManageView.vue'
import StatisticsView from '../views/StatisticsView.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: {
      public: true,
      title: '登录',
    },
  },
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'dashboard',
        component: DashboardView,
        meta: {
          title: '首页仪表盘',
        },
      },
      {
        path: 'base-data',
        name: 'base-data',
        component: BaseDataView,
        meta: {
          title: '基础数据管理',
          roles: [ROLE_ADMIN],
        },
      },
      {
        path: 'user-projects',
        name: 'user-projects',
        component: UserProjectManageView,
        meta: {
          title: '项目授权管理',
          roles: [ROLE_MANAGER],
        },
      },
      {
        path: 'work-times',
        name: 'work-times',
        component: WorkTimeManageView,
        meta: {
          title: '工时管理',
          roles: [ROLE_MANAGER, ROLE_EMPLOYEE],
        },
      },
      {
        path: 'statistics',
        name: 'statistics',
        component: StatisticsView,
        meta: {
          title: '统计分析',
          roles: [ROLE_ADMIN, ROLE_MANAGER, ROLE_EMPLOYEE],
        },
      },
      {
        path: 'profile',
        name: 'profile',
        component: ProfileView,
        meta: {
          title: '个人中心',
        },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫：根据 token 和角色控制页面是否允许访问。
router.beforeEach((to) => {
  const authStore = useAuthStore()

  if (to.meta.public && authStore.isLoggedIn) {
    return '/'
  }

  if (!to.meta.public && !authStore.isLoggedIn) {
    return '/login'
  }

  if (to.meta.roles && !to.meta.roles.includes(authStore.userRole)) {
    return '/'
  }

  return true
})

export default router
