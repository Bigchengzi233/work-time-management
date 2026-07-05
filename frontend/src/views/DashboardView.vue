<template>
  <section class="dashboard">
    <div class="dashboard-grid">
      <article class="surface-card">
        <span class="card-eyebrow">当前身份</span>
        <strong>{{ getRoleName(authStore.userRole) }}</strong>
        <p>{{ authStore.user?.deptName || '暂无部门信息' }}</p>
      </article>

      <article class="surface-card">
        <span class="card-eyebrow">登录状态</span>
        <strong>已登录</strong>
        <p>token 已保存在浏览器本地存储中。</p>
      </article>

      <article class="surface-card">
        <span class="card-eyebrow">下一步</span>
        <strong>业务页面</strong>
        <p>后续会按模块接入部门、用户、项目和工时接口。</p>
      </article>
    </div>

    <article class="surface-panel">
      <h2>系统首页</h2>
      <p>
        这里是前端第一阶段的主布局验证页面。当前阶段重点是登录闭环、路由守卫、token 保存和角色菜单。
      </p>
    </article>
  </section>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import { getRoleName } from '../utils/role'

const authStore = useAuthStore()
</script>
