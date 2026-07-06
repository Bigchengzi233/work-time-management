<template>
  <section class="management-page work-time-log-page">
    <div class="management-header">
      <div>
        <span class="card-eyebrow">操作日志</span>
        <h2>工时操作日志</h2>
      </div>

      <el-button :icon="Refresh" @click="loadLogs">
        刷新日志
      </el-button>
    </div>

    <div class="surface-panel log-filter-panel">
      <el-form :model="queryForm" class="log-filter-form" label-width="76px">
        <el-form-item label="关键词">
          <el-input
            v-model.trim="queryForm.keyword"
            class="log-keyword-input"
            clearable
            placeholder="搜索员工、项目、操作人"
            @input="resetCurrentPage"
            @clear="resetCurrentPage"
          />
        </el-form-item>

        <el-form-item label="工时编号">
          <el-input-number
            v-model="queryForm.workId"
            :min="1"
            :controls="false"
            placeholder="可按工时编号筛选"
          />
        </el-form-item>

        <el-form-item label="操作类型">
          <el-select
            v-model="queryForm.operationType"
            class="log-operation-select"
            placeholder="全部类型"
            clearable
          >
            <el-option
              v-for="item in operationTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-button type="primary" :loading="loading" @click="loadLogs">
          查询
        </el-button>
        <el-button @click="handleReset">
          重置
        </el-button>
      </el-form>
    </div>

    <div class="surface-panel table-panel">
      <div class="table-toolbar">
        <span>共 {{ filteredLogList.length }} 条日志记录</span>
      </div>

      <el-table v-loading="loading" :data="pagedLogList" border stripe empty-text="暂无操作日志">
        <el-table-column prop="logId" label="日志编号" width="100" />
        <el-table-column prop="workId" label="工时编号" width="100" />
        <el-table-column prop="workUserName" label="员工姓名" width="120" />
        <el-table-column prop="userDeptName" label="员工部门" min-width="150" show-overflow-tooltip />
        <el-table-column prop="projectName" label="项目名称" min-width="170" show-overflow-tooltip />
        <el-table-column prop="workDate" label="工作日期" width="130" />
        <el-table-column label="操作类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getLogTagType(row.operationType)" effect="plain">
              {{ getLogTypeText(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="operationTime" label="操作时间" width="180" />
        <el-table-column prop="operationDesc" label="操作说明" min-width="260" show-overflow-tooltip />
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="filteredLogList.length"
          background
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { listWorkTimeLogsApi } from '../api/workTimeLogs'
import { normalizeSearchText, paginateList } from '../utils/table'

const loading = ref(false)
const logList = ref([])

// 筛选条件：只做简单筛选，方便管理员或经理快速定位某条工时的操作记录。
const queryForm = reactive({
  keyword: '',
  workId: null,
  operationType: null,
})
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
})

const operationTypeOptions = [
  { label: '新建', value: 0 },
  { label: '修改', value: 1 },
  { label: '提交', value: 2 },
  { label: '审批通过', value: 3 },
  { label: '驳回', value: 4 },
]

const filteredLogList = computed(() => {
  const keyword = normalizeSearchText(queryForm.keyword)

  if (!keyword) {
    return logList.value
  }

  return logList.value.filter((item) =>
    [
      item.logId,
      item.workId,
      item.workUserName,
      item.userDeptName,
      item.projectName,
      item.operatorName,
      item.operationDesc,
    ].some((value) => normalizeSearchText(value).includes(keyword)),
  )
})
const pagedLogList = computed(() =>
  paginateList(filteredLogList.value, pagination.currentPage, pagination.pageSize),
)

onMounted(() => {
  loadLogs()
})

async function loadLogs() {
  resetCurrentPage()
  loading.value = true

  try {
    logList.value = await listWorkTimeLogsApi({
      workId: queryForm.workId || undefined,
      operationType: queryForm.operationType ?? undefined,
    })
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.keyword = ''
  queryForm.workId = null
  queryForm.operationType = null
  loadLogs()
}

function resetCurrentPage() {
  pagination.currentPage = 1
}

function getLogTypeText(operationType) {
  const typeMap = {
    0: '新建',
    1: '修改',
    2: '提交',
    3: '审批通过',
    4: '驳回',
  }

  return typeMap[operationType] || '未知操作'
}

function getLogTagType(operationType) {
  const tagMap = {
    0: 'info',
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'danger',
  }

  return tagMap[operationType] || 'info'
}
</script>
