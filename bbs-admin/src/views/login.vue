<template>
    <div class="login">
        <!--表单-->
        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
            <!--标题-->
            <h2 class="title">管理员登录</h2>
            <!--账号表单项-->
            <el-form-item prop="account">
                <!--输入框-->
                <el-input v-model="loginForm.account" clearable prefix-icon="User" placeholder="账号" />
            </el-form-item>
            <!--密码表单项-->
            <el-form-item prop="password">
                <!--输入框-->
                <el-input type="password" v-model="loginForm.password" clearable show-password prefix-icon="Lock"
                    placeholder="密码" />
            </el-form-item>
            <!--验证码表单项-->
            <el-form-item prop="code">
                <!--输入框-->
                <el-input v-model="loginForm.code" auto-complete="off" placeholder="验证码" style="width: 66%"
                    prefix-icon="CircleCheck" v-on:keyup.enter="doLogin" />
                <!--验证码图片-->
                <div class="login-code">
                    <!--点击图片会刷新验证码-->
                    <img :src="codeImage" @click="getCode" class="login-code-img" />
                </div>
            </el-form-item>
            <!--登录按钮-->
            <el-form-item style="width: 100%;">
                <el-button type="primary" @click="doLogin" style="width: 100%;">登录</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref } from "vue";

//获取全局变量代理，以此调用全局变量
const { proxy } = getCurrentInstance();

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
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    width: 100%;
}

.title {
    margin: 0px auto 30px auto;
    text-align: center;
    color: #707070;
}

.login-form {
    border-radius: 6px;
    background: #ffffff;
    padding: 30px 30px 10px 30px;
    min-width: 250px;
    width: 25vw;

    .el-input {
        height: 38px;

        input {
            height: 38px;
        }
    }

    .input-icon {
        height: 39px;
        width: 14px;
        margin-left: 2px;
    }
}

.login-tip {
    font-size: 13px;
    text-align: center;
    color: #bfbfbf;
}

.login-code {
    width: 33%;
    height: 38px;
    float: right;

    img {
        cursor: pointer;
        vertical-align: middle;
    }
}

.login-code-img {
    height: 38px;
}
</style>