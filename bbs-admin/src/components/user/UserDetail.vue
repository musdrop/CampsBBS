<template>
    <el-dialog v-model="visible" title="用户详情" append-to-body width="650px" destroy-on-close class="user-detail-dialog"
        top="5vh">
        <div class="detail-container">
            <div class="user-header">
                <div class="avatar-container">
                    <el-avatar :size="100" :src="getAvatarUrl(detailData.head)" class="user-avatar">
                        {{ detailData.name?.charAt(0).toUpperCase() }}
                    </el-avatar>
                    <div class="user-role"
                        :class="{ 'admin-role': detailData.auth === 1, 'normal-role': detailData.auth === 0 }">
                        {{ detailData.auth === 1 ? '管理员' : '普通用户' }}
                    </div>
                </div>
                <div class="user-info">
                    <h2 class="user-name">{{ detailData.name }}</h2>
                    <div class="user-id">用户ID: {{ detailData.id }}</div>
                    <div class="user-meta">
                        <div class="user-account">
                            <el-icon>
                                <User />
                            </el-icon>
                            <span>{{ detailData.account }}</span>
                        </div>
                        <div class="user-email">
                            <el-icon>
                                <Message />
                            </el-icon>
                            <span>{{ detailData.mail }}</span>
                        </div>
                    </div>
                </div>
            </div>

            <el-divider />

            <div class="user-details">
                <div class="detail-item">
                    <div class="detail-label">用户名</div>
                    <div class="detail-value">{{ detailData.name }}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">账号</div>
                    <div class="detail-value">{{ detailData.account }}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">邮箱</div>
                    <div class="detail-value">{{ detailData.mail }}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">权限</div>
                    <div class="detail-value">
                        <el-tag :type="detailData.auth === 1 ? 'danger' : 'info'">
                            {{ detailData.auth === 1 ? '管理员' : '普通用户' }}
                        </el-tag>
                    </div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">注册时间</div>
                    <div class="detail-value">{{ formatDetailTime(detailData.createTime) }}</div>
                </div>
                <div class="detail-item"
                    v-if="detailData.updateTime && detailData.updateTime !== detailData.createTime">
                    <div class="detail-label">最后更新</div>
                    <div class="detail-value">{{ formatDetailTime(detailData.updateTime) }}</div>
                </div>
            </div>
        </div>
    </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue';
import { User, Message, Edit, Key } from '@element-plus/icons-vue';
import { getCurrentInstance } from 'vue';
import { imageBaseURL } from "../../global/index.js";

const { proxy } = getCurrentInstance();
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

const emits = defineEmits(['update:modelValue', 'edit', 'resetPassword']);

const visible = computed({
    get: () => props.modelValue,
    set: (value) => emits('update:modelValue', value)
});

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
.user-detail-dialog :deep(.el-dialog__body) {
    padding: 0;
    max-height: 80vh;
    overflow-y: auto;
}

.detail-container {
    padding: 24px;
}

.user-header {
    display: flex;
    gap: 30px;
    margin-bottom: 24px;
}

.avatar-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
}

.user-avatar {
    border: 3px solid #f0f2f5;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.user-role {
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
}

.admin-role {
    background-color: #f56c6c;
    color: white;
}

.normal-role {
    background-color: #909399;
    color: white;
}

.user-info {
    flex: 1;
}

.user-name {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;
}

.user-id {
    font-size: 14px;
    color: #909399;
    margin-bottom: 16px;
}

.user-meta {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.user-account,
.user-email {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    color: #606266;
}

.user-details {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px 30px;
    margin-bottom: 30px;
}

.detail-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.detail-label {
    font-size: 14px;
    color: #909399;
    font-weight: 500;
}

.detail-value {
    font-size: 15px;
    color: #303133;
}

.detail-footer {
    display: flex;
    justify-content: flex-end;
    gap: 16px;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #f0f2f5;
}
</style>
