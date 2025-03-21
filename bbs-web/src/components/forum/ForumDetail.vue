<template>
  <el-dialog 
    v-model="visible" 
    :destroy-on-close="true" 
    :show-close="true" 
    :close-on-click-modal="false"
    :close-on-press-escape="true" 
    width="85%" 
    top="7vh" 
    class="forum-detail-dialog" 
    @closed="handleDialogClose"
    :modal-append-to-body="true" 
    :append-to-body="true" 
    fullscreen-on-mobile>
    <div class="forum-detail-container">
      <!-- 左侧帖子详情 -->
      <div class="forum-detail-wrapper">
        <!-- 固定顶部信息区域 -->
        <div class="forum-detail-header">
          <div class="forum-author">
            <el-avatar :size="40" :src="detailForm.authorAvatar || defaultAvatar">
              {{ detailForm.authorName ? detailForm.authorName.substring(0, 1) : 'U' }}
            </el-avatar>
            <div class="author-info">
              <div class="author-name">{{ detailForm.authorName || '未知用户' }}</div>
              <div class="post-time">{{ formatTime(detailForm.createTime) }}</div>
            </div>
          </div>
          <h2 class="detail-title">{{ detailForm.title || '无标题' }}</h2>
        </div>

        <!-- 可滚动的内容区域 -->
        <el-scrollbar class="forum-detail-content">
          <!-- 帖子封面 -->
          <div class="detail-cover" v-if="detailForm.coverImage">
            <el-image 
              :src="imageBaseURL + detailForm.coverImage" 
              fit="cover"
              :preview-src-list="[imageBaseURL + detailForm.coverImage]" 
            />
          </div>

          <!-- 帖子内容 -->
          <div class="detail-content">{{ detailForm.content || '无内容' }}</div>

          <!-- 帖子标签 -->
          <div class="detail-tags" v-if="detailForm.tags && detailForm.tags.length">
            <el-tag v-for="tag in detailForm.tags" :key="tag" size="small" class="forum-tag">
              {{ tag }}
            </el-tag>
          </div>

          <!-- 互动栏 -->
          <div class="detail-interaction">
            <div class="interaction-btn" @click="handleLike">
              <el-icon :class="{ 'liked': detailForm.isLiked }">
                <ThumbUp />
              </el-icon>
              <span>点赞 {{ detailForm.likeCount || 0 }}</span>
            </div>
            <div class="interaction-btn">
              <el-icon>
                <Share />
              </el-icon>
              <span>分享</span>
            </div>
            <div class="interaction-btn">
              <el-icon :class="{ 'collected': detailForm.isCollected }">
                <Star />
              </el-icon>
              <span>收藏</span>
            </div>
          </div>
        </el-scrollbar>
      </div>

      <!-- 右侧评论区 -->
      <div class="forum-comments">
        <h3 class="comments-title">
          <el-icon>
            <ChatDotRound />
          </el-icon>
          评论区 ({{ totalComments }})
        </h3>

        <!-- 评论列表 -->
        <el-scrollbar class="comments-list" ref="commentScrollRef" @scroll="handleCommentScroll">
          <el-empty v-if="commentList.length === 0 && !commentLoading" description="暂无评论，来说点什么吧~" />
          <div v-else class="comment-item" v-for="item in commentList" :key="item.id">
            <div class="comment-author">
              <el-avatar :size="32" class="comment-avatar">
                {{ item.authorName ? item.authorName.substring(0, 1) : 'U' }}
              </el-avatar>
              <div class="comment-info">
                <div class="comment-name">{{ item.authorName }}</div>
                <div class="comment-time">{{ formatTime(item.createTime) }}</div>
              </div>
            </div>
            <div class="comment-content">{{ item.content }}</div>
            <div class="comment-actions">
              <span class="action-btn" @click="handleCommentLike(item)">
                <el-icon :class="{ 'liked': item.isLiked }">
                  <ThumbUp />
                </el-icon>
                <span>{{ item.likeCount || 0 }}</span>
              </span>
              <span class="action-btn" @click="handleReply(item)">
                <el-icon>
                  <ChatLineRound />
                </el-icon>
                回复
              </span>
            </div>
          </div>

          <!-- 评论加载状态 -->
          <div v-if="commentLoading" class="comment-loading">
            <el-skeleton :rows="1" animated />
          </div>

          <div v-if="!hasMoreComments && commentList.length > 0" class="no-more">没有更多评论了</div>
        </el-scrollbar>

        <!-- 评论输入框 -->
        <div class="comment-input">
          <el-input 
            v-model="content" 
            type="textarea" 
            rows="2" 
            placeholder="写下你的评论..." 
            maxlength="200"
            show-word-limit 
            @keyup.ctrl.enter="commentSend" 
            :autosize="{ minRows: 2, maxRows: 4 }" 
          />
          <div class="comment-actions-bar">
            <div class="reply-info" v-if="replyTo">
              回复给：{{ replyTo.authorName }}
              <el-button type="text" @click="cancelReply">
                <el-icon>
                  <Close />
                </el-icon>
              </el-button>
            </div>
            <el-button 
              type="primary" 
              @click="commentSend" 
              :disabled="!content.trim()" 
              :loading="sending"
            >
              发送评论
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { getCurrentInstance, reactive, ref, watch, nextTick } from "vue";
import { imageBaseURL } from "../../global/index.js";
import {
  Top as ThumbUp, Share, Star,
  ChatDotRound, ChatLineRound, Close
} from '@element-plus/icons-vue';
const { proxy } = getCurrentInstance();

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

// 弹窗显示与关闭
const visible = ref(false);
const emit = defineEmits(['update-forum']);

// 详情数据
const props = defineProps({
  forumId: {
    type: [String, Number],
    default: null
  }
});

// 详情表单
let detailForm = reactive({
  id: null,
  title: '',
  content: '',
  authorName: '',
  authorAvatar: '',
  createTime: '',
  coverImage: '',
  likeCount: 0,
  viewCount: 0,
  commentCount: 0,
  tags: [],
  isLiked: false,
  isCollected: false
});

// 监听forumId变化，打开详情弹窗
watch(() => props.forumId, (newVal) => {
  if (newVal) {
    visible.value = true;
    loadForumDetail(newVal);
  } else {
    visible.value = false;
  }
}, { immediate: true });

// 加载帖子详情
const loadForumDetail = async (id) => {
  try {
    // 实际应用中应调用后端API获取详情
    // const result = await proxy.axios.get(`forum/detail/${id}`);
    
    // 模拟加载详情数据
    const result = await proxy.axios.post('forum/list', { id });
    if (!result) return;
    
    let forum;
    
    if (Array.isArray(result)) {
      forum = result.find(item => item.id == id);
    } else if (result.list && Array.isArray(result.list)) {
      forum = result.list.find(item => item.id == id);
    } else {
      forum = result;
    }
    
    if (forum) {
      // 确保清空旧数据
      Object.assign(detailForm, {
        id: null,
        title: '',
        content: '',
        authorName: '',
        authorAvatar: '',
        createTime: '',
        coverImage: '',
        likeCount: 0,
        viewCount: 0,
        commentCount: 0,
        tags: [],
        isLiked: false,
        isCollected: false
      });
      
      // 再赋新值
      Object.assign(detailForm, forum);
      
      // 重置评论相关状态
      commentList.value = [];
      commentPage.value = 1;
      hasMoreComments.value = true;
      replyTo.value = null;
      content.value = "";
      
      // 加载评论
      commentInit(id);
      
      // 增加浏览量
      incrementViewCount(id);
    } else {
      console.error("找不到ID为", id, "的帖子");
      proxy.msg.error("找不到该帖子");
    }
  } catch (error) {
    console.error("加载帖子详情失败", error);
    proxy.msg.error("加载详情失败");
  }
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  const now = new Date();
  const diff = now - date;

  // 小于1分钟
  if (diff < 60 * 1000) {
    return '刚刚';
  }
  // 小于1小时
  if (diff < 60 * 60 * 1000) {
    return Math.floor(diff / (60 * 1000)) + '分钟前';
  }
  // 小于24小时
  if (diff < 24 * 60 * 60 * 1000) {
    return Math.floor(diff / (60 * 60 * 1000)) + '小时前';
  }
  // 小于30天
  if (diff < 30 * 24 * 60 * 60 * 1000) {
    return Math.floor(diff / (24 * 60 * 60 * 1000)) + '天前';
  }

  // 其他情况显示具体日期
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 关闭弹窗处理
const handleDialogClose = () => {
  // 通知父组件清空forumId
  emit('update-forum', null);
  
  // 清空状态
  content.value = "";
  replyTo.value = null;
};

// 处理点赞
const handleLike = async () => {
  try {
    // 模拟点赞API调用
    // const result = await proxy.axios.post('forum/like', { forumId: detailForm.id });
    detailForm.isLiked = !detailForm.isLiked;
    detailForm.likeCount = detailForm.isLiked
      ? (detailForm.likeCount || 0) + 1
      : Math.max((detailForm.likeCount || 1) - 1, 0);

    // 通知父组件更新列表
    emit('update-forum', {
      id: detailForm.id,
      likeCount: detailForm.likeCount
    });
  } catch (error) {
    console.error("点赞失败", error);
    proxy.msg.error("操作失败，请稍后重试");
  }
};

// 增加浏览量
const incrementViewCount = async (forumId) => {
  try {
    // 模拟API调用
    // await proxy.axios.post('forum/view', { forumId });
    
    detailForm.viewCount = (detailForm.viewCount || 0) + 1;
    
    // 通知父组件更新浏览量
    emit('update-forum', {
      id: forumId,
      viewCount: detailForm.viewCount
    });
  } catch (error) {
    console.error("更新浏览量失败", error);
  }
};

// 评论相关
let commentList = ref([]);
let commentPage = ref(1);
const commentPageSize = ref(10);
let hasMoreComments = ref(true);
let commentLoading = ref(false);
const commentScrollRef = ref(null);
let totalComments = ref(0);

// 初始化评论列表
const commentInit = async (forumId) => {
  if (commentLoading.value || !hasMoreComments.value) return;

  commentLoading.value = true;
  let param = {
    forumId,
    pageNum: commentPage.value,
    pageSize: commentPageSize.value
  };

  try {
    let result = await proxy.axios.get('comment/list', { params: param });
    if (!result) {
      proxy.msg.error("加载评论失败");
      return;
    }

    // 假设API返回{list: Array, total: Number}格式的数据
    const newComments = result.list || result;
    totalComments.value = result.total || newComments.length;

    if (commentPage.value === 1) {
      commentList.value = newComments;
    } else {
      commentList.value = [...commentList.value, ...newComments];
    }

    // 判断是否还有更多评论
    hasMoreComments.value = Array.isArray(result)
      ? newComments.length === commentPageSize.value
      : commentList.value.length < (result.total || 0);
  } catch (error) {
    console.error("加载评论失败", error);
  } finally {
    commentLoading.value = false;
  }
};

// 监听评论区滚动
const handleCommentScroll = (e) => {
  const { scrollTop, scrollHeight, clientHeight } = e.target;
  // 滚动到底部时自动加载更多
  if (scrollHeight - scrollTop - clientHeight < 50 && !commentLoading.value && hasMoreComments.value) {
    commentPage.value++;
    commentInit(detailForm.id);
  }
};

// 评论内容
let content = ref("");
let sending = ref(false);
let replyTo = ref(null);

// 发送评论
const commentSend = async () => {
  if (!content.value.trim()) {
    proxy.msg.warning("评论内容不能为空");
    return;
  }

  sending.value = true;
  let param = {
    forumId: detailForm.id,
    content: content.value,
    parentId: replyTo.value ? replyTo.value.id : null
  };

  try {
    let result = await proxy.axios.post('comment/save', param);
    if (!result) {
      return;
    }

    content.value = "";
    replyTo.value = null;

    // 重新加载第一页评论
    commentPage.value = 1;
    await commentInit(detailForm.id);

    // 更新帖子评论数
    detailForm.commentCount = (detailForm.commentCount || 0) + 1;
    
    // 通知父组件更新评论数
    emit('update-forum', {
      id: detailForm.id,
      commentCount: detailForm.commentCount
    });

    // 滚动到评论列表顶部
    await nextTick();
    if (commentScrollRef.value) {
      commentScrollRef.value.setScrollTop(0);
    }
  } catch (error) {
    console.error("发送评论失败", error);
    proxy.msg.error("发送评论失败，请稍后重试");
  } finally {
    sending.value = false;
  }
};

// 处理评论点赞
const handleCommentLike = (comment) => {
  // 模拟点赞逻辑
  comment.isLiked = !comment.isLiked;
  comment.likeCount = comment.isLiked
    ? (comment.likeCount || 0) + 1
    : Math.max((comment.likeCount || 1) - 1, 0);
};

// 处理回复
const handleReply = (comment) => {
  replyTo.value = comment;
  // 聚焦输入框
  nextTick(() => {
    const textarea = document.querySelector('.comment-input .el-textarea__inner');
    if (textarea) {
      textarea.focus();
    }
  });
};

// 取消回复
const cancelReply = () => {
  replyTo.value = null;
};
</script>

<style scoped>
/* 详情对话框样式 */
:deep(.forum-detail-dialog) {
  display: flex;
  align-items: center;
}

:deep(.forum-detail-dialog .el-dialog) {
  margin: 0 auto;
  max-width: 1400px;
  height: 86vh;
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  overflow: hidden;
  margin-top: 7vh !important;
  margin-bottom: 7vh !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.forum-detail-dialog .el-dialog__body) {
  padding: 0;
  flex: 1;
  overflow: hidden;
}

:deep(.forum-detail-dialog .el-dialog__header) {
  padding: 16px 24px;
  margin: 0;
  border-bottom: 1px solid #ebeef5;
}

:deep(.forum-detail-dialog .el-dialog__headerbtn) {
  font-size: 20px;
}

:deep(.forum-detail-dialog .el-overlay) {
  overflow: hidden;
}

.forum-detail-container {
  display: flex;
  height: 100%;
}

.forum-detail-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-right: 1px solid #ebeef5;
}

.forum-detail-header {
  padding: 20px 24px;
  background-color: #fff;
  z-index: 2;
  border-bottom: 1px solid #ebeef5;
}

.forum-author {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.forum-author .author-info {
  margin-left: 12px;
  display: flex;
  flex-direction: column;
}

.forum-author .author-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.forum-author .post-time {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.detail-title {
    font-size: 22px;
    font-weight: 600;
    color: #303133;
    line-height: 1.4;
    margin: 0;
    word-break: break-word;
}

.forum-detail-content {
    flex: 1;
    overflow: hidden;
    padding: 0;
}

.forum-detail-content :deep(.el-scrollbar__view) {
    padding: 24px;
}

.forum-detail-content :deep(.el-scrollbar__bar) {
    z-index: 2;
    opacity: 0.1;
    transition: opacity 0.3s;
}

.forum-detail-content:hover :deep(.el-scrollbar__bar) {
    opacity: 0.3;
}

.detail-cover {
    margin-bottom: 24px;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.detail-cover .el-image {
    width: 100%;
    max-height: 450px;
    object-fit: cover;
    transition: transform 0.5s ease;
}

.detail-cover:hover .el-image {
    transform: scale(1.02);
}

.detail-content {
    font-size: 16px;
    line-height: 1.8;
    color: #303133;
    white-space: pre-wrap;
    word-break: break-word;
    margin-bottom: 32px;
}

.detail-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 24px;
}

.detail-interaction {
    display: flex;
    gap: 24px;
    padding-top: 16px;
    border-top: 1px solid #ebeef5;
}

.interaction-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    border-radius: 20px;
    background-color: #f5f7fa;
    cursor: pointer;
    transition: all 0.3s;
}

.interaction-btn:hover {
    background-color: #ecf5ff;
    color: #409EFF;
}

.liked,
.collected {
    color: #409EFF;
}

.forum-comments {
    width: 40%;
    display: flex;
    flex-direction: column;
    background-color: #fafafa;
}

.comments-title {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px 24px;
    margin: 0;
    border-bottom: 1px solid #ebeef5;
    font-size: 16px;
    font-weight: 600;
    background-color: #fff;
}

.comments-list {
    flex: 1;
    overflow: hidden;
}

.comments-list :deep(.el-scrollbar__view) {
    padding: 16px 24px;
}

.comments-list :deep(.el-scrollbar__bar) {
    z-index: 2;
    opacity: 0.1;
    transition: opacity 0.3s;
}

.comments-list:hover :deep(.el-scrollbar__bar) {
    opacity: 0.3;
}

.comment-item {
    padding: 16px;
    border-bottom: 1px solid #ebeef5;
    background-color: #fff;
    border-radius: 8px;
    margin-bottom: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
}

.comment-item:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
}

.comment-item:last-child {
    margin-bottom: 16px;
}

.comment-author {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
}

.comment-avatar {
    margin-right: 12px;
}

.comment-info {
    display: flex;
    flex-direction: column;
}

.comment-name {
    font-weight: 500;
    color: #303133;
}

.comment-time {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
}

.comment-content {
    font-size: 14px;
    line-height: 1.6;
    color: #303133;
    margin-bottom: 12px;
    white-space: pre-wrap;
    word-break: break-word;
}

.comment-actions {
    display: flex;
    gap: 16px;
}

.action-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: #909399;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 4px;
    transition: all 0.3s;
}

.action-btn:hover {
    background-color: #f5f7fa;
    color: #409EFF;
}

.action-btn .liked {
    color: #409EFF;
}

.comment-loading {
    padding: 16px 0;
    text-align: center;
}

.comment-input {
    padding: 16px 24px;
    border-top: 1px solid #ebeef5;
    display: flex;
    flex-direction: column;
    gap: 12px;
    background-color: #fff;
    position: relative;
}

.comment-input :deep(.el-textarea__inner) {
    resize: none;
    font-size: 14px;
    padding: 12px;
    border-radius: 8px;
    box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
    transition: all 0.3s;
}

.comment-input :deep(.el-textarea__inner:focus) {
    box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1), 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.comment-actions-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.reply-info {
    font-size: 13px;
    color: #909399;
    display: flex;
    align-items: center;
    gap: 8px;
}

.no-more {
    text-align: center;
    padding: 16px;
    color: #909399;
    font-size: 13px;
}

@media (max-width: 768px) {
    .forum-detail-container {
        flex-direction: column;
    }

    .forum-comments {
        width: 100%;
        height: 50%;
    }

    .forum-detail-wrapper {
        height: 50%;
        border-right: none;
        border-bottom: 1px solid #ebeef5;
    }
}
</style>
