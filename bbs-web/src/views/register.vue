<template>
    <div class="register">
        <!-- 背景轮播图 -->
        <el-carousel class="background-carousel" :interval="5000" arrow="always" indicator-position="none"
            height="100vh">
            <el-carousel-item v-for="(item, index) in backgroundImages" :key="index">
                <div class="carousel-image" :style="{ backgroundImage: `url(${item})` }"></div>
            </el-carousel-item>
        </el-carousel>

        <!-- 注册表单卡片 -->
        <div class="register-card">
            <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" class="register-form">
                <!-- 标题 -->
                <h2 class="title">NUAA校园论坛 · 用户注册</h2>

                <!-- 账号表单项 -->
                <el-form-item prop="account">
                    <el-input v-model="registerForm.account" clearable prefix-icon="User" placeholder="请输入账号" />
                </el-form-item>

                <!-- 密码表单项 -->
                <el-form-item prop="password">
                    <el-input type="password" v-model="registerForm.password" clearable show-password prefix-icon="Lock"
                        placeholder="请输入密码" />
                </el-form-item>

                <!-- 确认密码表单项 -->
                <el-form-item prop="rePassword">
                    <el-input type="password" v-model="registerForm.rePassword" clearable show-password
                        prefix-icon="Lock" placeholder="请确认密码" />
                </el-form-item>

                <!-- 名称表单项 -->
                <el-form-item prop="name">
                    <el-input v-model="registerForm.name" clearable prefix-icon="User" placeholder="请输入名称" />
                </el-form-item>

                <!-- 邮箱表单项 -->
                <el-form-item prop="mail">
                    <el-input v-model="registerForm.mail" clearable prefix-icon="Message" placeholder="请输入邮箱" />
                </el-form-item>

                <!-- 按钮区域 -->
                <el-form-item class="button-container">
                    <div class="button-row">
                        <el-button type="primary" @click="doRegister" class="register-button">注册</el-button>
                        <el-button type="default" @click="toLogin" class="login-button">返回登录</el-button>
                    </div>
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

//Vue Ref对象，el-form内加了ref="registerFormRef",引用了el-form组件，它的value属性就是el-form组件对象
const registerFormRef = ref(null);

//注册表单对象，会根据当前组件内的使用情况自动生成被用到的属性
const registerForm = reactive({});

//注册函数
const doRegister = () => {
    //组件对象的validate方法会使用自己绑定的规则校验自己绑定的模型中的数据
    //验证结果valid传入一个异步箭头函数
    registerFormRef.value.validate(async (valid) => {
        //验证失败
        if (!valid) {
            proxy.msg.error("请按要求输入信息");
            return
        }
        //验证成功
        //创建请求体，为registerForm的副本
        let params = Object.assign({}, registerForm);
        //发送并等待请求结果
        let result = await proxy.axios.post('register', params);
        //失败则刷新验证码，无反应
        if (!result) {
            return
        }
        //弹窗注册成功
        proxy.msg.success("注册成功");
        //跳转login组件
        proxy.router.push('login')
    });
};
//跳转登录页面
const toLogin = () => {
    proxy.router.push('login')
};


//注册规则，属于表单el-form的用法
const registerRules = {
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
    rePassword: [
        {
            required: true,
            message: "请再次输入密码",
        },
        {
            validator: (rule, value, callback) => {
                if (registerForm.password == null) {
                    callback(new Error("请先输入密码"));
                }
                else if (value !== registerForm.password) {
                    callback(new Error("两次输入密码不一致"));
                } else {
                    callback();
                }
            },
            trigger: "blur",
        },
    ],
    name: [
        {
            required: true,
            message: "请输入名称",
        },
    ],
    mail: [
        {
            required: true,
            message: "请输入邮箱",
        },
    ],

}
</script>

<style lang="scss" scoped>
.register {
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
    filter: brightness(0.8);
    transition: all 0.5s ease;
}

// 注册卡片样式
.register-card {
    position: relative;
    z-index: 1;
    border-radius: 10px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    padding: 40px;
    min-width: 300px;
    width: 400px;
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

.register-form {
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

.button-container {
    margin-top: 30px;
    width: 100%;
}

.button-row {
    display: flex;
    justify-content: space-between;
    gap: 15px;
    width: 100%;
}

.register-button,
.login-button {
    flex: 1;
    height: 45px;
    border-radius: 8px;
    font-size: 16px;
    transition: all 0.3s ease;
}

.register-button {
    background: var(--el-color-primary);
    border: none;

    &:hover {
        background: var(--el-color-primary-light-3);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.4);
    }
}

.login-button {
    background: transparent;
    border: 1px solid var(--el-color-primary);
    color: var(--el-color-primary);

    &:hover {
        background: rgba(var(--el-color-primary-rgb), 0.1);
    }
}

/* 响应式适配 */
@media screen and (max-width: 768px) {
    .register-card {
        padding: 25px;
        min-width: 280px;
    }

    .title {
        font-size: 1.5rem;
        margin-bottom: 20px;
    }

    .register-button,
    .login-button {
        max-width: 250px;
    }
}

@media screen and (max-height: 600px) {
    .register-card {
        padding: 20px;
    }

    .button-container {
        margin-top: 15px;
        gap: 10px;
    }
}
</style>