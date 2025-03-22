<template>
  <div class="profile-container">
    <el-scrollbar height="95vh" class="profile-scrollbar" ref="mainScrollRef">
      <!-- 背景装饰 -->
      <div class="background-decoration"></div>

      <div class="profile-content">
        <!-- 用户信息卡片 -->
        <el-card shadow="hover" class="user-info-card">
          <div class="user-header">
            <el-avatar :size="80" class="user-avatar">
              {{ loginUser.name ? loginUser.name.substring(0, 1).toUpperCase() : 'U' }}
            </el-avatar>
            <div class="user-title">
              <h2>{{ loginUser.name }}</h2>
              <p class="user-subtitle">个人资料</p>
            </div>
          </div>

          <div class="user-details">
            <div class="info-item">
              <el-icon>
                <User />
              </el-icon>
              <div class="info-content">
                <div class="info-label">账号</div>
                <div class="info-value">{{ loginUser.account }}</div>
              </div>
            </div>
            <div class="info-item">
              <el-icon>
                <Message />
              </el-icon>
              <div class="info-content">
                <div class="info-label">邮箱</div>
                <div class="info-value">{{ loginUser.mail }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 我的帖子区域 -->
        <div class="my-posts-section">
          <div class="section-header">
            <h2>我发布的帖子</h2>
            <el-button type="primary" @click="goToPublish" size="small" plain>
              <el-icon>
                <Edit />
              </el-icon>发布新帖
            </el-button>
          </div>

          <el-empty v-if="forumList.length === 0 && !initLoading && !loading" description="你还没有发布过帖子" />

          <!-- 帖子列表 -->
          <div class="forum-masonry" v-if="forumList.length > 0 || initLoading">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="forum in forumList" :key="forum.id" class="masonry-item">
                <forum-card :forum="forum" :show-author="false" @card-click="handleDetail" />
              </el-col>
            </el-row>

            <!-- 加载状态显示 -->
            <div class="load-more" v-if="forumList.length > 0" ref="loadMoreRef">
              <el-skeleton :rows="1" animated v-if="loading" />
              <div v-else-if="!hasMore" class="no-more">已经到底啦 ~</div>
            </div>

            <!-- 初始加载时的骨架屏 -->
            <div v-if="initLoading" class="skeleton-container">
              <el-skeleton :rows="3" animated v-for="i in 4" :key="i" class="skeleton-item" />
            </div>
          </div>
        </div>
      </div>
    </el-scrollbar>

    <!-- 使用同首页相同的帖子详情组件 -->
    <forum-detail :forum-id="selectedForumId" @update-forum="handleForumUpdate" />
  </div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref, onMounted, onBeforeUnmount, watch } from "vue";
import { User, Message, Edit } from '@element-plus/icons-vue';
import ForumCard from '../components/forum/ForumCard.vue';
import ForumDetail from '../components/forum/ForumDetail.vue';

const { proxy } = getCurrentInstance();
let loginUser = proxy.cookies.get("bbs-web");

// 帖子列表
let forumList = ref([]);
// 分页参数
const pageSize = ref(8);
const currentPage = ref(1);
const hasMore = ref(true);
const loading = ref(false);
const initLoading = ref(true);

// 滚动加载相关
const mainScrollRef = ref(null);
const loadMoreRef = ref(null);
let observer = null;

// 选中的帖子ID（用于详情组件）
const selectedForumId = ref(null);

// 初始化帖子列表
const forumInit = async () => {
  loading.value = true;

  let param = {
    userId: loginUser.id,
    pageNum: currentPage.value,
    pageSize: pageSize.value
  };

  try {
    let result = await proxy.axios.post("forum/list", param);
    if (!result) {
      proxy.msg.error("查询帖子失败");
      return;
    }

    // 假设API返回{list: Array, total: Number}格式的数据
    const newForums = result.list || result;

    if (currentPage.value === 1) {
      forumList.value = newForums;
    } else {
      forumList.value = [...forumList.value, ...newForums];
    }

    // 判断是否还有更多数据
    hasMore.value = Array.isArray(result)
      ? newForums.length === pageSize.value
      : forumList.value.length < (result.total || 0);

  } catch (error) {
    console.error("加载帖子失败", error);
    proxy.msg.error("加载帖子失败");
  } finally {
    loading.value = false;
    initLoading.value = false;
  }
};

// 设置交叉观察器来监控加载更多元素
const setupIntersectionObserver = () => {
  // 如果浏览器支持IntersectionObserver
  if ('IntersectionObserver' in window) {
    observer = new IntersectionObserver((entries) => {
      const entry = entries[0];
      if (entry.isIntersecting && !loading.value && hasMore.value && !initLoading.value) {
        loadMore();
      }
    }, {
      root: null, // 默认是视口
      rootMargin: '0px',
      threshold: 0.1 // 当目标元素的10%进入视口时触发
    });

    // 等待DOM更新后再观察元素
    setTimeout(() => {
      if (loadMoreRef.value) {
        observer.observe(loadMoreRef.value);
      }
    }, 100);
  } else {
    // 对于不支持IntersectionObserver的浏览器，使用传统的滚动事件
    if (mainScrollRef.value) {
      mainScrollRef.value.wrap.addEventListener('scroll', handleScrollLegacy);
    }
  }
};

// 传统滚动事件处理（备用方案）
const handleScrollLegacy = () => {
  if (!mainScrollRef.value || loading.value || !hasMore.value || initLoading.value) return;

  const scrollElement = mainScrollRef.value.wrap;
  const { scrollTop, scrollHeight, clientHeight } = scrollElement;

  if (scrollHeight - scrollTop - clientHeight < 100) {
    loadMore();
  }
};

// 加载更多
const loadMore = () => {
  if (loading.value || !hasMore.value) return;
  currentPage.value++;
  forumInit();
};

// 前往发布页面
const goToPublish = () => {
  proxy.router.push('/publish');
};

// 打开帖子详情
const handleDetail = (forum) => {
  selectedForumId.value = forum.id;
};

// 处理详情组件更新帖子数据
const handleForumUpdate = (data) => {
  if (!data) {
    // 关闭详情弹窗时
    selectedForumId.value = null;
    return;
  }

  // 更新列表中对应的帖子数据
  const index = forumList.value.findIndex(item => item.id === data.id);
  if (index !== -1) {
    forumList.value[index] = {
      ...forumList.value[index],
      ...data
    };
  }
};

// 当数据加载状态改变时，可能需要重新设置观察器
watch([loading, hasMore], () => {
  if (!loading.value && observer && loadMoreRef.value) {
    // 确保观察器正在观察最新的加载更多元素
    observer.disconnect();
    observer.observe(loadMoreRef.value);
  }
});

// 页面加载
onMounted(() => {
  forumInit();
  setupIntersectionObserver();
});

// 页面卸载前清理
onBeforeUnmount(() => {
  if (observer) {
    observer.disconnect();
  }

  // 移除传统滚动事件监听（如果有）
  if (mainScrollRef.value && !('IntersectionObserver' in window)) {
    mainScrollRef.value.wrap.removeEventListener('scroll', handleScrollLegacy);
  }
});
</script>

<style lang="scss" scoped>
.profile-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.profile-scrollbar {
  scrollbar-width: thin;

  :deep(.el-scrollbar__bar) {
    opacity: 0.2;
    z-index: 2;

    &:hover {
      opacity: 0.4;
    }
  }
}

.background-decoration {
  position: fixed;
  top: -15%;
  right: -10%;
  width: 70%;
  height: 70%;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(64, 158, 255, 0.05));
  z-index: -1;
  pointer-events: none;

  &::after {
    content: '';
    position: absolute;
    bottom: -60%;
    left: -60%;
    width: 80%;
    height: 80%;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.05), rgba(64, 158, 255, 0.02));
  }
}

.profile-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.user-info-card {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  animation: fadeIn 0.8s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
  }
}

.user-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.user-avatar {
  background: linear-gradient(135deg, #409EFF, #36D1DC);
  color: white;
  font-size: 32px;
  font-weight: 600;
  margin-right: 20px;
}

.user-title {
  h2 {
    font-size: 24px;
    margin: 0 0 5px;
    color: #303133;
  }

  .user-subtitle {
    color: #909399;
    margin: 0;
    font-size: 14px;
  }
}

.user-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  padding: 12px;
  border-radius: 8px;
  background-color: #f8f9fa;
  transition: background-color 0.3s;

  &:hover {
    background-color: #ebeef5;
  }

  .el-icon {
    font-size: 20px;
    color: #409EFF;
    margin-right: 12px;
    margin-top: 4px;
  }
}

.info-content {
  flex: 1;
}

.info-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.info-value {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
  word-break: break-word;
}

.my-posts-section {
  margin-top: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h2 {
    font-size: 22px;
    color: #303133;
    margin: 0;
    position: relative;
    padding-left: 12px;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 20px;
      background: #409EFF;
      border-radius: 2px;
    }
  }
}

.forum-masonry {
  margin-top: 20px;
}

.masonry-item {
  margin-bottom: 24px;
  transition: transform 0.3s ease;
}

.load-more {
  text-align: center;
  margin-top: 16px;
  margin-bottom: 32px;
  color: #909399;
  height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.no-more {
  font-size: 14px;
  color: #909399;
  padding: 16px 0;
  text-align: center;
}

.skeleton-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.skeleton-item {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式适配
@media (max-width: 768px) {
  .profile-content {
    padding: 15px;
  }

  .user-header {
    flex-direction: column;
    text-align: center;
    margin-bottom: 20px;
  }

  .user-avatar {
    margin-right: 0;
    margin-bottom: 15px;
  }

  .user-details {
    grid-template-columns: 1fr;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
