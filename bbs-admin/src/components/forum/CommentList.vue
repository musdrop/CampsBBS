<template>
    <el-dialog v-model="visible" title="帖子评论" append-to-body width="800px" destroy-on-close class="comment-list-dialog"
        top="5vh">
        <div class="comment-header" v-if="comments.length > 0">
            <div class="comment-stats">
                <div class="stat-total">共 {{ comments.length }} 条评论</div>
                <div class="stat-sort">
                    <el-radio-group v-model="sortType" size="small">
                        <el-radio-button label="time">按时间排序</el-radio-button>
                        <el-radio-button label="likes">按点赞排序</el-radio-button>
                    </el-radio-group>
                </div>
            </div>
        </div>

        <div class="comment-container">
            <div v-if="comments.length === 0" class="empty-comments">
                <el-empty description="暂无评论" />
            </div>
            <div v-else class="comment-list">
                <div v-for="(comment, index) in sortedComments" :key="comment.id" class="comment-item">
                    <div class="comment-main">
                        <div class="user-info">
                            <el-avatar :size="40" :src="getAvatarUrl(comment.authorAvatar)">
                                {{ comment.authorName?.charAt(0).toUpperCase() }}
                            </el-avatar>
                            <div class="user-details">
                                <div class="username">{{ comment.authorName }}</div>
                                <div class="account">{{ comment.authorAccount }} (ID: {{ comment.userId }})</div>
                            </div>
                        </div>
                        <div class="comment-content">{{ comment.content }}</div>
                        <div class="comment-footer">
                            <div class="comment-time">
                                <el-icon>
                                    <Timer />
                                </el-icon>
                                <span>{{ formatTime(comment.createTime) }}</span>
                            </div>
                            <div class="comment-likes" v-if="comment.likeCount > 0">
                                <el-icon>
                                    <StarFilled />
                                </el-icon>
                                <span>{{ comment.likeCount }} 点赞</span>
                            </div>
                            <div v-if="comment.updateTime !== comment.createTime" class="comment-edit-time">
                                <el-icon>
                                    <Edit />
                                </el-icon>
                                <span>已编辑</span>
                            </div>
                        </div>
                    </div>
                    <div class="comment-actions">
                        <el-popconfirm title="确定要删除这条评论吗?" @confirm="handleDelete(index, comment)"
                            confirm-button-text="确定" cancel-button-text="取消" width="220">
                            <template #reference>
                                <el-button type="danger" size="small" text>
                                    <el-icon>
                                        <Delete />
                                    </el-icon> 删除
                                </el-button>
                            </template>
                        </el-popconfirm>
                    </div>
                </div>
            </div>
        </div>
    </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Delete, Timer, Edit, StarFilled } from '@element-plus/icons-vue';
import { getCurrentInstance } from 'vue';
import { imageBaseURL } from "../../global/index.js";

const { proxy } = getCurrentInstance();
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
const sortType = ref('time');

const props = defineProps({
    modelValue: {
        type: Boolean,
        default: false
    },
    comments: {
        type: Array,
        default: () => []
    }
});

const emits = defineEmits(['update:modelValue', 'delete']);

const visible = computed({
    get: () => props.modelValue,
    set: (value) => emits('update:modelValue', value)
});

const sortedComments = computed(() => {
    if (sortType.value === 'likes') {
        return [...props.comments].sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0));
    } else {
        return [...props.comments].sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
    }
});

const formatTime = (time) => {
    if (!time) return '';

    const date = new Date(time);
    const now = new Date();
    const diff = now - date;

    // 如果是在一小时内
    if (diff < 3600000) {
        const minutes = Math.floor(diff / 60000);
        return minutes <= 0 ? '刚刚' : `${minutes}分钟前`;
    }
    // 如果是在24小时内
    else if (diff < 86400000) {
        const hours = Math.floor(diff / 3600000);
        return `${hours}小时前`;
    }
    // 如果是在7天内
    else if (diff < 604800000) {
        const days = Math.floor(diff / 86400000);
        return `${days}天前`;
    }
    // 否则显示完整日期时间
    else {
        return `${date.toLocaleDateString()} ${date.toLocaleTimeString().substring(0, 5)}`;
    }
};

const getAvatarUrl = (avatar) => {
    if (!avatar || avatar === 'noHead') {
        return defaultAvatar;
    }
    return imageBaseURL + avatar;
};

const handleDelete = async (index, comment) => {
    try {
        let result = await proxy.axios.get('comment/delete?id=' + comment.id);
        if (!result) return;

        proxy.msg.success('评论删除成功');
        emits('delete', comment.id);
    } catch (error) {
        console.error('删除评论失败:', error);
        proxy.msg.error('删除评论失败');
    }
};
</script>

<style scoped>
.comment-list-dialog :deep(.el-dialog__body) {
    padding: 0;
    max-height: 80vh;
    overflow-y: auto;
}

.comment-header {
    padding: 16px 24px;
    border-bottom: 1px solid #f0f0f0;
}

.comment-stats {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.stat-total {
    font-size: 14px;
    color: #606266;
    font-weight: 500;
}

.comment-container {
    padding: 0 16px 16px;
}

.empty-comments {
    padding: 40px 0;
}

.comment-list {
    padding: 8px 0;
}

.comment-item {
    display: flex;
    border-bottom: 1px solid #f0f0f0;
    padding: 20px 8px;
}

.comment-item:last-child {
    border-bottom: none;
}

.comment-main {
    flex: 1;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
}

.user-details {
    display: flex;
    flex-direction: column;
}

.username {
    font-weight: 500;
    font-size: 16px;
    color: #303133;
}

.account {
    font-size: 13px;
    color: #909399;
}

.comment-content {
    padding: 0 0 16px 52px;
    font-size: 15px;
    line-height: 1.6;
    color: #303133;
    white-space: pre-wrap;
}

.comment-footer {
    display: flex;
    align-items: center;
    gap: 16px;
    padding-left: 52px;
}

.comment-time,
.comment-likes,
.comment-edit-time {
    color: #909399;
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 4px;
}

.comment-actions {
    display: flex;
    align-items: flex-start;
    padding-top: 4px;
}

/* 动画效果 */
.comment-item {
    transition: all 0.3s ease;
}

.comment-item:hover {
    background-color: #f9f9f9;
}
</style>
