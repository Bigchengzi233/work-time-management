import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/index.css'
import App from './App.vue'
import router from './router'

// 创建 Vue 应用实例。
const app = createApp(App)

// 注册 Pinia，用来保存登录 token 和用户信息。
app.use(createPinia())

// 注册 Vue Router，用来控制 /login、/、/work-times 等页面跳转。
app.use(router)

// 注册 Element Plus，用来使用输入框、按钮、菜单、消息提示等组件。
app.use(ElementPlus)

// 把应用挂载到 index.html 中的 #app 节点。
app.mount('#app')
