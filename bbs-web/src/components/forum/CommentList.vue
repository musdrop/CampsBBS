<template>
    <div class="comment-section">
        <!-- 评论输入框 -->
        <div class="comment-input-container">
            <div class="reply-info" v-if="replyTo">
                回复 <span class="reply-name">@{{ replyTo.authorName }}</span>
                <el-button type="text" size="small" @click="cancelReply" class="cancel-reply">
                    <el-icon>
                        <Close />
                    </el-icon>
                </el-button>
            </div>
            <el-input v-model="commentContent" type="textarea" :rows="2" resize="none" placeholder="写下你的评论..."
                class="comment-textarea" />
            <div class="comment-action">
                <el-button type="primary" :loading="sending" @click="sendComment" class="send-button" round>
                    发布评论
                </el-button>
            </div>
        </div>

        <!-- 评论列表 -->
        <div class="comment-list-container">
            <template v-if="commentList.length > 0">
                <div v-for="comment in commentList" :key="comment.id" class="comment-item">
                    <!-- 评论者头像 -->
                    <el-avatar :size="40" :src="comment.authorAvatar" class="comment-avatar">
                        {{ comment.authorName ? comment.authorName.substring(0, 1).toUpperCase() : 'U' }}
                    </el-avatar>

                    <div class="comment-content-wrapper">
                        <!-- 评论者信息 -->
                        <div class="comment-author">
                            <span class="author-name">{{ comment.authorName }}</span>
                            <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                        </div>

                        <!-- 评论内容 -->
                        <div class="comment-content">
                            <div v-if="comment.parentAuthorName" class="parent-info">
                                回复 <span class="parent-name">@{{ comment.parentAuthorName }}</span>:
                            </div>
                            {{ comment.content }}
                        </div>

                        <!-- 评论操作栏 -->
                        <div class="comment-actions">
                            <div class="action-item reply-btn" @click="replyComment(comment)">
                                <el-icon>
                                    <ChatLineRound />
                                </el-icon> 回复
                            </div>
                            <div class="action-item like-btn" :class="{ 'liked': comment.isLiked }"
                                @click="likeComment(comment)">
                                <el-icon v-if="comment.isLiked">
                                    <StarFilled />
                                </el-icon>
                                <el-icon v-else>
                                    <Star />
                                </el-icon>
                                <span>{{ comment.likeCount || 0 }}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 加载更多 -->
                <div class="load-more-container" v-if="hasMore">
                    <el-button type="primary" plain :loading="loading" @click="loadMore" round size="small">
                        {{ loading ? '加载中...' : '加载更多评论' }}
                    </el-button>
                </div>
                <div class="no-more" v-else-if="commentList.length > 0">
                    已经到底啦 ~ 没有更多评论了
                </div>
            </template>

            <!-- 无评论时显示 -->
            <el-empty v-else-if="!loading" description="暂无评论，快来发表第一条评论吧！" />

            <!-- 加载中 -->
            <div v-if="loading && commentList.length === 0" class="loading-container">
                <el-skeleton :rows="3" animated />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { ChatLineRound, Star, StarFilled, Close } from '@element-plus/icons-vue';

const props = defineProps({
    commentList: {
        type: Array,
        default: () => []
    },
    loading: {
        type: Boolean,
        default: false
    },
    hasMore: {
        type: Boolean,
        default: false
    },
    totalComments: {
        type: Number,
        default: 0
    },
    replyTo: {
        type: Object,
        default: null
    },
    sending: {
        type: Boolean,
        default: false
    }
});

const emit = defineEmits(['load-more', 'like-comment', 'reply-comment', 'send-comment', 'cancel-reply']);

const commentContent = ref('');

// 加载更多评论
const loadMore = () => {
    emit('load-more');
};

// 点赞评论
const likeComment = (comment) => {
    emit('like-comment', comment);
};

// 回复评论
const replyComment = (comment) => {
    emit('reply-comment', comment);
};

// 取消回复
const cancelReply = () => {
    emit('cancel-reply');
};

// 发送评论
const sendComment = () => {
    if (!commentContent.value.trim()) return;
    emit('send-comment', commentContent.value);
    commentContent.value = '';
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
</script>

<style scoped>
.comment-section {
    width: 100%;
    display: flex;
    flex-direction: column;
}

/* 评论输入区域 */
.comment-input-container {
    background-color: #f9fafc;
    border-radius: 12px;
    padding: 18px;
    margin-bottom: 24px;
    position: relative;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    border: 1px solid #ebeef5;
}

.reply-info {
    margin-bottom: 10px;
    font-size: 14px;
    color: #606266;
    background-color: #ecf5ff;
    border-radius: 6px;
    padding: 8px 12px;
    display: flex;
    align-items: center;
}

.reply-name {
    color: #409EFF;
    font-weight: 500;
    margin: 0 4px;
}

.cancel-reply {
    margin-left: auto;
    padding: 2px;
}

.comment-textarea {
    margin-bottom: 12px;
    border-radius: 8px;
}

.comment-textarea :deep(.el-textarea__inner) {
    border-radius: 8px;
    padding: 12px;
    transition: all 0.3s;
}

.comment-textarea :deep(.el-textarea__inner:focus) {
    border-color: #409EFF;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.comment-action {
    display: flex;
    justify-content: flex-end;
    margin-top: 12px;
}

.send-button {
    padding: 8px 20px;
}

/* 评论列表 */
.comment-list-container {
    width: 100%;
}

.comment-item {
    display: flex;
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #ebeef5;
    transition: background-color 0.3s;
    padding: 10px;
    border-radius: 8px;
}

.comment-item:hover {
    background-color: #f9fafc;
}

.comment-avatar {
    margin-right: 12px;
    flex-shrink: 0;
    border: 2px solid #fff;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.comment-content-wrapper {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
}

.comment-author {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
}

.author-name {
    font-weight: 500;
    color: #303133;
    margin-right: 8px;
}

.comment-time {
    font-size: 12px;
    color: #909399;
}

.parent-info {
    font-size: 14px;
    color: #606266;
    margin-bottom: 4px;
    padding: 4px 8px;
    background-color: #f5f7fa;
    border-radius: 4px;
    display: inline-block;
}

.parent-name {
    color: #409EFF;
    font-weight: 500;
}

.comment-content {
    word-break: break-word;
    line-height: 1.6;
    font-size: 15px;
    color: #303133;
    margin-bottom: 10px;
    padding: 0 2px;
}

.comment-actions {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-top: 8px;
}

.action-item {
    display: flex;
    align-items: center;
    color: #909399;
    font-size: 13px;
    cursor: pointer;
    gap: 4px;
    transition: all 0.3s;
    padding: 4px 10px;
    border-radius: 16px;
}

.action-item:hover {
    background-color: #f5f7fa;
}

.reply-btn:hover {
    color: #409EFF;
}

.like-btn:hover,
.like-btn.liked {
    color: #f56c6c;
}

.load-more-container {
    text-align: center;
    margin: 25px 0 15px;
}

.no-more {
    text-align: center;
    color: #909399;
    font-size: 14px;
    margin: 20px 0;
    padding: 10px;
    background-color: #f9fafc;
    border-radius: 20px;
}

.loading-container {
    padding: 20px 0;
}

@media (max-width: 768px) {
    .comment-item {
        padding-bottom: 16px;
        margin-bottom: 16px;
    }

    .comment-avatar {
        width: 36px;
        height: 36px;
    }

    .comment-content {
        font-size: 14px;
    }

    .action-item {
        font-size: 12px;
    }

    .comment-input-container {
        padding: 15px;
    }
}
</style>
