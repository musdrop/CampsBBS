<template>
  <div class="forum-container">
    <div class="page-header">
      <h2 class="page-title">帖子管理</h2>
      <div class="page-description">管理社区中的所有帖子内容</div>
    </div>

    <el-card class="query-card" shadow="hover">
      <div class="query-form">
        <el-form :model="queryForm" label-width="60px" :inline="true">
          <el-form-item label="标题">
            <el-input v-model="queryForm.title" clearable style="width:320px" placeholder="请输入帖子标题关键词"
              prefix-icon="Search" @keyup.enter="init()" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="init()" :icon="Search">查询</el-button>
            <el-button @click="resetQuery" :icon="Refresh">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="table-card" shadow="hover">
      <div class="table-header">
        <div class="table-title">帖子列表</div>
        <div class="table-info">共 {{ totalData }} 条数据</div>
      </div>

      <el-table :data="tableData" style="width: 100%;" stripe border highlight-current-row size="large"
        v-loading="loading" :header-cell-style="{ 'background-color': '#f5f7fa', 'color': '#606266' }">
        <!-- 帖子基本信息 -->
        <el-table-column align="center" label="帖子信息" min-width="300">
          <template #default="{ row }">
            <div class="post-info">
              <div class="post-cover" v-if="row.coverImage && row.coverImage !== 'noHead'">
                <el-image :src="imageBaseURL + row.coverImage" fit="cover" :preview-src-list="[]"
                  style="width: 70px; height: 70px; border-radius: 4px;">
                  <template #error>
                    <div class="image-placeholder">
                      <el-icon>
                        <Picture />
                      </el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              <div class="post-text">
                <div class="post-title" @click="handleDetail(null, row)">{{ row.title }}</div>
                <div class="post-brief">
                  <span>
                    <el-icon>
                      <View />
                    </el-icon> {{ row.viewCount || 0 }}
                  </span>
                  <span>
                    <el-icon>
                      <StarFilled />
                    </el-icon> {{ row.likeCount || 0 }}
                  </span>
                  <span>
                    <el-icon>
                      <ChatDotRound />
                    </el-icon> {{ row.commentCount || 0 }}
                  </span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 作者信息 -->
        <el-table-column align="center" label="作者" width="180">
          <template #default="{ row }">
            <div class="author-cell">
              <div class="author-name">{{ row.authorName }}</div>
              <div class="author-id">ID: {{ row.authorId }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 日期时间信息 -->
        <el-table-column align="center" label="发布/修改时间" width="200">
          <template #default="{ row }">
            <div class="time-cell">
              <div class="create-time">
                <el-icon>
                  <Timer />
                </el-icon>
                <el-tooltip :content="row.createTime" placement="top">
                  <span>{{ formatTime(row.createTime) }}</span>
                </el-tooltip>
              </div>
              <div class="update-time" v-if="row.updateTime !== row.createTime">
                <el-icon>
                  <Edit />
                </el-icon>
                <el-tooltip :content="row.updateTime" placement="top">
                  <span>{{ formatTime(row.updateTime) }}</span>
                </el-tooltip>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column align="center" label="操作" width="260" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="handleDetail(scope.$index, scope.row)" :icon="View">
              详情
            </el-button>
            <el-button size="small" type="success" plain @click="handleComment(scope.$index, scope.row)"
              :icon="ChatDotRound" :badge="scope.row.commentCount || '0'">
              评论
            </el-button>
            <el-popconfirm title="确定要删除这篇帖子吗?" confirm-button-text="确定" cancel-button-text="取消"
              @confirm="handleDelete(scope.$index, scope.row)">
              <template #reference>
                <el-button size="small" type="danger" plain :icon="Delete">
                  删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination background layout="total, sizes, prev, pager, next, jumper" class="pagination"
        :page-sizes="[10, 20, 50, 100]" @size-change="handleSizeChange" @current-change="handleCurrentChange"
        v-model:page-size="queryForm.pageSize" v-model:current-page="queryForm.pageNum" :total="totalData" />
    </el-card>

    <!-- 使用新的组件 -->
    <forum-detail v-model="detailDialog" :detail-data="detailForm" />
    <comment-list v-model="commentDialog" :comments="commentData" @delete="handleCommentDelete" />
  </div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref, onMounted, onActivated } from "vue"
import { imageBaseURL } from "../global/index.js"
import {
  Search,
  Refresh,
  View,
  ChatDotRound,
  Delete,
  Edit,
  Timer,
  Picture,
  StarFilled
} from '@element-plus/icons-vue'
import ForumDetail from "../components/forum/ForumDetail.vue"
import CommentList from "../components/forum/CommentList.vue"

const { proxy } = getCurrentInstance()

const queryForm = reactive({
  title: '',
  pageNum: 1,
  pageSize: 10
})
const totalData = ref(0)
const loading = ref(false)

//详情
const detailDialog = ref(false)
const detailForm = reactive({})

let tableData = ref([]);

const init = async () => {
  console.log("Forum页面初始化，当前路径:", proxy.router.currentRoute.value.path);
  loading.value = true;

  let params = Object.assign({}, queryForm);
  try {
    let result = await proxy.axios.post('forum/list', params)
    if (!result) {
      return;
    }
    console.log("获取帖子管理数据成功:", result);
    tableData.value = result.list;
    totalData.value = result.total;
  } catch (error) {
    console.error("查询帖子失败:", error);
    proxy.msg.error("查询帖子失败");
  } finally {
    loading.value = false;
  }
}

// 使用onActivated钩子，确保每次组件激活时都刷新数据
onActivated(() => {
  console.log("Forum组件被激活");
  init();
});

// 组件挂载后确保数据加载
onMounted(() => {
  console.log("Forum组件挂载完成");
  // 确保在DOM更新后执行
  setTimeout(() => {
    init();
  }, 0);
});

const resetQuery = () => {
  queryForm.title = '';
  queryForm.pageNum = 1;
  init();
}

const handleSizeChange = (size) => {
  queryForm.pageSize = size;
  init();
}

const handleCurrentChange = (page) => {
  queryForm.pageNum = page;
  init();
}

const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  // 获取当前日期
  const now = new Date();
  // 计算时间差（毫秒）
  const diff = now - date;

  // 如果是今天发布的
  if (diff < 86400000 && date.getDate() === now.getDate()) {
    return `今天 ${date.toLocaleTimeString().substring(0, 5)}`;
  }
  // 如果是昨天发布的
  else if (diff < 172800000 && date.getDate() === now.getDate() - 1) {
    return `昨天 ${date.toLocaleTimeString().substring(0, 5)}`;
  }
  // 如果是最近7天发布的
  else if (diff < 604800000) {
    const days = ['日', '一', '二', '三', '四', '五', '六'];
    return `周${days[date.getDay()]} ${date.toLocaleTimeString().substring(0, 5)}`;
  }
  // 否则返回完整日期
  else {
    return `${date.toLocaleDateString()} ${date.toLocaleTimeString().substring(0, 5)}`;
  }
};

const handleDetail = async (idx, rowData) => {
  try {
    let result = await proxy.axios.get('user/detail?id=' + rowData.authorId)
    Object.assign(detailForm, rowData)
    detailForm.authorName = result.name
    detailForm.authorAccount = result.account
    detailDialog.value = true
  } catch (error) {
    console.error("获取详情失败:", error);
    proxy.msg.error("获取帖子详情失败");
  }
}

const handleDelete = async (idx, rowData) => {
  try {
    loading.value = true;
    let result = await proxy.axios.get('forum/delete?id=' + rowData.id)
    if (!result) {
      return
    }
    proxy.msg.success('帖子删除成功')
    init()
  } catch (error) {
    console.error("删除帖子失败:", error);
    proxy.msg.error("删除帖子失败");
  } finally {
    loading.value = false;
  }
}

let commentData = ref([])
let commentDialog = ref(false)
const handleComment = async (idx, rowData) => {
  try {
    let forumId = rowData.id
    let result = await proxy.axios.get('comment/list?forumId=' + forumId)
    console.log(result);
    commentData.value = result;
    commentDialog.value = true
  } catch (error) {
    console.error("获取评论失败:", error);
    proxy.msg.error("获取评论列表失败");
  }
}

const handleCommentDelete = (commentId) => {
  commentData.value = commentData.value.filter(item => item.id != commentId);
}
</script>

<style scoped>
.forum-container {
  width: 100%;
  height: 100%;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.page-description {
  font-size: 14px;
  color: #909399;
}

.query-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.table-info {
  font-size: 14px;
  color: #909399;
}

.title-cell {
  display: flex;
  align-items: center;
}

.title-text {
  margin-left: 8px;
  font-weight: 500;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式样式 */
@media screen and (max-width: 1200px) {
  .forum-container {
    padding: 15px;
  }
}

@media screen and (max-width: 768px) {
  .forum-container {
    padding: 10px;
  }
}

.post-info {
  display: flex;
  align-items: center;
  padding: 8px;
  text-align: left;
}

.post-cover {
  margin-right: 16px;
  flex-shrink: 0;
}

.image-placeholder {
  width: 70px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 4px;
  color: #909399;
  font-size: 24px;
}

.post-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.post-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  cursor: pointer;
  transition: color 0.3s;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.post-title:hover {
  color: #409eff;
}

.post-brief {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #909399;
  font-size: 13px;
}

.post-brief span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.author-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-weight: 500;
  color: #303133;
}

.author-id {
  font-size: 13px;
  color: #909399;
}

.time-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.create-time,
.update-time {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.create-time {
  color: #67c23a;
}

.update-time {
  color: #e6a23c;
}
</style>