<template>
  <aside class="sidebar">
    <div class="brand">
      <div class="brand-mark">工</div>
      <div>
        <div class="brand-title">员工工时管理系统</div>
        <div class="brand-subtitle">Work Time</div>
      </div>
    </div>

    <el-menu
      class="sidebar-menu"
      :default-active="route.path"
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
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  Clock,
  Collection,
  DataAnalysis,
  Files,
  House,
  User,
  UserFilled,
} from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'
import { ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_MANAGER } from '../utils/role'

const route = useRoute()
const authStore = useAuthStore()

// 左侧菜单配置：后续真实业务页面完成后，这里只需要把占位页换成真实页面。
const menus = [
  {
    title: '首页仪表盘',
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

// 根据当前用户角色过滤菜单，避免不同角色看到无权限入口。
const visibleMenus = computed(() =>
  menus.filter((item) => !item.roles || item.roles.includes(authStore.userRole)),
)
</script>
