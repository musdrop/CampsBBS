<template>
  <div class="common-layout">
    <el-container>
      <!--顶部导航栏-->
      <el-header class="header">
        <el-menu class="el-menu" mode="horizontal" :ellipsis="false" @select="handleSelect" router
          :default-active="defaultActive">
          <el-menu-item index="/home" class="logo-item">
            <template #title>
              <el-icon><home-filled /></el-icon>
              <span>NUAA校园论坛</span>
            </template>
          </el-menu-item>
          <!--搜索栏-->
          <div class="search-container">
            <div class="search-wrapper">
              <el-icon class="search-icon-prefix">
                <search />
              </el-icon>
              <el-input v-model="queryForm.title" clearable placeholder="输入标题搜索" class="search-input"
                @input="handleSearch">
              </el-input>
            </div>
          </div>
          <div class="flex-grow" />
          <!--各功能页面-->
          <el-menu-item index="/publish" class="nav-item">
            <template #title>
              <el-icon>
                <edit />
              </el-icon>
              <span>发帖</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/profile" class="nav-item">
            <template #title>
              <el-icon>
                <user />
              </el-icon>
              <span>个人中心</span>
            </template>
          </el-menu-item>
          <el-sub-menu index="user-menu" class="nav-item">
            <template #title>
              <el-icon>
                <setting />
              </el-icon>
              <span>{{ userInfo.name }}</span>
            </template>
            <el-menu-item index="/logout">
              <el-icon>
                <SwitchButton />
              </el-icon>
              退出登录
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-header>
      <!--主体内容-->
      <el-main class="main">
        <router-view :searchQuery="queryForm.title"></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { HomeFilled, Edit, User, Setting, Search,SwitchButton } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance();
const route = useRoute();

// 搜索参数
let queryForm = reactive({ title: '' });

const toHome = () => {
  queryForm.title = '';
  proxy.router.push('/home');
};

const handleSearch = () => {
  // 触发搜索事件，通过路由参数传递搜索关键词
  if (proxy.router.currentRoute.value.path !== '/home') {
    proxy.router.push('/home');
  }
  // 子组件会监听searchQuery的变化来获取数据
};

//用户信息
const userInfo = reactive({
  name: "",
  account: "",
});

const defaultActive = ref("/home");

// 监听路由变化，更新选中状态
watch(() => route.path, (newPath) => {
  defaultActive.value = newPath;
}, { immediate: true });

//用户信息初始化，从cookie中获取
const init = () => {
  let loginUser = proxy.cookies.get("bbs-web");
  userInfo.name = loginUser.name;
  userInfo.account = loginUser.account;

  // 获取当前路由
  defaultActive.value = proxy.router.currentRoute.value.path;
};
init();

const handleSelect = (key, keyPath) => {
  if (key === "/logout") {
    proxy.cookies.remove("bbs-web");
    proxy.axios.get("/logout");
    proxy.router.push("/login");
  }
};
</script>

<style scoped>
.flex-grow {
  flex-grow: 1;
}

.item {
  vertical-align: text-top;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.el-menu {
  flex: 1;
  display: flex;
  align-items: center;
}

.logo-item {
  transition: all 0.3s ease;
}

.logo-item:hover {
  background-color: rgba(64, 158, 255, 0.08);
}

.nav-item {
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.nav-item:hover {
  background-color: rgba(64, 158, 255, 0.08);
}

/* 移除菜单项默认的底部边框 */
.el-menu :deep(.el-menu-item.is-active),
.el-menu :deep(.el-sub-menu.is-active) {
  border-bottom: none !important;
}

/* 自定义hover效果，添加给所有菜单项 */
.el-menu-item::after,
.el-sub-menu::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background-color: #409EFF;
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.el-menu-item:hover::after,
.el-sub-menu:hover::after {
  width: 70%;
}

/* 选中项的样式 */
.el-menu :deep(.el-menu-item.is-active),
.el-menu :deep(.el-sub-menu.is-active) {
  color: #409EFF !important;
  background-color: rgba(64, 158, 255, 0.1) !important;
}

/* 为选中项添加底部边框 */
.el-menu :deep(.el-menu-item.is-active)::after,
.el-menu :deep(.el-sub-menu.is-active)::after {
  width: 70%;
}

.search-container {
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.search-wrapper {
  border-radius: 24px;
  background-color: #f0f0f0;
  padding: 2px 12px;
  box-shadow: inset 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  width: 30vw;
  display: flex;
  align-items: center;
}

.search-wrapper:hover {
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.15);
}

.search-icon-prefix {
  color: #909399;
  font-size: 18px;
  margin-right: 8px;
}

.search-input {
  height: 40px;
  background-color: transparent;
  border: none;
  flex: 1;
}

.search-input :deep(.el-input__wrapper) {
  background-color: transparent;
  border: none;
  box-shadow: none !important;
  padding-right: 0;
}

.search-input :deep(.el-input__suffix) {
  right: 5px;
}

.main {
  position: fixed;
  width: 100vw;
  top: 60px;
  left: 0;
  padding: 0px;
  background-color: #f5f5f5;
}
</style>
