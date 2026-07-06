<template>
  <section class="base-data-page">
    <div class="management-header">
      <div>
        <span class="card-eyebrow">基础数据</span>
        <h2>基础数据管理</h2>
        <p>统一维护部门、用户和项目信息，为项目授权、工时填报和统计分析提供基础数据。</p>
      </div>
    </div>

    <div class="surface-panel tabs-panel">
      <el-tabs v-model="activeTab" class="base-data-tabs">
        <el-tab-pane label="部门管理" name="departments">
          <DepartmentManageView />
        </el-tab-pane>
        <el-tab-pane label="用户管理" name="users">
          <UserManageView />
        </el-tab-pane>
        <el-tab-pane label="项目管理" name="projects">
          <ProjectManageView />
        </el-tab-pane>
      </el-tabs>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import DepartmentManageView from './DepartmentManageView.vue'
import ProjectManageView from './ProjectManageView.vue'
import UserManageView from './UserManageView.vue'

// 当前选中的基础数据页签，默认先展示已完成的部门管理。
const activeTab = ref('departments')
</script>
