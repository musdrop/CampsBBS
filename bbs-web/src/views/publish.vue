<template>
  <div class="publish-container">
    <!-- 背景装饰 -->
    <div class="background-decoration"></div>

    <el-scrollbar class="publish-scrollbar">
      <div class="publish-card">
        <el-form ref="saveFormRef" :model="saveForm" :rules="saveRules" class="publish-form" label-position="top">
          <h2 class="title">发布新帖子</h2>

          <!-- 标题输入 -->
          <el-form-item prop="title" label="标题">
            <el-input v-model="saveForm.title" maxlength="50" show-word-limit placeholder="请输入帖子标题（最多50字）"
              prefix-icon="Edit" />
          </el-form-item>

          <!-- 封面上传 -->
          <el-form-item prop="coverImage" label="封面图片">
            <el-upload class="cover-upload" :action="uploadAction" :on-success="uploadSuccess" :on-error="uploadError"
              :before-upload="beforeUpload" accept="image/*" with-credentials :limit="1" list-type="picture-card">
              <template #default>
                <el-icon>
                  <Plus />
                </el-icon>
                <span>上传封面</span>
              </template>
              <template #file="{ file }">
                <div class="uploaded-image">
                  <img :src="file.url" class="cover-preview" />
                  <div class="image-actions">
                    <el-icon @click.stop="handleRemove(file)">
                      <Delete />
                    </el-icon>
                  </div>
                </div>
              </template>
              <template #tip>
                <div class="upload-tip">支持 JPG/PNG 格式图片，建议尺寸 800×450px，不超过500KB</div>
              </template>
            </el-upload>
          </el-form-item>

          <!-- 内容输入 -->
          <el-form-item prop="content" label="内容">
            <el-input type="textarea" v-model="saveForm.content" clearable rows="8" maxlength="500" show-word-limit
              placeholder="请分享你的想法..." resize="none" />
          </el-form-item>

          <!-- 发布按钮 -->
          <el-form-item class="publish-actions">
            <div class="button-container">
              <el-button @click="resetForm" plain>重置</el-button>
              <el-button type="primary" @click="doSave" :loading="publishing">
                <el-icon>
                  <Position />
                </el-icon> 发布帖子
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref, onMounted } from "vue";
import { baseURL } from "../global/index.js";
import { Plus, Delete, Edit, Position } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const { proxy } = getCurrentInstance();

// 表单相关
const saveFormRef = ref(null);
const saveForm = reactive({});
const publishing = ref(false);

// 提交帖子表单
const doSave = () => {
  saveFormRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }

    publishing.value = true;
    try {
      let params = Object.assign({}, saveForm);
      let result = await proxy.axios.post("forum/save", params);
      if (!result) {
        return;
      }
      proxy.msg.success("发布成功");
      proxy.router.push("/home");
    } catch (error) {
      console.error("发布失败", error);
      proxy.msg.error("发布失败，请稍后重试");
    } finally {
      publishing.value = false;
    }
  });
};

// 重置表单
const resetForm = () => {
  saveFormRef.value.resetFields();
  saveForm.coverImage = null;
};

// 表单验证规则
const saveRules = {
  coverImage: [
    {
      required: true,
      message: "请上传封面图片",
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

// 图片上传相关
const uploadAction = ref(baseURL + "/image/upload");

// 上传前验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt500K = file.size / 1024 < 500;

  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return false;
  }
  if (!isLt500K) {
    ElMessage.error('图片大小不能超过 500KB!');
    return false;
  }
  return true;
};

// 上传成功回调
const uploadSuccess = (response, uploadFile, uploadFiles) => {
  if (response.code !== 0) {
    proxy.msg.error(response.msg);
  } else {
    saveForm.coverImage = response.data;
    proxy.msg.success("封面上传成功");
  }
};

// 上传失败回调
const uploadError = () => {
  proxy.msg.error("图片上传失败，请重试");
};

// 删除图片
const handleRemove = () => {
  saveForm.coverImage = null;
};

// 页面加载完成后的动画效果
onMounted(() => {
  document.querySelector('.publish-card').classList.add('active');
});
</script>

<style lang="scss" scoped>
.publish-container {
  height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.publish-scrollbar {
  flex: 1;
  height: 100%;
  padding: 0px;

  :deep(.el-scrollbar__wrap) {
    overflow-x: hidden;
  }

  :deep(.el-scrollbar__bar) {
    opacity: 0.3;
  }
}

.background-decoration {
  position: fixed;
  top: -10%;
  right: -10%;
  width: 70%;
  height: 70%;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(64, 158, 255, 0.05));
  z-index: -1;
  pointer-events: none;

  &::after {
    content: '';
    position: absolute;
    bottom: -60%;
    left: -60%;
    width: 80%;
    height: 80%;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.05), rgba(64, 158, 255, 0.02));
  }
}

.publish-card {
  width: 100%;
  max-width: 800px;
  margin: 20px auto;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
  transition: all 0.6s cubic-bezier(0.165, 0.84, 0.44, 1);
  transform: translateY(30px);
  opacity: 0;

  &.active {
    transform: translateY(0);
    opacity: 1;
  }

  &:hover {
    box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
  }
}

.title {
  font-size: 28px;
  color: #303133;
  text-align: center;
  margin-bottom: 40px;
  font-weight: 600;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: -12px;
    left: 50%;
    transform: translateX(-50%);
    width: 60px;
    height: 3px;
    background: var(--el-color-primary);
    border-radius: 3px;
  }
}

.publish-form {
  width: 100%;

  :deep(.el-form-item__label) {
    font-size: 16px;
    font-weight: 500;
    color: #606266;
    line-height: 1.5;
    padding-bottom: 8px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 8px;
    transition: all 0.3s ease;
    box-shadow: 0 0 0 1px #dcdfe6 inset;
    padding: 4px 12px;
  }

  :deep(.el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px var(--el-color-primary-light-3) inset;
  }

  :deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px var(--el-color-primary) inset;
  }

  :deep(.el-textarea__inner) {
    border-radius: 8px;
    padding: 12px;
    font-size: 15px;
    line-height: 1.6;
    transition: all 0.3s ease;

    &:focus {
      box-shadow: 0 0 8px rgba(var(--el-color-primary-rgb), 0.2);
    }
  }

  :deep(.el-input__count) {
    background: transparent;
    font-size: 12px;
    color: #909399;
  }
}

.cover-upload {
  width: 100%;

  :deep(.el-upload--picture-card) {
    width: 200px;
    height: 120px;
    border-radius: 8px;
    background-color: #f5f7fa;
    border: 1px dashed #d9d9d9;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    transition: all 0.3s;

    .el-icon {
      font-size: 24px;
      color: #909399;
      margin-bottom: 8px;
    }

    span {
      font-size: 14px;
      color: #909399;
    }

    &:hover {
      border-color: var(--el-color-primary);
      color: var(--el-color-primary);

      .el-icon,
      span {
        color: var(--el-color-primary);
      }
    }
  }

  :deep(.el-upload-list__item) {
    width: 200px;
    height: 120px;
    border-radius: 8px;
    overflow: hidden;
  }
}

.uploaded-image {
  position: relative;
  width: 100%;
  height: 100%;

  .cover-preview {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .image-actions {
    position: absolute;
    top: 0;
    right: 0;
    padding: 8px;
    background: rgba(0, 0, 0, 0.5);
    border-radius: 0 0 0 8px;
    opacity: 0;
    transition: opacity 0.3s;

    .el-icon {
      color: #fff;
      font-size: 16px;
      cursor: pointer;
    }
  }

  &:hover .image-actions {
    opacity: 1;
  }
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  line-height: 1.5;
}

.publish-actions {
  margin-top: 20px;
}

.button-container {
  display: flex;
  justify-content: flex-end;
  gap: 12px;

  .el-button {
    padding: 12px 24px;
    font-size: 16px;
    border-radius: 8px;
    transition: all 0.3s;

    .el-icon {
      margin-right: 4px;
    }
  }

  .el-button--primary {
    background: var(--el-color-primary);
    border: none;

    &:hover {
      background: var(--el-color-primary-light-3);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.4);
    }
  }
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .publish-card {
    padding: 30px 20px;
    margin: 10px auto;
  }

  .publish-scrollbar {
    padding: 10px;
  }

  .title {
    font-size: 24px;
    margin-bottom: 30px;
  }

  .button-container {
    width: 100%;

    .el-button {
      flex: 1;
    }
  }
}

@media screen and (max-height: 700px) {
  .publish-card {
    margin: 10px auto;
    padding: 20px;
  }

  .title {
    font-size: 22px;
    margin-bottom: 20px;

    &::after {
      bottom: -8px;
      width: 40px;
      height: 2px;
    }
  }

  .publish-form {
    :deep(.el-form-item) {
      margin-bottom: 12px;
    }

    :deep(.el-form-item__label) {
      padding-bottom: 4px;
    }
  }

  .cover-upload {
    :deep(.el-upload--picture-card) {
      height: 100px;
    }

    :deep(.el-upload-list__item) {
      height: 100px;
    }
  }

  .button-container .el-button {
    padding: 8px 16px;
  }
}
</style>
