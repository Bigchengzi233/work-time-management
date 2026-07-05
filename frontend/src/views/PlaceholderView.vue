<template>
  <section class="surface-panel placeholder-panel">
    <span class="card-eyebrow">待开发模块</span>
    <h2>{{ route.meta.title }}</h2>
    <p>这个页面当前只用于验证前端路由和角色菜单，具体业务表格与表单会在后续阶段逐步接入。</p>
  </section>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>
