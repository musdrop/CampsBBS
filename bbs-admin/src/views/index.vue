<template>
	<div class="common-layout">
		<el-container>
			<!--页面顶部导航栏-->
			<el-header style="padding: 0" class="header">
				<el-menu :default-active="defaultActive" class="el-menu" mode="horizontal" :ellipsis="false"
					@select="handleSelect" background-color="#545c64" text-color="#fff" active-text-color="#ffd04b">
					<el-menu-item index="/home">
						<template #title>
							<span>NUAA论坛管理系统</span>
						</template>
					</el-menu-item>
					<div class="flex-grow"/>
					<el-sub-menu index="">
						<template #title>{{ userInfo.name }}</template>
						<el-menu-item index="/logout">退出登录</el-menu-item>
					</el-sub-menu>
				</el-menu>
			</el-header>
			<!--页面主体部分-->
			<el-container>
				<!--侧边栏-->
				<el-aside width="200px" height="100vh" class="aside-menu">
					<el-menu :default-active="defaultActive" class="el-menu-vertical" @open="handleOpen"
						@close="handleClose" router @select="asideHandleSelect">
						<el-menu-item index="/user">用户管理</el-menu-item>
						<el-menu-item index="/forum">帖子管理</el-menu-item>
					</el-menu>
				</el-aside>
				<!--主体内容-->
				<el-main class="main">
					<router-view></router-view>
				</el-main>
			</el-container>
		</el-container>
	</div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref } from "vue";
import { useRouter } from "vue-router";

const { proxy } = getCurrentInstance();

//用户信息
const userInfo = {
	name: "",
	account: "",
};

//组件初始化
const init = () => {
	//获取cookies中的用户信息
	let loginUser = proxy.cookies.get("bbs-admin");
	console.log(loginUser);
	//设置用户信息
	userInfo.name = loginUser.name;
	userInfo.account = loginUser.account;
};
init();

//sub-menu的展开回调函数
const handleOpen = () => {
	console.log("open");
};
//sub-menu的关闭回调函数
const handleClose = () => {
	console.log("close");
};

//顶部导航栏的菜单的选中回调函数
const handleSelect = (key, keyPath) => {
	console.log(keyPath);
	//更改默认选中项
	defaultActive.value = key;
	//设置sessionStorage
	sessionStorage.setItem("default-active", key);
	//退出登录
	if (key === "/logout") {
		//清除cookies
		proxy.cookies.remove("bbs-admin");
		//跳转到登录页面
		proxy.router.push("/login");
	}
	//跳转到主页
	if (key === "/home") {
		proxy.router.push("/home");
	}
};
//默认选中项
const defaultActive = ref("/home");

//侧边栏的选中回调函数
const asideHandleSelect = (key, keyPath) => {
	console.log(key, keyPath);
	defaultActive.value = key;
	sessionStorage.setItem("default-active", key);
};

//侧边栏初始化
const asideInit = () => {
	//获取当前路径
	let da = proxy.router.currentRoute.value.path;
	//如果不为空
	if (da) {
		//设置为默认选中项
		defaultActive.value = da;
	}
};
asideInit();
</script>

<style scoped>
.flex-grow {
	flex-grow: 1;
}

.header {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	z-index: 10;
}

.aside-menu {
	position: fixed;
	top: 60px;
	left: 0;
	right: 0;
	z-index: 10;
}
.main {
	position: fixed;
	width: 85vw;
	top: 80px;
	left: 180px;
}
</style>
