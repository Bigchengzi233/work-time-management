<template>
  <section class="surface-panel profile-panel">
    <span class="card-eyebrow">个人中心</span>
    <h2>{{ authStore.user?.userName }}</h2>

    <div class="info-list">
      <div>
        <span>手机号</span>
        <strong>{{ authStore.user?.phone }}</strong>
      </div>
      <div>
        <span>角色</span>
        <strong>{{ getRoleName(authStore.userRole) }}</strong>
      </div>
      <div>
        <span>部门</span>
        <strong>{{ authStore.user?.deptName || '暂无' }}</strong>
      </div>
    </div>
  </section>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import { getRoleName } from '../utils/role'

const authStore = useAuthStore()
</script>
