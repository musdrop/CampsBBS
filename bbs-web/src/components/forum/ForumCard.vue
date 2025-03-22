<template>
    <el-card shadow="hover" class="forum-card" @click="handleCardClick">
        <!-- 封面图片 -->
        <div class="image-container">
            <el-image :src="imageBaseURL + forum.coverImage" class="cover-image" fit="cover" />
            <div class="author-avatar" v-if="showAuthor">
                <el-avatar :size="32" :src="forum.authorAvatar || defaultAvatar">
                    {{ forum.authorName ? forum.authorName.substring(0, 1) : 'U' }}
                </el-avatar>
            </div>
        </div>

        <!-- 帖子信息 -->
        <div class="forum-content">
            <!-- 帖子标题 -->
            <h3 class="forum-title">{{ forum.title }}</h3>

            <!-- 帖子摘要，如果显示的话 -->
            <p class="forum-excerpt" v-if="showExcerpt">{{ getExcerpt(forum.content) }}</p>

            <!-- 帖子元数据 -->
            <div class="forum-meta">
                <div class="author-info" v-if="showAuthor">
                    <span class="author-name">{{ forum.authorName }}</span>
                </div>

                <div class="post-time">
                    <el-icon>
                        <Clock />
                    </el-icon>
                    <span>{{ formatTime(forum.createTime) }}</span>
                </div>
            </div>

            <!-- 互动信息 -->
            <div class="interaction-bar">
                <div class="interaction-item">
                    <el-icon>
                        <ChatLineRound />
                    </el-icon>
                    <span>{{ forum.commentCount || 0 }}</span>
                </div>
                <div class="interaction-item">
                    <el-icon>
                        <View />
                    </el-icon>
                    <span>{{ forum.viewCount || 0 }}</span>
                </div>
                <div class="interaction-item">
                    <el-icon>
                        <Top />
                    </el-icon>
                    <span>{{ forum.likeCount || 0 }}</span>
                </div>
            </div>
        </div>

        <!-- 帖子标签 -->
        <div class="forum-tags" v-if="showTags && forum.tags && forum.tags.length">
            <el-tag v-for="tag in forum.tags" :key="tag" size="small" class="forum-tag">
                {{ tag }}
            </el-tag>
        </div>
    </el-card>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
import { Clock, ChatLineRound, View, Top } from '@element-plus/icons-vue';
import { imageBaseURL } from "../../global/index.js";

const props = defineProps({
    forum: {
        type: Object,
        required: true
    },
    showExcerpt: {
        type: Boolean,
        default: true
    },
    showAuthor: {
        type: Boolean,
        default: true
    },
    showTags: {
        type: Boolean,
        default: true
    },
    defaultAvatar: {
        type: String,
        default: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
});

const emit = defineEmits(['card-click']);

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

// 获取内容摘要
const getExcerpt = (content) => {
    if (!content) return '';
    return content.length > 60 ? content.substring(0, 60) + '...' : content;
};

// 处理卡片点击
const handleCardClick = () => {
    emit('card-click', props.forum);
};
</script>

<style scoped>
.forum-card {
    border-radius: 12px;
    overflow: hidden;
    transition: all 0.3s ease;
    height: 100%;
    display: flex;
    flex-direction: column;
}

.forum-card:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    transform: translateY(-5px);
}

.image-container {
    position: relative;
    overflow: hidden;
    border-radius: 8px 8px 0 0;
}

.cover-image {
    width: 100%;
    height: 200px;
    object-fit: cover;
    transition: transform 0.5s ease;
}

.forum-card:hover .cover-image {
    transform: scale(1.05);
}

.author-avatar {
    position: absolute;
    bottom: -16px;
    right: 16px;
    z-index: 2;
    border: 2px solid #fff;
    border-radius: 50%;
    background-color: #fff;
}

.forum-content {
    padding: 16px;
    flex: 1;
    display: flex;
    flex-direction: column;
}

.forum-title {
    font-size: 18px;
    font-weight: 600;
    margin: 8px 0;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
}

.forum-excerpt {
    font-size: 14px;
    color: #606266;
    margin: 8px 0 12px;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    flex: 1;
}

.forum-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 12px;
    font-size: 13px;
    color: #909399;
}

.author-info {
    display: flex;
    align-items: center;
}

.author-name {
    font-weight: 500;
    color: #606266;
}

.post-time {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
}

.interaction-bar {
    display: flex;
    justify-content: space-between;
    margin-top: 16px;
    padding-top: 12px;
    border-top: 1px solid #ebeef5;
}

.interaction-item {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #909399;
    font-size: 13px;
    transition: color 0.3s;
}

.interaction-item:hover {
    color: #409EFF;
}

.forum-tags {
    padding: 0 16px 16px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.forum-tag {
    font-size: 12px;
    padding: 0 8px;
    height: 24px;
    line-height: 22px;
}
</style>
