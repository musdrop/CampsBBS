<template>
	<div class="home-container">
		<el-scrollbar height="95vh" class="main-scrollbar">
			<!-- 瀑布流布局展示帖子 -->
			<div class="masonry-container">
				<el-row :gutter="20">
					<el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="forum in forumList" :key="forum.id"
						class="masonry-item">
						<el-card shadow="hover" class="forum-card" @click="handleDetail(forum)">
							<!-- 封面图片 -->
							<div class="image-container">
								<el-image :src="imageBaseURL + forum.coverImage" class="cover-image" fit="cover" />
								<div class="author-avatar">
									<el-avatar :size="32" :src="forum.authorAvatar || defaultAvatar">
										{{ forum.authorName ? forum.authorName.substring(0, 1) : 'U' }}
									</el-avatar>
								</div>
							</div>

							<!-- 帖子信息 -->
							<div class="forum-content">
								<!-- 帖子标题 -->
								<h3 class="forum-title">{{ forum.title }}</h3>

								<!-- 帖子摘要 -->
								<p class="forum-excerpt">{{ getExcerpt(forum.content) }}</p>

								<!-- 帖子元数据 -->
								<div class="forum-meta">
									<div class="author-info">
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
											<Star />
										</el-icon>
										<span>{{ forum.likeCount || 0 }}</span>
									</div>
								</div>
							</div>

							<!-- 帖子标签 -->
							<div class="forum-tags" v-if="forum.tags && forum.tags.length">
								<el-tag v-for="tag in forum.tags" :key="tag" size="small" class="forum-tag">
									{{ tag }}
								</el-tag>
							</div>
						</el-card>
					</el-col>
				</el-row>

				<!-- 加载更多占位 -->
				<div class="load-more" v-if="forumList.length > 0">
					<el-skeleton :rows="1" animated v-if="loading" />
					<el-button v-else-if="hasMore" @click="loadMore" text>加载更多</el-button>
					<div v-else class="no-more">已经到底啦 ~</div>
				</div>

				<!-- 无数据时显示 -->
				<el-empty v-if="forumList.length === 0 && !loading" description="暂无帖子" />

				<!-- 初始加载时的骨架屏 -->
				<div v-if="initLoading" class="skeleton-container">
					<el-skeleton :rows="3" animated v-for="i in 8" :key="i" class="skeleton-item" />
				</div>
			</div>
		</el-scrollbar>
	</div>

	<!-- 帖子详情组件 -->
	<forum-detail :forum-id="selectedForumId" @update-forum="handleForumUpdate" />
</template>

<script setup>
import { getCurrentInstance, reactive, ref, watch, onMounted } from "vue";
import { imageBaseURL } from "../global/index.js";
import {
	Clock, ChatLineRound, View, Star
} from '@element-plus/icons-vue';
import ForumDetail from '../components/forum/ForumDetail.vue';

const { proxy } = getCurrentInstance();

// 接收搜索关键词
const props = defineProps({
	searchQuery: {
		type: String,
		default: ''
	}
});

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

// 帖子列表
let forumList = ref([]);
// 查询表单
let queryForm = reactive({ title: '' });
// 分页参数
const pageSize = ref(10);
const currentPage = ref(1);
const hasMore = ref(true);
const loading = ref(false);
const initLoading = ref(true);

// 选中的帖子ID（用于详情组件）
const selectedForumId = ref(null);

// 初始化帖子列表
const forumInit = async () => {
	loading.value = true;

	let param = {
		...queryForm,
		pageNum: currentPage.value,
		pageSize: pageSize.value
	};

	try {
		let result = await proxy.axios.post('forum/list', param);
		if (!result) {
			return;
		}

		// 假设API返回{list: Array, total: Number}格式的数据
		if (currentPage.value === 1) {
			forumList.value = result.list || result;
		} else {
			forumList.value = [...forumList.value, ...(result.list || result)];
		}

		// 判断是否还有更多数据
		hasMore.value = Array.isArray(result)
			? result.length === pageSize.value
			: forumList.value.length < (result.total || 0);
	} catch (error) {
		console.error("加载帖子失败", error);
		proxy.msg.error("加载帖子失败");
	} finally {
		loading.value = false;
		initLoading.value = false;
	}
};

// 监听搜索关键词变化
watch(() => props.searchQuery, (newVal) => {
	queryForm.title = newVal;
	// 重置分页参数
	currentPage.value = 1;
	forumList.value = [];
	hasMore.value = true;

	// 重新加载数据
	forumInit();
}, { immediate: true });

// 首次加载
onMounted(() => {
	forumInit();
});

// 加载更多
const loadMore = () => {
	if (loading.value || !hasMore.value) return;
	currentPage.value++;
	forumInit();
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

// 获取内容摘要
const getExcerpt = (content) => {
	if (!content) return '';
	return content.length > 60 ? content.substring(0, 60) + '...' : content;
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
</script>

<style scoped>
.home-container {
	padding: 0 16px;
}

.main-scrollbar {
	scrollbar-width: thin;
}

.main-scrollbar :deep(.el-scrollbar__bar) {
	z-index: 2;
	opacity: 0.1;
	transition: opacity 0.3s;
}

main-scrollbar:hover :deep(.el-scrollbar__bar) {
	opacity: 0.3;
}

.masonry-container {
	max-width: 1600px;
	margin: 0 auto;
}

.masonry-item {
	margin-bottom: 24px;
	transition: transform 0.3s ease;
}

.masonry-item:hover {
	transform: translateY(-5px);
}

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

.load-more {
	text-align: center;
	margin-top: 16px;
	margin-bottom: 32px;
	color: #909399;
}

.no-more {
	font-size: 14px;
	color: #909399;
	padding: 16px 0;
	text-align: center;
}

.skeleton-container {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
	gap: 20px;
	padding: 20px 0;
}

.skeleton-item {
	background: #fff;
	border-radius: 12px;
	padding: 20px;
	box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>