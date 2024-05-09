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
//创建app
const app = createApp(App)

//挂载pinia
app.use(createPinia())
//挂载router
app.use(router)

//注册element-plus的图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }
  
  //设置element-plus的语言为中文
  app.use(ElementPlus,{
    locale: zhCn,
  })
  //设置
  axios.defaults.withCredentials = true
  //全局变量axios，使用同一个axios实例
  app.config.globalProperties.axios = axios
  //用于存储全局变量,从global文件夹下的index.js中导入
  app.config.globalProperties.global = global
  //element-plus的消息提示框方法
  app.config.globalProperties.msg = ElMessage
  //全局变量router，使用同一个router实例
  app.config.globalProperties.router = router
  //全局变量cookies，使用同一个cookies实例
  app.config.globalProperties.cookies = cookies
  //挂载app
  app.mount('#app')