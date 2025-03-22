<template>
	<div class="home-container">
		<el-scrollbar height="95vh" class="main-scrollbar" ref="mainScrollRef">
			<!-- 瀑布流布局展示帖子 -->
			<div class="masonry-container">
				<el-row :gutter="20">
					<el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="forum in forumList" :key="forum.id"
						class="masonry-item">
						<!-- 使用提取的帖子卡片组件 -->
						<forum-card :forum="forum" @card-click="handleDetail" />
					</el-col>
				</el-row>

				<!-- 加载状态显示 -->
				<div class="load-more" v-if="forumList.length > 0" ref="loadMoreRef">
					<el-skeleton :rows="1" animated v-if="loading" />
					<div v-else-if="!hasMore" class="no-more">已经到底啦 ~</div>
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
import { getCurrentInstance, reactive, ref, watch, onMounted, onBeforeUnmount } from "vue";
import ForumCard from '../components/forum/ForumCard.vue';
import ForumDetail from '../components/forum/ForumDetail.vue';

const { proxy } = getCurrentInstance();

// 接收搜索关键词
const props = defineProps({
	searchQuery: {
		type: String,
		default: ''
	}
});

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

const mainScrollRef = ref(null);
const loadMoreRef = ref(null);

// 使用Intersection Observer实现滚动加载
let observer = null;

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
	setupIntersectionObserver();
});

onBeforeUnmount(() => {
	if (observer) {
		observer.disconnect();
	}
});

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
</script>

<style scoped>
.home-container {
	padding: 20px 16px;
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