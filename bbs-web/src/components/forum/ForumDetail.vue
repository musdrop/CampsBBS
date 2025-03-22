<template>
    <el-dialog v-model="visible" :destroy-on-close="true" :show-close="false" :close-on-click-modal="true"
        :close-on-press-escape="true" top="5vh" class="forum-detail-dialog" @closed="handleDialogClose"
        :modal-append-to-body="true" :append-to-body="true">
        <div class="forum-detail-container">
            <el-scrollbar class="main-scrollbar">
                <!-- 帖子区域 -->
                <div class="post-section">
                    <!-- 帖子头部信息 -->
                    <post-header :authorName="detailForm.authorName" :authorAvatar="detailForm.authorAvatar"
                        :createTime="detailForm.createTime" :title="detailForm.title" :defaultAvatar="defaultAvatar" />

                    <!-- 帖子内容 -->
                    <post-content :content="detailForm.content" :coverImage="imageBaseURL + detailForm.coverImage"
                        :tags="detailForm.tags" :likeCount="detailForm.likeCount" :isLiked="detailForm.isLiked"
                        @like="handleLike" />

                    <!-- 评论分隔线 -->
                    <div class="comment-divider">
                        <div class="divider-text">评论区 ({{ totalComments }})</div>
                    </div>

                    <!-- 评论区 -->
                    <comment-list :commentList="commentList" :loading="commentLoading" :hasMore="hasMoreComments"
                        :totalComments="totalComments" @load-more="loadMoreComments" @like-comment="handleCommentLike"
                        @reply-comment="handleReply" />
                </div>
            </el-scrollbar>

            <!-- 底部固定评论栏 -->
            <div class="fixed-comment-bar" :class="{ 'reply-mode': replyTo }">
                <div class="reply-info" v-if="replyTo">
                    <span>回复: {{ replyTo.authorName }}</span>
                    <el-button type="text" @click="cancelReply">取消</el-button>
                </div>
                <div class="comment-input-wrapper">
                    <el-input v-model="commentContent" placeholder="写下你的评论..." class="comment-input" :disabled="sending"
                        @keyup.enter="handleCommentSend(commentContent)"></el-input>
                    <el-button type="primary" :loading="sending" @click="handleCommentSend(commentContent)">
                        发送
                    </el-button>
                </div>
            </div>
        </div>
    </el-dialog>
</template>

<script setup>
import { getCurrentInstance, reactive, ref, watch } from "vue";
import PostHeader from './PostHeader.vue';
import PostContent from './PostContent.vue';
import CommentList from './CommentList.vue';
import { imageBaseURL } from "../../global/index.js"; // 导入图片基础路径

const { proxy } = getCurrentInstance();

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

// 弹窗显示状态
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
const detailForm = reactive({
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

// 评论相关
const commentList = ref([]);
const commentPage = ref(1);
const commentPageSize = ref(10);
const hasMoreComments = ref(true);
const commentLoading = ref(false);
const totalComments = ref(0);
const replyTo = ref(null);
const commentContent = ref("");
const sending = ref(false);

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
const loadForumDetail = async (forumId) => {
    try {
        // 模拟加载详情数据
        const result = await proxy.axios.post('forum/list', { forumId: forumId });
        if (!result) return;

        let forum = result.list[0];

        if (forum) {
            // 清空旧数据后赋新值
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

            Object.assign(detailForm, forum);

            // 重置评论相关状态
            commentList.value = [];
            commentPage.value = 1;
            hasMoreComments.value = true;
            replyTo.value = null;
            commentContent.value = "";

            // 加载评论
            loadComments(forumId);

            // 增加浏览量
            incrementViewCount(forumId);
        } else {
            console.error("找不到ID为", forumId, "的帖子");
            proxy.msg.error("找不到该帖子");
        }
    } catch (error) {
        console.error("加载帖子详情失败", error);
        proxy.msg.error("加载详情失败");
    }
};

// 关闭弹窗处理
const handleDialogClose = () => {
    emit('update-forum', null);
    commentContent.value = "";
    replyTo.value = null;
};

// 处理点赞
const handleLike = async () => {
    try {
        const result = await proxy.axios.post('forum/like', null, {
            params: { forumId: detailForm.id }
        });

        if (result) {
            detailForm.isLiked = result.isLiked;
            detailForm.likeCount = result.likeCount;

            emit('update-forum', {
                id: detailForm.id,
                likeCount: detailForm.likeCount,
                isLiked: detailForm.isLiked
            });
        }
    } catch (error) {
        console.error("点赞失败", error);
        proxy.msg.error("操作失败，请稍后重试");
    }
};

// 增加浏览量
const incrementViewCount = async (forumId) => {
    try {
        await proxy.axios.post('forum/view', null, {
            params: { forumId }
        });

        detailForm.viewCount = (detailForm.viewCount || 0) + 1;

        emit('update-forum', {
            id: forumId,
            viewCount: detailForm.viewCount
        });
    } catch (error) {
        console.error("更新浏览量失败", error);
    }
};

// 加载评论
const loadComments = async (forumId) => {
    if (commentLoading.value || !hasMoreComments.value) return;

    commentLoading.value = true;
    let params = {
        forumId,
        pageNum: commentPage.value,
        pageSize: commentPageSize.value
    };

    try {
        let result = await proxy.axios.get('comment/list', { params });
        if (!result) {
            proxy.msg.error("加载评论失败");
            return;
        }

        const newComments = result.list || [];
        totalComments.value = result.total || 0;

        // 处理评论树结构，将所有嵌套回复放到二级结构中
        const processComments = (comments) => {
            // 遍历一级评论
            comments.forEach(comment => {
                // 递归处理所有嵌套回复，扁平化为两级结构
                if (comment.replies && comment.replies.length > 0) {
                    // 暂存原始的回复列表
                    const originalReplies = [...comment.replies];
                    // 清空回复列表，重新构建
                    comment.replies = [];

                    // 记录评论深度的递归函数，提取所有嵌套层级的回复
                    const extractReplies = (replyList, depth = 1, parentInfo = null) => {
                        replyList.forEach(reply => {
                            // 只有二级以上的评论才需要标注回复对象
                            if (depth > 1 && parentInfo) {
                                reply.parentAuthorName = parentInfo.authorName;
                            }

                            // 将回复添加到一级评论的replies中
                            comment.replies.push(reply);

                            // 递归处理此回复的回复
                            if (reply.replies && reply.replies.length > 0) {
                                extractReplies(reply.replies, depth + 1, reply);
                                // 处理完后删除原始嵌套结构，避免重复
                                delete reply.replies;
                            }
                        });
                    };

                    // 开始提取嵌套回复，从深度1开始
                    extractReplies(originalReplies);

                    // 按时间顺序排序所有回复
                    comment.replies.sort((a, b) => new Date(a.createTime) - new Date(b.createTime));
                }
            });

            return comments;
        };

        // 处理评论树结构
        const processedComments = processComments(newComments);

        // 按时间排序一级评论
        const sortedComments = [...processedComments].sort((a, b) =>
            new Date(b.createTime) - new Date(a.createTime)
        );

        if (commentPage.value === 1) {
            commentList.value = sortedComments;
        } else {
            commentList.value = [...commentList.value, ...sortedComments];
        }

        hasMoreComments.value = commentList.value.length < totalComments.value;
    } catch (error) {
        console.error("加载评论失败", error);
    } finally {
        commentLoading.value = false;
    }
};

// 加载更多评论
const loadMoreComments = () => {
    if (commentLoading.value || !hasMoreComments.value) return;
    commentPage.value++;
    loadComments(detailForm.id);
};

// 处理评论点赞
const handleCommentLike = async (comment) => {
    try {
        const result = await proxy.axios.post('comment/like', null, {
            params: { commentId: comment.id }
        });

        if (result) {
            comment.isLiked = result.isLiked;
            comment.likeCount = result.likeCount;
        }
    } catch (error) {
        console.error("评论点赞失败", error);
        proxy.msg.error("操作失败，请稍后重试");
    }
};

// 处理回复
const handleReply = (comment) => {
    if (comment.parentComment) {
        // 如果是回复内的回复，需要关联父评论信息以便前端展示
        replyTo.value = {
            ...comment,
            rootCommentId: comment.parentComment.id
        };
    } else {
        replyTo.value = comment;
    }
};

// 取消回复
const cancelReply = () => {
    replyTo.value = null;
};

// 发送评论
const handleCommentSend = async (content) => {
    if (!content.trim()) {
        proxy.msg.warning("评论内容不能为空");
        return;
    }

    sending.value = true;
    let param = {
        forumId: detailForm.id,
        content: content,
        parentId: replyTo.value ? replyTo.value.id : null
    };

    try {
        let result = await proxy.axios.post('comment/save', param);
        if (!result) return;

        replyTo.value = null;
        commentContent.value = ""; // 清空评论内容

        // 无论是否有更多评论，都重置为第一页并重新加载
        commentPage.value = 1;
        commentList.value = []; // 清空现有评论列表，确保新评论会显示
        hasMoreComments.value = true; // 重置加载更多状态
        await loadComments(detailForm.id);

        // 更新帖子评论数
        detailForm.commentCount = (detailForm.commentCount || 0) + 1;

        // 通知父组件更新评论数
        emit('update-forum', {
            id: detailForm.id,
            commentCount: detailForm.commentCount
        });
    } catch (error) {
        console.error("发送评论失败", error);
        proxy.msg.error("发送评论失败，请稍后重试");
    } finally {
        sending.value = false;
    }
};
</script>

<style scoped>
/* 详情对话框样式 */
:deep(.forum-detail-dialog) {
    display: flex;
    align-items: center;
    justify-content: center;
}

:deep(.forum-detail-dialog .el-dialog) {
    margin: 0 auto !important;
    /* 减小宽度百分比 */
    max-width: 900px;
    /* 减小最大宽度，减少左右空白 */
    height: 88vh;
    min-height: 500px;
    max-height: 90vh;
    display: flex;
    flex-direction: column;
    border-radius: 12px;
    overflow: hidden;
    position: relative;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.forum-detail-dialog .el-dialog__body) {
    padding: 0;
    flex: 1;
    overflow: hidden;
    position: relative;
    height: calc(100% - 54px);
}

:deep(.forum-detail-dialog .el-dialog__header) {
    padding: 16px 24px;
    margin: 0;
    border-bottom: 1px solid #ebeef5;
    height: 54px;
    box-sizing: border-box;
}

:deep(.forum-detail-dialog .el-dialog__headerbtn) {
    font-size: 20px;
}

:deep(.forum-detail-dialog .el-overlay) {
    overflow: hidden;
}

/* 改为上下布局的容器样式 */
.forum-detail-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    overflow: hidden;
}

/* 主滚动区域 */
.main-scrollbar {
    height: 100%;
    width: 100%;
    padding-bottom: 70px;
    /* 为底部评论栏留出空间 */
}

/* 帖子区域 */
.post-section {
    display: flex;
    flex-direction: column;
    padding: 25px 30px 40px;
    /* 调整内边距 */
    max-width: 800px;
    width: 100%;
    margin: 0 auto;
    box-sizing: border-box;
    background-color: #fff;
}

/* 评论区分隔线 */
.comment-divider {
    position: relative;
    height: 40px;
    margin: 30px 0 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
}

.comment-divider::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 1px;
    background-color: #ebeef5;
    z-index: 1;
}

.divider-text {
    position: relative;
    background-color: #fff;
    padding: 0 20px;
    font-size: 16px;
    font-weight: 500;
    color: #606266;
    z-index: 2;
}

/* 底部固定评论栏样式 */
.fixed-comment-bar {
    position: fixed;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    background: #fff;
    padding: 12px 20px;
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
    z-index: 100;
    display: flex;
    flex-direction: column;
    transition: all 0.3s ease;
    width: 90%;
    max-width: 700px;
    /* 与帖子区域的max-width一致 */
    border-top: 1px solid #ebeef5;
    border-radius: 8px 8px 0 0;
}

.reply-mode {
    padding-top: 8px;
}

.reply-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    color: #606266;
    font-size: 14px;
}

.comment-input-wrapper {
    display: flex;
    align-items: center;
    gap: 10px;
}

.comment-input {
    flex: 1;
}

:deep(.comment-input .el-input__inner) {
    border-radius: 20px;
    padding-left: 15px;
}

@media (max-width: 768px) {
    :deep(.forum-detail-dialog .el-dialog) {
        width: 95% !important;
        height: 90vh;
        border-radius: 12px 12px 0 0;
        max-width: none;
    }

    .post-section {
        padding: 15px 20px 30px;
        border-radius: 0;
        box-shadow: none;
    }

    .fixed-comment-bar {
        width: 95%;
        padding: 10px 15px;
        box-sizing: border-box;
    }

    .main-scrollbar {
        padding-bottom: 60px;
    }
}
</style>
