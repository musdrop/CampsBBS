<template>
  <div class="common-layout">
    <el-container>
      <!--顶部导航栏-->
      <el-header class="header">
        <el-menu
          class="el-menu"
          mode="horizontal"
          :ellipsis="false"
          @select="handleSelect"
          router
          :default-active="defaultActive"
        >
          <el-menu-item index="/home" @click="toHome">
            <template #title>
              <span>NUAA校园论坛</span>
            </template>
          </el-menu-item>
          <div class="flex-grow" />
          <!--搜索栏-->
          <el-input
            v-model="queryForm.title"
            clearable
            placeholder="输入标题搜索"
            style="padding:10px 10px 0px 0px;height:50px;width: 30vw"
          >
            <template #append>
              <el-button type="primary" @click="forumInit()">查询</el-button>
            </template>
          </el-input>
          <!--各功能页面-->
          <el-menu-item index="/publish">发帖</el-menu-item>
          <el-menu-item index="/profile">个人中心</el-menu-item>
          <el-sub-menu index="">
            <template #title>{{ userInfo.name }}</template>
            <el-menu-item index="/logout">退出登录</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-header>
      <!--主体内容-->
      <el-main class="main">
        <router-view :forumList="forumList"></router-view>
      </el-main>
    </el-container>
  </div>
</template>
<script setup>
import { getCurrentInstance, reactive, ref } from "vue";
import { useRouter } from "vue-router";

const { proxy } = getCurrentInstance();


//帖子列表
let forumList = ref([])
//查询表单
let queryForm = reactive({})
//初始化帖子列表
const forumInit = async () => {
	let param = Object.assign({}, queryForm)
	let result = await proxy.axios.post('forum/list', param)
	if (!result) {
	  return;
	}
	forumList.value = result
    proxy.router.push('/home')
    defaultActive.value="/home"
}
forumInit()


const toHome = () => {
    queryForm.title=null
    forumInit()
};

//用户信息
const userInfo = {
  name: "",
  account: "",
};
const defaultActive = ref("/forum");
//用户信息初始化，从cookie中获取
const init = () => {
  let loginUser = proxy.cookies.get("bbs-web");
  userInfo.name = loginUser.name;
  userInfo.account = loginUser.account;
  //获取当前路由
  let da = proxy.router.currentRoute.value.path;
  if (da) {
    defaultActive.value = da;
  }
};
init();

const handleOpen = () => {};
const handleClose = () => {};

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
}

.main{
    position: fixed;
    width:100vw;
    top:0;
    left:0;
}
</style>
