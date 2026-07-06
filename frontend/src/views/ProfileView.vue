<template>
  <section class="management-page profile-page">
    <div class="management-header">
      <div>
        <span class="card-eyebrow">个人中心</span>
        <h2>{{ profileData?.userName || authStore.user?.userName }}</h2>
        <p>查看当前登录账号信息，并维护姓名、邮箱和登录密码。</p>
      </div>

      <el-button :icon="Refresh" @click="loadProfile">
        刷新信息
      </el-button>
    </div>

    <div class="profile-layout">
      <div class="surface-panel profile-panel">
        <span class="card-eyebrow">账号信息</span>

        <div class="profile-account-head">
          <div class="profile-avatar-large">
            {{ avatarText }}
          </div>
          <div>
            <h3>{{ profileData?.userName || authStore.user?.userName }}</h3>
            <p>{{ getRoleName(authStore.userRole) }} · {{ profileData?.deptName || '暂无部门' }}</p>
          </div>
        </div>

        <div class="info-list profile-info-list">
          <div>
            <span>用户编号</span>
            <strong>{{ profileData?.userId || '-' }}</strong>
          </div>
          <div>
            <span>手机号</span>
            <strong>{{ profileData?.phone || '-' }}</strong>
          </div>
          <div>
            <span>职位</span>
            <strong>{{ getRoleName(authStore.userRole) }}</strong>
          </div>
          <div>
            <span>部门</span>
            <strong>{{ profileData?.deptName || '暂无' }}</strong>
          </div>
        </div>
      </div>

      <div class="surface-panel profile-panel">
        <span class="card-eyebrow">个人资料</span>
        <h2>修改基础资料</h2>

        <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" class="profile-edit-form" label-width="76px">
          <el-form-item label="姓名" prop="userName">
            <el-input v-model.trim="profileForm.userName" maxlength="50" placeholder="请输入姓名" show-word-limit />
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model.trim="profileForm.email" maxlength="100" placeholder="可填写常用邮箱" show-word-limit />
          </el-form-item>

          <el-form-item class="profile-form-actions">
            <el-button type="primary" :loading="profileSaving" @click="handleSaveProfile">
              保存资料
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="surface-panel profile-panel">
        <span class="card-eyebrow">账号安全</span>
        <h2>修改登录密码</h2>

        <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" class="profile-edit-form" label-width="96px">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input
              v-model.trim="passwordForm.oldPassword"
              maxlength="50"
              placeholder="请输入当前密码"
              show-password
              type="password"
            />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model.trim="passwordForm.newPassword"
              maxlength="50"
              placeholder="请输入新密码"
              show-password
              type="password"
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model.trim="passwordForm.confirmPassword"
              maxlength="50"
              placeholder="请再次输入新密码"
              show-password
              type="password"
            />
          </el-form-item>

          <el-form-item class="profile-form-actions">
            <el-button type="primary" :loading="passwordSaving" @click="handleSavePassword">
              修改密码
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getProfileApi, updatePasswordApi, updateProfileApi } from '../api/profile'
import { useAuthStore } from '../stores/auth'
import { getRoleName } from '../utils/role'

const authStore = useAuthStore()
const profileFormRef = ref()
const passwordFormRef = ref()
const profileSaving = ref(false)
const passwordSaving = ref(false)
const profileData = ref(null)

const avatarText = computed(() => {
  const name = profileData.value?.userName || authStore.user?.userName || '用'
  return name.slice(0, 1)
})

// 个人资料表单：当前只允许用户修改姓名和邮箱，手机号、角色、部门仍由管理员维护。
const profileForm = reactive({
  userName: '',
  email: '',
})

// 修改密码表单：前端先做基础校验，后端还会再次校验原密码是否正确。
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const profileRules = {
  userName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { max: 50, message: '姓名不能超过 50 个字符', trigger: 'blur' },
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
    { max: 100, message: '邮箱不能超过 100 个字符', trigger: 'blur' },
  ],
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 50, message: '原密码长度应为 6 到 50 个字符', trigger: 'blur' },
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 50, message: '新密码长度应为 6 到 50 个字符', trigger: 'blur' },
    { validator: validateNewPassword, trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

onMounted(() => {
  loadProfile()
})

async function loadProfile() {
  const profile = await getProfileApi()
  profileData.value = profile
  profileForm.userName = profile.userName || ''
  profileForm.email = profile.email || ''
  authStore.updateUserInfo(profile)
}

async function handleSaveProfile() {
  await profileFormRef.value.validate()
  profileSaving.value = true

  try {
    const updatedProfile = await updateProfileApi({
      userName: profileForm.userName,
      email: profileForm.email,
    })

    profileData.value = updatedProfile
    authStore.updateUserInfo(updatedProfile)
    ElMessage.success('个人资料修改成功')
  } finally {
    profileSaving.value = false
  }
}

async function handleSavePassword() {
  await passwordFormRef.value.validate()
  passwordSaving.value = true

  try {
    await updatePasswordApi({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    })

    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    passwordFormRef.value.clearValidate()
    ElMessage.success('密码修改成功，下次登录请使用新密码')
  } finally {
    passwordSaving.value = false
  }
}

function validateNewPassword(rule, value, callback) {
  if (value && value === passwordForm.oldPassword) {
    callback(new Error('新密码不能和原密码相同'))
    return
  }

  callback()
}

function validateConfirmPassword(rule, value, callback) {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
    return
  }

  callback()
}
</script>
