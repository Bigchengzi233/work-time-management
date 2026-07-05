<template>
  <main class="login-page">
    <section class="login-shell">
      <div class="login-intro">
        <div class="login-brand">
          <div class="brand-mark">工</div>
          <span>Work Time Console</span>
        </div>

        <div class="login-copy">
          <span class="login-badge">企业工时工作台</span>
          <h1>员工工时管理系统</h1>
          <p>统一处理项目授权、工时填报、部门审批和统计分析。</p>
        </div>

      </div>

      <section class="login-card">
        <div class="login-heading">
          <h2>登录系统</h2>
          <p>请输入账号信息继续。</p>
        </div>

        <el-form ref="loginFormRef" :model="form" :rules="rules" size="large" @submit.prevent>
          <el-form-item prop="phone">
            <el-input
              v-model.trim="form.phone"
              :prefix-icon="Iphone"
              maxlength="11"
              placeholder="手机号"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              :prefix-icon="Lock"
              placeholder="密码"
              show-password
              type="password"
            />
          </el-form-item>

          <el-button class="login-button" type="primary" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form>
      </section>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Iphone, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loginFormRef = ref()
const loading = ref(false)

// 登录表单数据：字段名必须和后端 LoginDTO 保持一致。
const form = reactive({
  phone: '',
  password: '',
})

// 前端基础校验：后端还会再校验一次，不能只依赖前端。
const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度应为 6 到 50 个字符', trigger: 'blur' },
  ],
}

async function handleLogin() {
  await loginFormRef.value.validate()

  try {
    loading.value = true
    await authStore.login(form)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>
