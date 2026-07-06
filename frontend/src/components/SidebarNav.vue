<template>
  <aside class="sidebar">
    <div class="brand">
      <div class="brand-mark">工</div>
      <div class="brand-text">
        <div class="brand-title">员工工时管理系统</div>
        <div class="brand-subtitle">Work Time Console</div>
      </div>
    </div>

    <div class="nav-section-title">工作台</div>

    <el-menu
      class="sidebar-menu"
      :default-active="activePath"
      router
      background-color="transparent"
      text-color="#31302e"
      active-text-color="#0075de"
    >
      <el-menu-item v-for="item in visibleMenus" :key="item.path" :index="item.path">
        <el-icon>
          <component :is="item.icon" />
        </el-icon>
        <span>{{ item.title }}</span>
      </el-menu-item>
    </el-menu>

    <div class="sidebar-footer">
      <div class="sidebar-footer-row">
        <span>职位：</span>
        <strong>{{ getRoleName(authStore.userRole) }}</strong>
      </div>
      <div class="sidebar-footer-row">
        <span>部门：</span>
        <strong>{{ authStore.user?.deptName || '暂无部门' }}</strong>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Clock, DataAnalysis, DocumentChecked, Files, House, User, UserFilled } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'
import { getRoleName, ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_MANAGER } from '../utils/role'

const route = useRoute()
const authStore = useAuthStore()

// 左侧导航配置：只控制入口展示，真正的权限校验仍然要由后端完成。
const menus = [
  {
    title: '首页工作台',
    path: '/',
    icon: House,
  },
  {
    title: '基础数据管理',
    path: '/base-data',
    icon: Files,
    roles: [ROLE_ADMIN],
  },
  {
    title: '项目授权管理',
    path: '/user-projects',
    icon: UserFilled,
    roles: [ROLE_MANAGER],
  },
  {
    title: '工时管理',
    path: '/work-times',
    icon: Clock,
    roles: [ROLE_MANAGER, ROLE_EMPLOYEE],
  },
  {
    title: '操作日志',
    path: '/work-time-logs',
    icon: DocumentChecked,
    roles: [ROLE_ADMIN, ROLE_MANAGER, ROLE_EMPLOYEE],
  },
  {
    title: '统计分析',
    path: '/statistics',
    icon: DataAnalysis,
    roles: [ROLE_ADMIN, ROLE_MANAGER, ROLE_EMPLOYEE],
  },
  {
    title: '个人中心',
    path: '/profile',
    icon: User,
  },
]

// 首页路径特殊处理：当前地址是 / 时，高亮首页菜单。
const activePath = computed(() => route.path || '/')

// 根据当前用户角色过滤菜单，避免不同角色看到无权限入口。
const visibleMenus = computed(() =>
  menus.filter((item) => !item.roles || item.roles.includes(authStore.userRole)),
)
</script>
