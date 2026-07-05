<template>
  <section class="dashboard">
    <div class="workspace-hero">
      <div>
        <span class="card-eyebrow">{{ getRoleName(authStore.userRole) }}工作台</span>
        <h2>{{ welcomeTitle }}</h2>
        <p>{{ welcomeText }}</p>
      </div>
      <el-button type="primary" @click="router.push(primaryAction.path)">
        {{ primaryAction.title }}
      </el-button>
    </div>

    <div class="dashboard-grid">
      <article v-for="item in summaryCards" :key="item.label" class="surface-card metric-card">
        <span class="card-eyebrow">{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <p>{{ item.desc }}</p>
      </article>
    </div>

    <div class="dashboard-layout">
      <article class="surface-panel task-panel">
        <div class="panel-heading">
          <div>
            <span class="card-eyebrow">快捷入口</span>
            <h2>常用操作</h2>
          </div>
        </div>

        <div class="action-list">
          <button
            v-for="action in quickActions"
            :key="action.title"
            class="action-item"
            type="button"
            @click="router.push(action.path)"
          >
            <span>{{ action.title }}</span>
            <small>{{ action.desc }}</small>
          </button>
        </div>
      </article>

      <article class="surface-panel process-panel">
        <span class="card-eyebrow">业务流程</span>
        <h2>{{ processTitle }}</h2>
        <div class="process-list">
          <div v-for="item in processSteps" :key="item.title" class="process-item">
            <span>{{ item.step }}</span>
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.desc }}</p>
            </div>
          </div>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getRoleName, ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_MANAGER } from '../utils/role'

const router = useRouter()
const authStore = useAuthStore()

const roleConfig = {
  [ROLE_ADMIN]: {
    title: '管理基础数据，掌握公司工时情况',
    text: '当前重点是维护部门、用户和项目，并查看公司维度的工时统计。',
    primaryAction: {
      title: '进入基础数据',
      path: '/base-data',
    },
    actions: [
      { title: '基础数据管理', desc: '维护部门、用户和项目', path: '/base-data' },
      { title: '统计分析', desc: '查看公司工时统计', path: '/statistics' },
      { title: '个人中心', desc: '查看当前登录信息', path: '/profile' },
    ],
    processTitle: '管理员工作流',
    processSteps: [
      { step: '01', title: '维护组织数据', desc: '先保证部门、用户和项目数据准确。' },
      { step: '02', title: '跟踪工时数据', desc: '通过统计页面查看公司整体工时投入。' },
      { step: '03', title: '支持后续迭代', desc: '为审批和统计模块提供稳定基础数据。' },
    ],
  },
  [ROLE_MANAGER]: {
    title: '处理项目授权，审批本部门工时',
    text: '当前重点是为员工分配项目，并审核本部门员工提交的工时。',
    primaryAction: {
      title: '进入工时审批',
      path: '/work-times',
    },
    actions: [
      { title: '项目授权管理', desc: '给本部门员工分配项目', path: '/user-projects' },
      { title: '工时管理', desc: '查看待审批工时报单', path: '/work-times' },
      { title: '统计分析', desc: '查看部门工时统计', path: '/statistics' },
    ],
    processTitle: '部门经理工作流',
    processSteps: [
      { step: '01', title: '分配项目', desc: '员工只能填报已授权项目。' },
      { step: '02', title: '审核工时', desc: '对待审批工时执行通过或驳回。' },
      { step: '03', title: '查看统计', desc: '按部门维度分析工时投入。' },
    ],
  },
  [ROLE_EMPLOYEE]: {
    title: '填报个人工时，跟踪审批状态',
    text: '当前重点是按项目填写每日工时，并查看提交后的审批结果。',
    primaryAction: {
      title: '进入工时填报',
      path: '/work-times',
    },
    actions: [
      { title: '工时管理', desc: '新增、修改、提交本人工时', path: '/work-times' },
      { title: '统计分析', desc: '查看个人工时统计', path: '/statistics' },
      { title: '个人中心', desc: '查看账号与部门信息', path: '/profile' },
    ],
    processTitle: '员工工作流',
    processSteps: [
      { step: '01', title: '选择项目', desc: '只能选择自己已授权的项目。' },
      { step: '02', title: '填写工时', desc: '按日期填写工作时长和描述。' },
      { step: '03', title: '提交审批', desc: '提交后等待部门经理审核。' },
    ],
  },
}

// 当前角色没有匹配时，默认按员工工作台展示，避免页面空白。
const currentConfig = computed(() => roleConfig[authStore.userRole] || roleConfig[ROLE_EMPLOYEE])

const welcomeTitle = computed(() => currentConfig.value.title)
const welcomeText = computed(() => currentConfig.value.text)
const primaryAction = computed(() => currentConfig.value.primaryAction)
const quickActions = computed(() => currentConfig.value.actions)
const processTitle = computed(() => currentConfig.value.processTitle)
const processSteps = computed(() => currentConfig.value.processSteps)

const summaryCards = computed(() => [
  {
    label: '当前身份',
    value: getRoleName(authStore.userRole),
    desc: '菜单入口会根据当前角色自动调整。',
  },
  {
    label: '所属部门',
    value: authStore.user?.deptName || '暂无部门',
    desc: '部门范围会影响授权、审批和统计数据。',
  },
  {
    label: '登录状态',
    value: '已登录',
    desc: 'token 已保存，刷新页面后仍会保持登录。',
  },
])
</script>
