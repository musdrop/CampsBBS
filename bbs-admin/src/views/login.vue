<template>
    <div class="login">
        <!-- 背景轮播图 -->
        <el-carousel class="background-carousel" :interval="5000" arrow="always" indicator-position="none"
            height="100vh">
            <el-carousel-item v-for="(item, index) in backgroundImages" :key="index">
                <div class="carousel-image" :style="{ backgroundImage: `url(${item})` }"></div>
            </el-carousel-item>
        </el-carousel>

        <!-- 登录表单卡片 -->
        <div class="login-card">
            <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
                <!-- 标题 -->
                <h2 class="title">校园论坛 · 管理员登录</h2>

                <!-- 账号表单项 -->
                <el-form-item prop="account">
                    <el-input v-model="loginForm.account" clearable prefix-icon="User" placeholder="请输入管理员账号" />
                </el-form-item>

                <!-- 密码表单项 -->
                <el-form-item prop="password">
                    <el-input type="password" v-model="loginForm.password" clearable show-password prefix-icon="Lock"
                        placeholder="请输入密码" />
                </el-form-item>

                <!-- 验证码表单项 -->
                <el-form-item prop="code">
                    <div class="code-container">
                        <el-input v-model="loginForm.code" auto-complete="off" placeholder="请输入验证码"
                            prefix-icon="CircleCheck" v-on:keyup.enter="doLogin" />
                        <!-- 验证码图片 -->
                        <div class="login-code">
                            <img :src="codeImage" @click="getCode" class="login-code-img" />
                        </div>
                    </div>
                </el-form-item>

                <!-- 登录按钮 -->
                <el-form-item class="button-container">
                    <el-button type="primary" @click="doLogin" class="login-button">管理员登录</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref } from "vue";

//获取全局变量代理，以此调用全局变量
const { proxy } = getCurrentInstance();

// 背景轮播图数组
const backgroundImages = [
    "https://x.nuaa.edu.cn/https/77726476706e69737468656265737421f1e2559434357a467b1ac7a28d54227b848842e5509e/authserver/nuaaTheme/static/web/images/banner1.jpg?vpn-1",
    "https://x.nuaa.edu.cn/https/77726476706e69737468656265737421f1e2559434357a467b1ac7a28d54227b848842e5509e/authserver/nuaaTheme/static/web/images/banner2.jpg?vpn-1",
    "https://x.nuaa.edu.cn/https/77726476706e69737468656265737421f1e2559434357a467b1ac7a28d54227b848842e5509e/authserver/nuaaTheme/static/web/images/banner3.jpg?vpn-1",
    "https://x.nuaa.edu.cn/https/77726476706e69737468656265737421f1e2559434357a467b1ac7a28d54227b848842e5509e/authserver/nuaaTheme/static/web/images/banner4.jpg?vpn-1",
    "https://x.nuaa.edu.cn/https/77726476706e69737468656265737421f1e2559434357a467b1ac7a28d54227b848842e5509e/authserver/nuaaTheme/static/web/images/banner5.jpg?vpn-1",
];

//Vue Ref对象，el-form内加了ref="loginFormRef",引用了el-form组件，它的value属性就是el-form组件对象
const loginFormRef = ref(null);

//登录表单对象，会根据当前组件内的使用情况自动生成被用到的属性
const loginForm = reactive({});

//登录函数
const doLogin = () => {
    //组件对象的validate方法会使用自己绑定的规则校验自己绑定的模型中的数据
    //验证结果valid传入一个异步箭头函数
    loginFormRef.value.validate(async (valid) => {
        //验证失败
        if (!valid) {
            proxy.msg.error("请输入完整信息");
            return
        }
        //验证成功
        //创建请求体，为loginForm的副本
        let params = Object.assign({}, loginForm);
        //发送并等待请求结果
        let result = await proxy.axios.post('login', params);
        //失败则刷新验证码，无反应
        if (!result) {
            getCode()
            return
        }
        //成功，先保存cookies
        proxy.cookies.set("bbs-admin", result, 0);
        //清楚之前的会话状态
        sessionStorage.removeItem('default-active')
        //弹窗登录成功
        proxy.msg.success("登录成功");
        //跳转home组件
        proxy.router.push('home')
    });
};

//验证码图片
const codeImage = ref('')
//验证码id
const codeId = ref('')
//定义向服务器请求验证码图片的函数
const getCode = async () => {
    //请求验证码图片
    let result = await proxy.axios.get('login-code');
    if (!result) {
        return;
    }
    //保存验证码图片和验证码id
    codeImage.value = result.image
    codeId.value = result.codeId
    //将codeId写入登录表单对象中，到时候发回给服务器要用
    loginForm.codeId = result.codeId
}
//立即执行请求验证码图片
getCode()

//登录规则，属于表单el-form的用法
const loginRules = {
    //message为校验失败后对应表单元素下方弹出的提示
    account: [
        {
            required: true,
            message: "请输入账号",
        },
    ],
    password: [
        {
            required: true,
            message: "请输入密码",
        },
    ],
    code: [
        {
            required: true,
            message: "请输入验证码",
        },
    ]
}
</script>

<style lang="scss" scoped>
.login {
    position: fixed;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    width: 100vw;
    top: 0;
    left: 0;
    overflow: hidden;
}

// 背景轮播图样式
.background-carousel {
    position: absolute;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    z-index: 0;

    :deep(.el-carousel__container) {
        height: 100%;
    }

    :deep(.el-carousel__item) {
        height: 100%;
        overflow: hidden;
    }
}

.carousel-image {
    width: 100%;
    height: 100%;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    filter: brightness(0.7);
    transition: all 0.5s ease;
}

// 登录卡片样式
.login-card {
    position: relative;
    z-index: 1;
    border-radius: 10px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
    padding: 40px;
    min-width: 320px;
    width: 380px;
    max-width: 90%;
    animation: fadeIn 0.8s ease-in-out;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(20px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.title {
    margin: 0 0 30px 0;
    text-align: center;
    color: #333;
    font-size: 1.8rem;
    font-weight: 500;
}

.login-form {
    width: 100%;

    .el-input {
        --el-input-height: 45px;
        margin-bottom: 5px;
    }

    :deep(.el-input__wrapper) {
        border-radius: 8px;
        transition: all 0.3s ease;
    }

    :deep(.el-input__wrapper:hover) {
        box-shadow: 0 0 0 1px var(--el-color-primary) inset;
    }

    :deep(.el-input__inner) {
        font-size: 16px;
    }
}

.code-container {
    display: flex;
    align-items: center;
    gap: 10px;

    .el-input {
        flex: 1;
    }
}

.login-code {
    height: 45px;
    flex-shrink: 0;

    .login-code-img {
        height: 100%;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.2s;

        &:hover {
            transform: scale(1.03);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
    }
}

.button-container {
    margin-top: 30px;
    width: 100%;
}

.login-button {
    width: 100%;
    height: 45px;
    border-radius: 8px;
    font-size: 16px;
    background: var(--el-color-primary);
    border: none;
    transition: all 0.3s ease;

    &:hover {
        background: var(--el-color-primary-light-3);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.4);
    }
}

/* 响应式适配 */
@media screen and (max-width: 768px) {
    .login-card {
        padding: 25px;
        min-width: 280px;
    }

    .title {
        font-size: 1.5rem;
        margin-bottom: 20px;
    }
}

@media screen and (max-height: 600px) {
    .login-card {
        padding: 20px;
    }

    .button-container {
        margin-top: 15px;
    }
}
</style>