<template>
    <div class="post-content-container">
        <!-- 封面图 (如果有) -->
        <div class="cover-image-container" v-if="coverImage">
            <el-image :src="coverImage" class="cover-image" fit="cover" :preview-src-list="[coverImage]">
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

        <!-- 帖子内容 -->
        <div class="content-body" v-html="content"></div>

        <!-- 标签 -->
        <div class="tags-container" v-if="tags && tags.length > 0">
            <el-tag v-for="tag in tags" :key="tag" size="small" class="tag-item" effect="light">{{ tag }}</el-tag>
        </div>

        <!-- 交互栏 -->
        <div class="interaction-bar">
            <div class="interaction-buttons">
                <div class="like-action" :class="{ 'is-liked': isLiked }" @click="handleLike">
                    <i class="like-icon" :class="isLiked ? 'el-icon-star-filled' : 'el-icon-star'">
                        <el-icon v-if="isLiked">
                            <StarFilled />
                        </el-icon>
                        <el-icon v-else>
                            <Star />
                        </el-icon>
                    </i>
                    <span class="like-text">{{ isLiked ? '已赞' : '点赞' }} ({{ likeCount }})</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { Picture, Star, StarFilled } from '@element-plus/icons-vue';

defineProps({
    content: {
        type: String,
        default: ''
    },
    coverImage: {
        type: String,
        default: ''
    },
    tags: {
        type: Array,
        default: () => []
    },
    likeCount: {
        type: Number,
        default: 0
    },
    isLiked: {
        type: Boolean,
        default: false
    }
});

const emit = defineEmits(['like']);

const handleLike = () => {
    emit('like');
};
</script>

<style scoped>
.post-content-container {
    display: flex;
    flex-direction: column;
    width: 100%;
    margin-top: 20px;
}

.cover-image-container {
    width: 100%;
    margin-bottom: 24px;
    border-radius: 12px;
    overflow: hidden;
    background-color: #f5f7fa;
    max-height: 450px;
    display: flex;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.cover-image {
    width: 100%;
    max-height: 450px;
    object-fit: cover;
    border-radius: 12px;
    transition: transform 0.5s ease;
}

.cover-image:hover {
    transform: scale(1.02);
}

.image-error {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;
    color: #909399;
    font-size: 14px;
}

.image-error .el-icon {
    font-size: 32px;
    margin-bottom: 8px;
}

.content-body {
    line-height: 1.8;
    font-size: 16px;
    color: #303133;
    word-break: break-word;
    overflow-wrap: break-word;
    padding: 10px 0;
    min-height: 100px;
}

.content-body :deep(img) {
    max-width: 100%;
    height: auto;
    border-radius: 8px;
    margin: 15px 0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.content-body :deep(a) {
    color: #409EFF;
    text-decoration: none;
}

.content-body :deep(a:hover) {
    text-decoration: underline;
}

.content-body :deep(pre) {
    background-color: #f6f8fa;
    padding: 16px;
    border-radius: 8px;
    overflow-x: auto;
    font-family: SFMono-Regular, Consolas, "Liberation Mono", Menlo, monospace;
    font-size: 14px;
    line-height: 1.6;
    margin: 16px 0;
}

.tags-container {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 24px;
}

.tag-item {
    border-radius: 16px;
    padding: 0 12px;
    height: 24px;
    line-height: 22px;
    font-size: 12px;
}

.interaction-bar {
    display: flex;
    justify-content: flex-end;
    margin-top: 25px;
    padding-top: 16px;
    border-top: 1px solid #ebeef5;
}

.interaction-buttons {
    display: flex;
    gap: 20px;
}

.like-action {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    border-radius: 20px;
    background-color: #f5f7fa;
    cursor: pointer;
    transition: all 0.3s ease;
}

.like-action:hover {
    background-color: #e8f4ff;
}

.like-action.is-liked {
    background-color: #ecf5ff;
    color: #409EFF;
}

.like-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
}

.like-text {
    font-size: 14px;
    font-weight: 500;
}

@media (max-width: 768px) {
    .cover-image-container {
        max-height: 300px;
    }

    .cover-image {
        max-height: 300px;
    }

    .content-body {
        font-size: 15px;
    }

    .interaction-bar {
        justify-content: center;
    }
}
</style>
