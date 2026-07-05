<template>
  <header class="topbar">
    <div class="topbar-title">
      <span class="topbar-kicker">员工工时管理</span>
      <h1 class="page-title">{{ pageTitle }}</h1>
    </div>

    <div class="user-panel">
      <div class="user-avatar">{{ userInitial }}</div>
      <div class="user-meta">
        <span class="user-name">{{ authStore.user?.userName || '未命名用户' }}</span>
        <span class="user-role">{{ getRoleName(authStore.userRole) }}</span>
      </div>
      <el-button class="utility-button" :icon="SwitchButton" @click="handleLogout">
        退出
      </el-button>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { getRoleName } from '../utils/role'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 顶部标题直接取当前路由的 meta.title。
const pageTitle = computed(() => route.meta.title || '员工工时管理系统')

// 头像文字取用户姓名首字，缺省时展示“工”。
const userInitial = computed(() => authStore.user?.userName?.slice(0, 1) || '工')

function handleLogout() {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>
