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
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown, Bell } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { listPendingWorkTimesByManagerIdApi, listWorkTimesByUserIdApi } from '../api/workTimes'
import { useAuthStore } from '../stores/auth'
import { getRoleName, ROLE_EMPLOYEE, ROLE_MANAGER } from '../utils/role'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const notificationCount = ref(0)

// 顶部标题直接取当前路由的 meta.title。
const pageTitle = computed(() => route.meta.title || '员工工时管理系统')

// 头像文字取用户姓名首字，缺省时展示“工”。
const userInitial = computed(() => authStore.user?.userName?.slice(0, 1) || '工')

// 管理员当前没有真实通知来源，所以只给经理和员工展示通知入口。
const showNotificationButton = computed(() =>
  authStore.userRole === ROLE_MANAGER || authStore.userRole === ROLE_EMPLOYEE,
)

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
    return notificationCount.value > 0 ? `${notificationCount.value} 条工时待审批` : '暂无待审批工时'
  }

  if (authStore.userRole === ROLE_EMPLOYEE) {
    return notificationCount.value > 0 ? `${notificationCount.value} 条工时被驳回` : '暂无被驳回工时'
  }

  return '暂无通知'
})

onMounted(() => {
  loadNotificationCount()
})

watch(
  () => [route.fullPath, authStore.userRole, authStore.user?.userId],
  () => {
    loadNotificationCount()
  },
)

async function loadNotificationCount() {
  if (!authStore.user?.userId) {
    notificationCount.value = 0
    return
  }

  if (authStore.userRole === ROLE_MANAGER) {
    const pendingList = await listPendingWorkTimesByManagerIdApi(authStore.user.userId)
    notificationCount.value = pendingList.length
    return
  }

  if (authStore.userRole === ROLE_EMPLOYEE) {
    const workTimeList = await listWorkTimesByUserIdApi(authStore.user.userId)
    notificationCount.value = workTimeList.filter((item) => item.status === 3 && item.isDeleted === 0).length
    return
  }

  notificationCount.value = 0
}

function handleNotificationClick() {
  if (authStore.userRole === ROLE_MANAGER || authStore.userRole === ROLE_EMPLOYEE) {
    router.push('/work-times')
  }
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
