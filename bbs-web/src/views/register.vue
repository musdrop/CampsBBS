<template>
    <div class="register">
        <!--表单-->
        <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" class="register-form">
            <!--标题-->
            <h2 class="title">用户注册</h2>
            <!--账号表单项-->
            <el-form-item prop="account" label="账号">
                <!--输入框-->
                <el-input v-model="registerForm.account" clearable/>
            </el-form-item>
            <!--密码表单项-->
            <el-form-item prop="password" label="密码">
                <!--输入框-->
                <el-input type="password" v-model="registerForm.password" clearable show-password/>
            </el-form-item>
            <!--确认密码表单项-->
            <el-form-item prop="rePassword" label="确认密码">
                <!--输入框-->
                <el-input type="password" v-model="registerForm.rePassword" clearable show-password/>
            </el-form-item>
            <!--名称表单项-->
            <el-form-item prop="name" label="名称">
                <!--输入框-->
                <el-input v-model="registerForm.name" clearable />
            </el-form-item>
            <!--邮箱表单项-->
            <el-form-item prop="mail" label="邮箱">
                <!--输入框-->
                <el-input v-model="registerForm.mail" clearable />
            </el-form-item>
            <!--注册按钮-->
            <el-form-item style="width: 100%;">
                <div style="width: 66%;">
                    <el-button type="primary" @click="doRegister" style="width: 100%;">注册</el-button>
                </div>
                <div style="width: 33%; margin-left: auto;">
                    <el-button type="primary" @click="toLogin" style="width: 100%;">去登陆</el-button>
                </div>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref } from "vue";

//获取全局变量代理，以此调用全局变量
const { proxy } = getCurrentInstance();

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

.register-form {
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

.register-tip {
    font-size: 13px;
    text-align: center;
    color: #bfbfbf;
}
</style>