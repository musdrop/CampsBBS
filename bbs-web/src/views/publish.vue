<template>
  <div class="update" style="margin-top: 5%;">
    <el-form
      ref="saveFormRef"
      :model="saveForm"
      :rules="saveRules"
      class="update-form"
      label-width="6vw"
    >
      <h2 class="title">我的贴子</h2>
      <el-form-item prop="title" label="标题">
        <el-input v-model="saveForm.title" maxlength="50" show-word-limit />
      </el-form-item>
      <el-form-item prop="coverImage" label="封面">
        <el-upload
          class="upload-demo"
          :action="uploadAction"
          :on-success="uploadSuccess"
          accept="image/*"
          with-credentials
          :limit="1"
          list-type="picture"
        >
          <el-button type="primary">点击上传</el-button>
          <template #tip>
            <div class="upload-tip">jpg/png 图片不能大于 500kb</div>
          </template>
        </el-upload>
      </el-form-item>
      <el-form-item prop="content" label="内容">
        <el-input
          type="textarea"
          v-model="saveForm.content"
          clearable
          rows="10"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
      <el-form-item style="width: 100%">
        <el-button type="primary" @click="doSave" style="width: 100%"
          >发布</el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { baseURL } from "../global/index.js";

const { proxy } = getCurrentInstance();
//表单相关
const saveFormRef = ref(null);
const saveForm = reactive({});

//提交帖子表单
const doSave = () => {
  saveFormRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    let params = Object.assign({}, saveForm);
    let result = await proxy.axios.post("forum/save", params);
    if (!result) {
      return;
    }
    proxy.msg.success("发布成功");
    proxy.router.push("/home");
  });
};

const saveRules = {
  coverImage: [
    {
      required: true,
      message: "请上传图片",
    },
  ],
  title: [
    {
      required: true,
      message: "请输入标题",
    },
  ],
  content: [
    {
      required: true,
      message: "请输入内容",
    },
  ],
};

//图片上传
const uploadAction = ref(baseURL + "/image/upload");
const uploadSuccess = (response, uploadFile, uploadFiles) => {
  if (response.code !== 0) {
    proxy.msg.error(response.msg);
  } else {
    saveForm.coverImage = response.data;
  }
};
</script>

<style lang="scss" scoped>
.update {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
  background-size: cover;
}
.title {
  margin: 0px auto 30px 40px;
  text-align: center;
  color: #707070;
}

.update-form {
  border-radius: 6px;
  background: #ffffff;
  width: 40vw;
  padding: 30px 30px 10px 30px;
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
.update-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.update-code {
  width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.update-code-img {
  height: 38px;
}
</style>
