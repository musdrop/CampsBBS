<template>
  <div class="home-container">
    <el-scrollbar height="85vh">
      <!--帖子每行-->
      <el-row :gutter="24" v-for="list in forumList" :key="list">
        <!--帖子行中的每列-->
        <el-col :span="5" v-for="forum in list" :key="forum">
          <!--帖子卡片-->
          <el-card shadow="hover" class="box-card" @click="handleDetail(forum)">
            <!--封面图片-->
            <el-image :src="imageBaseURL + forum.coverImage" style="height: 100px" fit="fill" />
            <!--帖子信息-->
            <div class="line"></div>
            <div style="padding: 0px">
              <!--标题-->
              <h1 style="
                  background-color: #e6e6e6;
                  font-size: 20px;
                  width: 100%;
                  white-space: nowrap;
                  overflow: hidden;
                  text-overflow: ellipsis;
                ">
                {{ forum.title }}
              </h1>
              <div class="short-line"></div>
              <!--底部帖子发布时间-->
              <div class="bottom" style="font-size: 16px; color: #afa9a9">
                <P>作者：{{ forum.authorName }}</P>
                <time class="time">{{ forum.createTime }}</time>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-scrollbar>
  </div>

  <el-dialog v-model="detailDialog" :title="detailForm.title" width="80%" style="height: 600px" append-to-body>
    <!--帖子详情-->
    <div style="width: 48%; float: left; height: 500px">
      <!--帖子封面-->
      <el-image style="height: 250px" :src="imageBaseURL + detailForm.coverImage" fit="fill"
        :preview-src-list="[imageBaseURL + detailForm.coverImage]" />
      <div class="line"></div>
      <!--帖子内容-->
      <div style="
          text-indent: 2em;
          word-break: break-all;
          max-height: 200px;
          height: 200px;
          overflow-y: auto;
        ">
        {{ detailForm.content }}
      </div>
    </div>
    <!--评论列表-->
    <div style="width: 45%; float: right; min-height: 600px; padding: 0 1%">
      <h2 style="margin: auto">评论区</h2>
      <div class="line"></div>
      <div style="height: 450px; overflow-y: auto" id="comment-list">
        <ul style="list-style: none">
          <li style="width: 100%" v-for="item in commentList">
            <div class="line"></div>
            <p style="font-size: 16px; color: #afa9a9">
              {{ item.authorName }} {{ item.createTime }}
            </p>
            <p style="font-size: 22px; text-indent: 1em">{{ item.content }}</p>
            <div class="line"></div>
          </li>
        </ul>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { getCurrentInstance, reactive, ref, onMounted } from "vue";
import { imageBaseURL } from "../global/index.js";
import { useRouter } from "vue-router";

const { proxy } = getCurrentInstance();
//帖子列表
let forumList = ref([]);
//初始化帖子列表
const forumInit = async () => {
  console.log("Home页面初始化，当前路径:", proxy.router.currentRoute.value.path);
  let param = {};
  param.pageSize = -1;
  try {
    let result = await proxy.axios.post("forum/list", param);
    if (!result) {
      proxy.msg.error("查询帖子失败");
      return;
    }
    console.log("获取帖子数据成功:", result);
    forumList.value = result;
  } catch (error) {
    console.error("查询帖子失败:", error);
    proxy.msg.error("查询帖子出错: " + error.message);
  }
};

// 使用onActivated钩子，确保每次组件激活时都刷新数据
import { onActivated } from "vue";
onActivated(() => {
  console.log("Home组件被激活");
  forumInit();
});

// 组件挂载后确保数据加载
onMounted(() => {
  console.log("Home组件挂载完成");
  // 确保在DOM更新后执行
  setTimeout(() => {
    forumInit();
  }, 0);
});

//帖子详情对话框开关
let detailDialog = ref(false);
//具体的某个帖子的详情表单
let detailForm = reactive({});
const handleDetail = (forum) => {
  Object.assign(detailForm, forum);
  //加载评论
  commentInit(forum.id);
  //打开对话框
  detailDialog.value = true;
};

//评论列表
let commentList = ref([]);
//初始化评论列表
const commentInit = async (forumId) => {
  let result = await proxy.axios.get("comment/list?forumId=" + forumId);
  if (!result) {
    proxy.msg.erro("查询评论失败");
    return;
  }
  commentList.value = result;
};
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100%;
}

.box-card {
  height: 300px;
  margin-bottom: 20px;
}

.line {
  width: 100%;
  height: 1px;
  background-color: #e6e6e6;
}

.short-line {
  margin: auto;
  width: 50%;
  height: 1px;
  background-color: #e6e6e6;
}
</style>
