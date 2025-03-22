<template>
    <div class="post-header">
        <div class="author-info">
            <el-avatar :size="46" :src="authorAvatar || defaultAvatar" class="author-avatar">
                {{ authorName ? authorName.substring(0, 1).toUpperCase() : 'U' }}
            </el-avatar>
            <div class="author-details">
                <div class="author-name">{{ authorName }}</div>
                <div class="post-time">{{ formatTime(createTime) }}</div>
            </div>
        </div>
        <h1 class="post-title">{{ title }}</h1>
    </div>
</template>

<script setup>
defineProps({
    authorName: {
        type: String,
        default: ''
    },
    authorAvatar: {
        type: String,
        default: ''
    },
    createTime: {
        type: String,
        default: ''
    },
    title: {
        type: String,
        default: ''
    },
    defaultAvatar: {
        type: String,
        default: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    }
});

// 格式化时间函数
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
.post-header {
    padding-bottom: 18px;
    border-bottom: 1px solid #ebeef5;
    margin-bottom: 20px;
}

.author-info {
    display: flex;
    align-items: center;
    margin-bottom: 18px;
}

.author-avatar {
    margin-right: 15px;
    border: 2px solid #fff;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.author-details {
    display: flex;
    flex-direction: column;
}

.author-name {
    font-weight: 600;
    font-size: 16px;
    color: #303133;
    margin-bottom: 4px;
}

.post-time {
    font-size: 13px;
    color: #909399;
}

.post-title {
    font-size: 26px;
    font-weight: 600;
    color: #303133;
    line-height: 1.4;
    margin: 0;
    padding: 0;
    word-break: break-word;
}

@media (max-width: 768px) {
    .post-title {
        font-size: 20px;
    }

    .author-avatar {
        width: 40px;
        height: 40px;
    }

    .post-header {
        padding-bottom: 15px;
        margin-bottom: 15px;
    }
}
</style>
