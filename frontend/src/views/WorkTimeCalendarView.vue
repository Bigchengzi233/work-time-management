<template>
  <section class="management-page work-calendar-page">
    <div class="management-header">
      <div>
        <span class="card-eyebrow">工时日历</span>
        <h2>本月工时日历</h2>
      </div>

      <div class="calendar-header-actions">
        <el-button @click="changeMonth(-1)">上个月</el-button>
        <el-button type="primary" plain @click="goCurrentMonth">本月</el-button>
        <el-button @click="changeMonth(1)">下个月</el-button>
      </div>
    </div>

    <div v-loading="loading" class="calendar-summary-grid">
      <article v-for="item in summaryCards" :key="item.label" class="surface-card calendar-summary-card">
        <span class="card-eyebrow">{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </article>
    </div>

    <div class="calendar-layout">
      <section class="surface-panel calendar-panel">
        <div class="calendar-panel-head">
          <h2>{{ monthTitle }}</h2>
          <div class="calendar-legend">
            <span class="legend-dot status-draft"></span>草稿
            <span class="legend-dot status-pending"></span>待审批
            <span class="legend-dot status-approved"></span>已通过
            <span class="legend-dot status-rejected"></span>已驳回
          </div>
        </div>

        <div class="calendar-weekdays">
          <span v-for="weekday in weekdays" :key="weekday">{{ weekday }}</span>
        </div>

        <div class="calendar-grid">
          <button
            v-for="day in calendarDays"
            :key="day.date"
            class="calendar-day"
            :class="{
              'is-muted': !day.isCurrentMonth,
              'is-today': day.date === todayDate,
              'is-selected': day.date === selectedDate,
              [day.statusClass]: day.statusClass,
            }"
            type="button"
            @click="selectedDate = day.date"
          >
            <span class="calendar-day-number">{{ day.dayNumber }}</span>
            <strong v-if="day.totalHours > 0">{{ day.totalHours.toFixed(1) }} 小时</strong>
            <small v-for="project in day.projects" :key="project">{{ project }}</small>
          </button>
        </div>
      </section>

      <aside class="surface-panel calendar-detail-panel">
        <span class="card-eyebrow">日期详情</span>
        <h2>{{ selectedDateText }}</h2>

        <div v-if="selectedDayItems.length > 0" class="calendar-detail-list">
          <article v-for="item in selectedDayItems" :key="item.workId" class="calendar-detail-item">
            <div>
              <strong>{{ item.projectName }}</strong>
              <span>{{ item.workHours }} 小时</span>
            </div>
            <el-tag :type="getStatusTagType(item.status)" effect="plain">
              {{ getStatusText(item.status) }}
            </el-tag>
            <p>{{ item.workDesc || '暂无工作描述' }}</p>
          </article>
        </div>

        <el-empty v-else description="当天暂无工时记录" :image-size="76" />

        <el-button type="primary" class="calendar-fill-button" @click="router.push('/work-times')">
          去工时管理
        </el-button>
      </aside>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listWorkTimesByUserIdApi } from '../api/workTimes'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const workTimeList = ref([])
const todayDate = formatDate(new Date())
const selectedMonth = ref(new Date(new Date().getFullYear(), new Date().getMonth(), 1))
const selectedDate = ref(todayDate)
const weekdays = ['一', '二', '三', '四', '五', '六', '日']

// 当前月份标题，例如“2026年07月”。
const monthTitle = computed(() => {
  const year = selectedMonth.value.getFullYear()
  const month = `${selectedMonth.value.getMonth() + 1}`.padStart(2, '0')
  return `${year}年${month}月`
})

// 过滤掉软删除工时，日历只展示业务上有效的填报记录。
const validWorkTimes = computed(() => workTimeList.value.filter((item) => item.isDeleted === 0))

// 当前月份内的工时记录，用于顶部统计。
const currentMonthWorkTimes = computed(() => {
  const monthPrefix = formatMonth(selectedMonth.value)
  return validWorkTimes.value.filter((item) => String(item.workDate).startsWith(monthPrefix))
})

// 按工作日期把工时记录分组，方便日历格子快速读取当天数据。
const workTimesByDate = computed(() => {
  const dateMap = new Map()

  validWorkTimes.value.forEach((item) => {
    const list = dateMap.get(item.workDate) || []
    list.push(item)
    dateMap.set(item.workDate, list)
  })

  return dateMap
})

// 日历固定展示 6 行，避免月份切换时布局高度跳动。
const calendarDays = computed(() => {
  const firstDay = selectedMonth.value
  const gridStart = new Date(firstDay)
  const mondayOffset = (firstDay.getDay() + 6) % 7
  gridStart.setDate(firstDay.getDate() - mondayOffset)

  return Array.from({ length: 42 }, (_, index) => {
    const currentDate = new Date(gridStart)
    currentDate.setDate(gridStart.getDate() + index)
    return buildCalendarDay(currentDate)
  })
})

const selectedDayItems = computed(() => workTimesByDate.value.get(selectedDate.value) || [])
const selectedDateText = computed(() => selectedDate.value.replaceAll('-', '/'))

const summaryCards = computed(() => {
  const filledDateCount = new Set(currentMonthWorkTimes.value.map((item) => item.workDate)).size
  const totalHours = currentMonthWorkTimes.value.reduce((sum, item) => sum + Number(item.workHours || 0), 0)
  const pendingCount = currentMonthWorkTimes.value.filter((item) => item.status === 1).length
  const rejectedCount = currentMonthWorkTimes.value.filter((item) => item.status === 3).length

  return [
    { label: '本月填报天数', value: `${filledDateCount} 天` },
    { label: '本月总工时', value: `${totalHours.toFixed(1)} 小时` },
    { label: '待审批', value: `${pendingCount} 条` },
    { label: '已驳回', value: `${rejectedCount} 条` },
  ]
})

onMounted(() => {
  loadWorkTimes()
})

async function loadWorkTimes() {
  if (!authStore.user?.userId) {
    return
  }

  loading.value = true

  try {
    workTimeList.value = await listWorkTimesByUserIdApi(authStore.user.userId)
  } finally {
    loading.value = false
  }
}

function buildCalendarDay(date) {
  const dateText = formatDate(date)
  const dayItems = workTimesByDate.value.get(dateText) || []
  const totalHours = dayItems.reduce((sum, item) => sum + Number(item.workHours || 0), 0)
  const projects = [...new Set(dayItems.map((item) => item.projectName).filter(Boolean))].slice(0, 2)

  return {
    date: dateText,
    dayNumber: date.getDate(),
    isCurrentMonth: date.getMonth() === selectedMonth.value.getMonth(),
    totalHours,
    projects,
    statusClass: getDayStatusClass(dayItems),
  }
}

function getDayStatusClass(dayItems) {
  if (dayItems.some((item) => item.status === 3)) {
    return 'status-rejected'
  }

  if (dayItems.some((item) => item.status === 1)) {
    return 'status-pending'
  }

  if (dayItems.some((item) => item.status === 0)) {
    return 'status-draft'
  }

  if (dayItems.some((item) => item.status === 2)) {
    return 'status-approved'
  }

  return ''
}

function changeMonth(step) {
  selectedMonth.value = new Date(selectedMonth.value.getFullYear(), selectedMonth.value.getMonth() + step, 1)
  selectedDate.value = formatDate(selectedMonth.value)
}

function goCurrentMonth() {
  const today = new Date()
  selectedMonth.value = new Date(today.getFullYear(), today.getMonth(), 1)
  selectedDate.value = todayDate
}

function getStatusText(status) {
  const statusMap = {
    0: '草稿',
    1: '待审批',
    2: '已通过',
    3: '已驳回',
  }

  return statusMap[status] || '未知'
}

function getStatusTagType(status) {
  const typeMap = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger',
  }

  return typeMap[status] || 'info'
}

function formatMonth(date) {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  return `${year}-${month}`
}

function formatDate(date) {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}
</script>
