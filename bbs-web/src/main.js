import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { ElMessage } from 'element-plus'
import './style.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import axios from './axios'
import global from './global'
import cookies from 'vue-cookies'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }
  
  app.use(ElementPlus,{
    locale: zhCn,
  })
  
  axios.defaults.withCredentials = true
  app.config.globalProperties.axios = axios
  app.config.globalProperties.global = global
  app.config.globalProperties.router = router
  app.config.globalProperties.cookies = cookies
  app.config.globalProperties.msg = ElMessage
  app.mount('#app')