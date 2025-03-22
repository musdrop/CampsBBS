<template>
  <div class="user-container">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <div class="page-description">管理社区中的所有用户账号</div>
    </div>

    <el-card class="query-card" shadow="hover">
      <div class="query-form">
        <el-form :model="queryForm" label-width="60px" :inline="true">
          <el-form-item label="用户名">
            <el-input v-model="queryForm.name" clearable style="width:200px" placeholder="请输入用户名" prefix-icon="Search"
              @keyup.enter="init()" />
          </el-form-item>
          <el-form-item label="账号">
            <el-input v-model="queryForm.account" clearable style="width:200px" placeholder="请输入账号" prefix-icon="Search"
              @keyup.enter="init()" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="init()" :icon="Search">查询</el-button>
            <el-button @click="resetQuery" :icon="Refresh">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="table-card" shadow="hover">
      <div class="table-header">
        <div class="table-title">用户列表</div>
        <div class="table-info">共 {{ totalData }} 条数据</div>
      </div>

      <el-table :data="tableData" style="width: 100%;" stripe border highlight-current-row size="large"
        v-loading="loading" :header-cell-style="{ 'background-color': '#f5f7fa', 'color': '#606266' }"
        :cell-style="{ 'text-align': 'center' }">
        <!-- 用户基本信息 -->
        <el-table-column align="center" label="用户信息" min-width="240" :show-overflow-tooltip="true">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="50" :src="getAvatarUrl(row.head)">
                {{ row.name?.charAt(0).toUpperCase() }}
              </el-avatar>
              <div class="user-text">
                <div class="user-name">{{ row.name }}</div>
                <div class="user-account">{{ row.account }}</div>
                <div class="user-email">{{ row.mail }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 权限信息 -->
        <el-table-column align="center" label="权限" :show-overflow-tooltip="true">
          <template #default="{ row }">
            <el-tag :type="row.auth === 1 ? 'danger' : 'info'" effect="plain">
              {{ row.auth === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 日期时间信息 -->
        <el-table-column align="center" label="注册/更新时间" :show-overflow-tooltip="true">
          <template #default="{ row }">
            <div class="time-cell">
              <div class="create-time">
                <el-icon>
                  <Timer />
                </el-icon>
                <el-tooltip :content="row.createTime" placement="top">
                  <span>{{ formatTime(row.createTime) }}</span>
                </el-tooltip>
              </div>
              <div class="update-time" v-if="row.updateTime !== row.createTime">
                <el-icon>
                  <Edit />
                </el-icon>
                <el-tooltip :content="row.updateTime" placement="top">
                  <span>{{ formatTime(row.updateTime) }}</span>
                </el-tooltip>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column align="center" label="操作" :show-overflow-tooltip="true" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="handleDetail(scope.$index, scope.row)" :icon="View">
              详情
            </el-button>
            <el-popconfirm title="确定要删除该用户吗?" confirm-button-text="确定" cancel-button-text="取消"
              @confirm="handleDelete(scope.$index, scope.row)">
              <template #reference>
                <el-button size="small" type="danger" plain :icon="Delete">
                  删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination background layout="total, sizes, prev, pager, next, jumper" class="pagination"
        :page-sizes="[10, 20, 50, 100]" @size-change="handleSizeChange" @current-change="handleCurrentChange"
        v-model:page-size="queryForm.pageSize" v-model:current-page="queryForm.page" :total="totalData" />
    </el-card>

    <!-- 详情弹窗 -->
    <user-detail v-model="detailDialog" :detail-data="detailForm" />
  </div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref, onMounted, onActivated } from "vue"
import { imageBaseURL } from "../global/index.js"
import {
  Search,
  Refresh,
  View,
  Edit,
  Delete,
  Timer,
} from '@element-plus/icons-vue'
import UserDetail from "../components/user/UserDetail.vue"

const { proxy } = getCurrentInstance()

const queryForm = reactive({
  name: '',
  account: '',
  page: 1,
  pageSize: 10
})
const totalData = ref(0)
const loading = ref(false)

// 详情弹窗
const detailDialog = ref(false)
const detailForm = reactive({})

let tableData = ref([]);

const init = async () => {
  console.log("User页面初始化");
  loading.value = true;

  let params = Object.assign({}, queryForm);
  try {
    let result = await proxy.axios.post('user/list', params)
    if (!result) {
      return;
    }
    console.log("获取用户数据成功:", result);
    tableData.value = result.list;
    totalData.value = result.total;
  } catch (error) {
    console.error("查询用户失败:", error);
    proxy.msg.error("查询用户失败");
  } finally {
    loading.value = false;
  }
}

// 使用onActivated钩子，确保每次组件激活时都刷新数据
onActivated(() => {
  console.log("User组件被激活");
  init();
});

// 组件挂载后确保数据加载
onMounted(() => {
  console.log("User组件挂载完成");
  // 确保在DOM更新后执行
  setTimeout(() => {
    init();
  }, 0);
});

const resetQuery = () => {
  queryForm.name = '';
  queryForm.account = '';
  queryForm.page = 1;
  init();
}

const handleSizeChange = (size) => {
  queryForm.pageSize = size;
  init();
}

const handleCurrentChange = (page) => {
  queryForm.page = page;
  init();
}

const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  // 获取当前日期
  const now = new Date();
  // 计算时间差（毫秒）
  const diff = now - date;

  // 如果是今天
  if (diff < 86400000 && date.getDate() === now.getDate()) {
    return `今天 ${date.toLocaleTimeString().substring(0, 5)}`;
  }
  // 如果是昨天
  else if (diff < 172800000 && date.getDate() === now.getDate() - 1) {
    return `昨天 ${date.toLocaleTimeString().substring(0, 5)}`;
  }
  // 如果是最近7天
  else if (diff < 604800000) {
    const days = ['日', '一', '二', '三', '四', '五', '六'];
    return `周${days[date.getDay()]} ${date.toLocaleTimeString().substring(0, 5)}`;
  }
  // 否则返回完整日期
  else {
    return `${date.toLocaleDateString()} ${date.toLocaleTimeString().substring(0, 5)}`;
  }
};

const getAvatarUrl = (avatar) => {
  if (!avatar || avatar === 'noHead') {
    return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
  }
  return imageBaseURL + avatar;
};

const handleDetail = async (idx, rowData) => {
  Object.assign(detailForm, rowData);
  detailDialog.value = true;
}

const handleDelete = async (idx, rowData) => {
  try {
    loading.value = true;
    const result = await proxy.axios.get(`user/delete?id=${rowData.id}`);

    if (!result) return;

    proxy.msg.success('用户删除成功');
    init();
  } catch (error) {
    console.error("删除用户失败:", error);
    proxy.msg.error("删除用户失败");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.user-container {
  width: 100%;
  height: 100%;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.page-description {
  font-size: 14px;
  color: #909399;
}

.query-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.table-info {
  font-size: 14px;
  color: #909399;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 8px;
  text-align: left;
  gap: 16px;
}

.user-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.user-account {
  font-size: 14px;
  color: #606266;
}

.user-email {
  font-size: 13px;
  color: #909399;
}

.time-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.create-time,
.update-time {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.create-time {
  color: #67c23a;
}

.update-time {
  color: #e6a23c;
}

/* 响应式样式 */
@media screen and (max-width: 1200px) {
  .user-container {
    padding: 15px;
  }
}

@media screen and (max-width: 768px) {
  .user-container {
    padding: 10px;
  }
}
</style>