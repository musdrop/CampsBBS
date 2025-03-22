<template>
    <el-dialog v-model="visible" title="帖子详情" append-to-body width="750px" destroy-on-close class="forum-detail-dialog"
        top="5vh">
        <div class="detail-container">
            <div class="post-header">
                <h2 class="post-title">{{ detailData.title }}</h2>
                <div class="post-stats">
                    <div class="stat-item">
                        <el-icon>
                            <View />
                        </el-icon>
                        <span>{{ detailData.viewCount || 0 }} 浏览</span>
                    </div>
                    <div class="stat-item">
                        <el-icon>
                            <StarFilled />
                        </el-icon>
                        <span>{{ detailData.likeCount || 0 }} 点赞</span>
                    </div>
                    <div class="stat-item">
                        <el-icon>
                            <ChatDotRound />
                        </el-icon>
                        <span>{{ detailData.commentCount || 0 }} 评论</span>
                    </div>
                </div>
                <div class="post-meta">
                    <el-avatar :size="40" :src="getAvatarUrl(detailData.authorAvatar)">
                        {{ detailData.authorName?.charAt(0).toUpperCase() }}
                    </el-avatar>
                    <div class="author-info">
                        <div class="author-name">{{ detailData.authorName }}</div>
                        <div class="author-account">{{ detailData.authorAccount }} (ID: {{ detailData.authorId }})</div>
                    </div>
                    <div class="post-time">
                        <el-tooltip effect="dark" :content="'发布: ' + detailData.createTime" placement="top">
                            <el-icon>
                                <Clock />
                            </el-icon>
                        </el-tooltip>
                        <span>{{ formatTime(detailData.createTime) }}</span>
                    </div>
                </div>
            </div>

            <el-divider />

            <div v-if="detailData.coverImage && detailData.coverImage !== 'noHead'" class="cover-image">
                <el-image :src="imageBaseURL + detailData.coverImage" fit="cover"
                    :preview-src-list="[imageBaseURL + detailData.coverImage]">
                    <template #error>
                        <div class="image-error">
                            <el-icon>
                                <Picture />
                            </el-icon>
                            <span>图片加载失败</span>
                        </div>
                    </template>
                </el-image>
            </div>

            <div class="post-content">
                <pre>{{ detailData.content }}</pre>
            </div>

            <el-divider />

            <div class="post-footer">
                <div class="update-time" v-if="detailData.updateTime !== detailData.createTime">
                    <el-icon>
                        <Edit />
                    </el-icon>
                    <span>最后编辑于 {{ formatDetailTime(detailData.updateTime) }}</span>
                </div>
            </div>
        </div>
    </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Clock, Edit, View, StarFilled, ChatDotRound, Picture } from '@element-plus/icons-vue';
import { imageBaseURL } from "../../global/index.js";

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

const props = defineProps({
    modelValue: {
        type: Boolean,
        default: false
    },
    detailData: {
        type: Object,
        default: () => ({})
    }
});

const emits = defineEmits(['update:modelValue']);

const visible = computed({
    get: () => props.modelValue,
    set: (value) => emits('update:modelValue', value)
});

const formatTime = (time) => {
    if (!time) return '';
    const date = new Date(time);
    return date.toLocaleDateString();
};

const formatDetailTime = (time) => {
    if (!time) return '';
    const date = new Date(time);
    return `${date.toLocaleDateString()} ${date.toLocaleTimeString()}`;
};

const getAvatarUrl = (avatar) => {
    if (!avatar || avatar === 'noHead') {
        return defaultAvatar;
    }
    return imageBaseURL + avatar;
};
</script>

<style scoped>
.forum-detail-dialog :deep(.el-dialog__body) {
    padding: 0;
    max-height: 80vh;
    overflow-y: auto;
}

.detail-container {
    padding: 24px;
}

.post-header {
    margin-bottom: 20px;
}

.post-title {
    font-size: 26px;
    font-weight: 600;
    margin-bottom: 16px;
    color: #303133;
    line-height: 1.4;
}

.post-stats {
    display: flex;
    gap: 20px;
    margin-bottom: 16px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #606266;
    font-size: 14px;
}

.post-meta {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px 0;
    background-color: #f8f9fa;
    border-radius: 6px;
    padding: 12px;
}

.author-info {
    display: flex;
    flex-direction: column;
}

.author-name {
    font-weight: 500;
    font-size: 16px;
    color: #303133;
}

.author-account {
    font-size: 14px;
    color: #909399;
}

.post-time {
    margin-left: auto;
    color: #909399;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 4px;
}

.cover-image {
    margin-bottom: 24px;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.cover-image .el-image {
    width: 100%;
    max-height: 400px;
    display: block;
}

.image-error {
    width: 100%;
    height: 200px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: #f5f7fa;
    color: #909399;
    font-size: 14px;
    gap: 10px;
}

.image-error .el-icon {
    font-size: 32px;
}

.post-content {
    font-size: 16px;
    line-height: 1.7;
    color: #303133;
    margin-bottom: 24px;
    padding: 16px 0;
    overflow-wrap: break-word;
}

.post-content pre {
    white-space: pre-wrap;
    font-family: inherit;
}

.post-footer {
    display: flex;
    justify-content: flex-end;
    color: #909399;
    font-size: 13px;
    padding-top: 8px;
}

.update-time {
    display: flex;
    align-items: center;
    gap: 4px;
    background-color: #f8f9fa;
    padding: 6px 12px;
    border-radius: 4px;
}
</style>
