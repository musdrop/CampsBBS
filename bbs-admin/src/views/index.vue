<template>
	<div class="common-layout">
		<el-container>
			<!--页面顶部导航栏-->
			<el-header style="padding: 0" class="header">
				<el-menu :default-active="defaultActive" class="el-menu" mode="horizontal" :ellipsis="false"
					@select="handleSelect" background-color="#1e293b" text-color="#fff" active-text-color="#42b883">
					<el-menu-item index="/home" class="logo-item">
						<template #title>
							<el-icon class="logo-icon">
								<Platform />
							</el-icon>
							<span class="logo-text">NUAA论坛管理系统</span>
						</template>
					</el-menu-item>
					<div class="flex-grow" />
					<el-sub-menu index="" class="user-menu">
						<template #title>
							<el-icon>
								<User />
							</el-icon>
							<span class="user-name">{{ userInfo.name }}</span>
						</template>
						<el-menu-item index="/logout">
							<el-icon>
								<SwitchButton />
							</el-icon>
							<span>退出登录</span>
						</el-menu-item>
					</el-sub-menu>
				</el-menu>
			</el-header>
			<!--页面主体部分-->
			<el-container class="main-container">
				<!--侧边栏-->
				<el-aside width="220px" height="100vh" class="aside-menu">
					<div class="aside-header">
						<el-icon>
							<Menu />
						</el-icon>
						<span>功能导航</span>
					</div>
					<el-menu :default-active="defaultActive" class="el-menu-vertical" @open="handleOpen"
						@close="handleClose" router @select="asideHandleSelect" background-color="#f8fafc">
						<el-menu-item index="/user" class="menu-item">
							<el-icon>
								<Avatar />
							</el-icon>
							<template #title>用户管理</template>
						</el-menu-item>
						<el-menu-item index="/forum" class="menu-item">
							<el-icon>
								<ChatDotSquare />
							</el-icon>
							<template #title>帖子管理</template>
						</el-menu-item>
					</el-menu>
				</el-aside>
				<!--主体内容-->
				<el-main class="main">
					<div class="content-wrapper">
						<!-- 使用最基本的router-view并强制刷新 -->
						<router-view :key="$route.fullPath"></router-view>
					</div>
				</el-main>
			</el-container>
		</el-container>
	</div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import {
	Platform, User, SwitchButton, Menu,
	Avatar, ChatDotSquare
} from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance();

//用户信息
const userInfo = reactive({
	name: "",
	account: "",
});

//组件初始化
const init = () => {
	//获取cookies中的用户信息
	let loginUser = proxy.cookies.get("bbs-admin");
	console.log(loginUser);
	//设置用户信息
	userInfo.name = loginUser.name;
	userInfo.account = loginUser.account;
};

onMounted(() => {
	init();
	// 添加进入动画
	document.querySelector('.common-layout').classList.add('animate-in');
});

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
	console.log("顶部菜单选中：", key, keyPath);
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
	else if (key === "/home") {
		// 简化路由跳转
		proxy.router.push("/home");
	}
};
//默认选中项
const defaultActive = ref("/home");

//侧边栏的选中回调函数
const asideHandleSelect = (key, keyPath) => {
	console.log("侧边栏选中：", key, keyPath);
	defaultActive.value = key;
	sessionStorage.setItem("default-active", key);

	// 强制刷新并跳转
	if (key && key !== proxy.router.currentRoute.value.path) {
		console.log("尝试跳转到路径:", key);
		// 使用replace可以彻底替换当前路由，避免历史记录问题
		proxy.router.replace(key);
	}
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
.common-layout {
	min-height: 100vh;
	background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%);
	opacity: 0;
	transition: opacity 0.5s ease;
}

.common-layout.animate-in {
	opacity: 1;
}

.flex-grow {
	flex-grow: 1;
}

.header {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	z-index: 10;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.logo-item {
	display: flex;
	align-items: center;
	transition: transform 0.3s;
}

.logo-item:hover {
	transform: scale(1.02);
}

.logo-icon {
	margin-right: 8px;
	font-size: 20px;
}

.logo-text {
	font-weight: 600;
	letter-spacing: 0.5px;
	font-size: 18px;
}

.user-menu {
	transition: background 0.3s;
}

.user-menu:hover {
	background-color: rgba(255, 255, 255, 0.1) !important;
}

.user-name {
	margin-left: 5px;
}

.main-container {
	position: relative;
	margin-top: 60px;
}

.aside-menu {
	position: fixed;
	top: 60px;
	left: 0;
	bottom: 0;
	z-index: 9;
	background-color: #f8fafc;
	box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05);
	transition: width 0.3s;
}

.aside-header {
	padding: 20px 16px;
	color: #1e293b;
	font-weight: 600;
	font-size: 16px;
	display: flex;
	align-items: center;
	gap: 10px;
	border-bottom: 1px solid #e2e8f0;
}

.el-menu-vertical {
	border-right: none !important;
}

.menu-item {
	margin: 8px 0;
	border-radius: 6px;
	transition: all 0.3s;
}

.menu-item:hover {
	background-color: #e6f7ff !important;
	transform: translateX(5px);
}

.main {
	position: fixed;
	width: calc(100% - 220px);
	top: 60px;
	left: 220px;
	bottom: 0;
	padding: 24px;
	overflow-y: auto;
	box-sizing: border-box;
}

.content-wrapper {
	background-color: white;
	border-radius: 12px;
	padding: 20px;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
	min-height: calc(100vh - 130px);
	box-sizing: border-box;
}

/* 页面切换动画 */
/* .fade-enter-active,
.fade-leave-active {
	transition: opacity 0.3s, transform 0.3s;
}

.fade-enter-from,
.fade-leave-to {
	opacity: 0;
	transform: translateY(10px);
} */
</style>
