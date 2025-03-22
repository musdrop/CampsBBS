<template>
  <div class="dashboard-container">
    <h1 class="dashboard-title">论坛仪表盘</h1>

    <div class="overview-cards">
      <el-card class="card" shadow="hover">
        <div class="card-content">
          <div class="card-icon comments">
            <el-icon>
              <ChatLineSquare />
            </el-icon>
          </div>
          <div class="card-info">
            <div class="card-title">评论总数</div>
            <div class="card-number">{{ forumInfo.commentCount }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="card" shadow="hover">
        <div class="card-content">
          <div class="card-icon users">
            <el-icon>
              <User />
            </el-icon>
          </div>
          <div class="card-info">
            <div class="card-title">用户总数</div>
            <div class="card-number">{{ forumInfo.userCount }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="card" shadow="hover">
        <div class="card-content">
          <div class="card-icon forums">
            <el-icon>
              <Flag />
            </el-icon>
          </div>
          <div class="card-info">
            <div class="card-title">帖子总数</div>
            <div class="card-number">{{ forumInfo.forumCount }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="card" shadow="hover">
        <div class="card-content">
          <div class="card-icon today">
            <el-icon>
              <DataLine />
            </el-icon>
          </div>
          <div class="card-info">
            <div class="card-title">今日新增帖子</div>
            <div class="card-number">{{ forumInfo.todayForumCount }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <div class="charts-container">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-title">
                <span>论坛统计数据</span>
              </div>
            </template>
            <div class="chart">
              <div ref="pieChartRef" class="chart-container"></div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-title">
                <span>数据分布</span>
              </div>
            </template>
            <div class="chart">
              <div ref="barChartRef" class="chart-container"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick, getCurrentInstance } from 'vue'
import * as echarts from 'echarts'
import {
  ChatLineSquare,
  User,
  Flag,
  DataLine
} from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()

// 声明响应式数据
const forumInfo = reactive({
  commentCount: 0,
  userCount: 0,
  forumCount: 0,
  todayForumCount: 0
})

// 图表引用
const pieChartRef = ref(null)
const barChartRef = ref(null)
let pieChart = null
let barChart = null

// 获取论坛信息
const fetchForumInfo = async () => {
  try {
    console.log("Dashboard页面初始化，获取论坛统计信息");
    const result = await proxy.axios.get('info/all')
    if (!result) {
      return
    }
    console.log("获取论坛统计数据成功:", result);

    // 更新数据
    Object.assign(forumInfo, result)

    // 在DOM更新后初始化图表
    await nextTick()
    initPieChart()
    initBarChart()
  } catch (error) {
    console.error('获取论坛信息失败：', error)
    proxy.msg.error('获取论坛统计信息失败')
  }
}

// 初始化饼图
const initPieChart = () => {
  if (pieChart) {
    pieChart.dispose()
  }

  pieChart = echarts.init(pieChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 10,
      left: 'center',
      data: ['评论', '用户', '帖子', '今日新增帖子']
    },
    series: [
      {
        name: '论坛统计',
        type: 'pie',
        radius: ['40%', '65%'],
        center: ['50%', '40%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          position: 'outside',
          formatter: '{b}: {c} ({d}%)'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '16',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: true
        },
        data: [
          { value: forumInfo.commentCount, name: '评论' },
          { value: forumInfo.userCount, name: '用户' },
          { value: forumInfo.forumCount, name: '帖子' },
          { value: forumInfo.todayForumCount, name: '今日新增帖子' }
        ]
      }
    ]
  }
  pieChart.setOption(option)
  window.addEventListener('resize', () => pieChart.resize())
}

// 初始化柱状图
const initBarChart = () => {
  if (barChart) {
    barChart.dispose()
  }

  barChart = echarts.init(barChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['评论数', '用户数', '帖子数', '今日新增']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '数量',
        type: 'bar',
        barWidth: '60%',
        data: [
          forumInfo.commentCount,
          forumInfo.userCount,
          forumInfo.forumCount,
          forumInfo.todayForumCount
        ],
        itemStyle: {
          color: function (params) {
            const colors = ['#91cc75', '#5470c6', '#ee6666', '#fac858']
            return colors[params.dataIndex]
          }
        }
      }
    ]
  }
  barChart.setOption(option)
  window.addEventListener('resize', () => barChart.resize())
}

// 生命周期钩子
onMounted(() => {
  console.log("Dashboard组件挂载完成");
  // 确保在DOM更新后执行
  setTimeout(() => {
    fetchForumInfo()
  }, 0)
})

// 组件销毁前清理图表实例和事件监听
onBeforeUnmount(() => {
  if (pieChart) {
    window.removeEventListener('resize', () => pieChart.resize())
    pieChart.dispose()
    pieChart = null
  }
  if (barChart) {
    window.removeEventListener('resize', () => barChart.resize())
    barChart.dispose()
    barChart = null
  }
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.dashboard-title {
  font-size: 24px;
  margin-bottom: 24px;
  color: #333;
  font-weight: 600;
}

.overview-cards {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
}

.card {
  width: 23%;
  margin-right: 10px;
}

.card-content {
  display: flex;
  align-items: center;
}

.card-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  margin-right: 15px;
}

.card-icon i {
  font-size: 30px;
  color: #fff;
}

.comments {
  background-color: #91cc75;
}

.users {
  background-color: #5470c6;
}

.forums {
  background-color: #ee6666;
}

.today {
  background-color: #fac858;
}

.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.card-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.charts-container {
  margin-top: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.chart-title {
  font-size: 16px;
  color: #333;
}

.chart {
  padding: 10px;
  height: 400px;
}

.chart-container {
  width: 100%;
  height: 380px;
}

@media screen and (max-width: 1200px) {
  .overview-cards {
    flex-wrap: wrap;
  }

  .card {
    width: 48%;
    margin-bottom: 15px;
  }
}

@media screen and (max-width: 768px) {
  .card {
    width: 100%;
  }
}
</style>