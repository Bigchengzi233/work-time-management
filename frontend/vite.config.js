import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Vite 配置文件：告诉 Vite 当前项目使用 Vue 插件解析 .vue 单文件组件。
export default defineConfig({
  plugins: [vue()],
})
