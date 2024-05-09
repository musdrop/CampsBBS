<template>
  <div class="forum">
    <div>
      <el-form :model="queryForm" label-width="60px" :inline="true">
        <el-form-item label="标题">
          <el-input v-model="queryForm.title" clearable style="width:300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="init()">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div style="margin-top: 1%;">
      <el-table :data="tableData" style="width: 100%;" stripe border highlight-current-row size="large">
        <el-table-column align="center" prop="title" label="标题" />
        <el-table-column align="center" prop="content" label="内容" />
        <el-table-column align="center" prop="createTime" label="发布时间" />
        <el-table-column align="center" prop="updateTime" label="修改时间" />
        <el-table-column align="center" label="操作" width="300">
          <template #default="scope">
            <el-button size="small" @click="handleDetail(scope.$index, scope.row)">详情</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
            <el-button size="small" @click="handleComment(scope.$index, scope.row)">查看评论</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination background layout="total, sizes, prev, pager, next" style="margin-top: 2%;"
        :page-sizes="[10, 20, 50]" @size-change="init" @current-change="init" v-model:page-size="queryForm.pageSize"
        v-model:current-page="queryForm.page" :total="totalData" />
    </div>

    <el-dialog v-model="detailDialog" title="帖子详情" append-to-body>
      <el-form :model="detailForm">
        <el-form-item label="标题" prop="title" :label-width="formLabelWidth">
          <el-input v-model="detailForm.title" disabled />
        </el-form-item>
        <el-form-item label="作者名称" prop="authorName" :label-width="formLabelWidth">
          <el-input v-model="detailForm.authorName" disabled />
        </el-form-item>
        <el-form-item label="作者账号" prop="authorAccount" :label-width="formLabelWidth">
          <el-input v-model="detailForm.authorAccount" disabled />
        </el-form-item>
        <el-form-item prop="coverImage" label="封面" :label-width="formLabelWidth">
          <el-image style="height: 200px;" :src="imageBaseURL + detailForm.coverImage" fit="fill"
            :preview-src-list="[imageBaseURL + detailForm.coverImage]" />
        </el-form-item>
        <el-form-item label="内容" prop="content" :label-width="formLabelWidth">
          <el-input type="textarea" rows="15" v-model="detailForm.content" disabled />
        </el-form-item>
        <el-form-item label="发布时间" prop="createTime" :label-width="formLabelWidth">
          <el-input v-model="detailForm.createTime" disabled />
        </el-form-item>
        <el-form-item label="修改时间" prop="updateTime" :label-width="formLabelWidth">
          <el-input v-model="detailForm.updateTime" disabled />
        </el-form-item>
      </el-form>
    </el-dialog>

    <el-dialog v-model="commentDialog" title="帖子评论" append-to-body>
      <el-table :data="commentData" style="width: 100%;" max-height="600" stripe border highlight-current-row
        size="large">
        <el-table-column align="center" prop="authorName" label="用户名称" />
        <el-table-column align="center" prop="authorAccount" label="用户账号" />
        <el-table-column align="center" prop="content" label="评论内容" />
        <el-table-column align="center" prop="createTime" label="评论时间" />
        <el-table-column align="center" label="操作" width="300">
          <template #default="scope">
            <el-button size="small" type="danger" @click="DeleteComment(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>

</template>

<script setup>
import { getCurrentInstance, reactive, ref } from "vue"
import { imageBaseURL } from "../global/index.js"
const { proxy } = getCurrentInstance()

const queryForm = reactive({})
const totalData = ref(0)

//详情
const detailDialog = ref(false)
const detailForm = reactive({})

const formLabelWidth = ref(80);


let tableData = ref([]);
const init = async () => {
  let params = Object.assign({}, queryForm);
  let result = await proxy.axios.post('forum/list', params)
  if (!result) {
    return;
  }
  tableData.value = result.list;
  totalData.value = result.total;
}
init();

const handleDetail = async (idx, rowData) => {
  let result = await proxy.axios.get('user/detail?id=' + rowData.authorId)
  Object.assign(detailForm, rowData)
  detailForm.authorName = result.name
  detailForm.authorAccount = result.account
  detailDialog.value = true
}

const handleDelete = async (idx, rowData) => {
  let result = await proxy.axios.get('forum/delete?id=' + rowData.id)
  if (!result) {
    return
  }
  proxy.msg.success('删除成功')
  init()
}

let commentData = ref([])
let commentDialog = ref(false)
const handleComment = async (idx, rowData) => {
  let forumId = rowData.id
  let result = await proxy.axios.get('comment/list?forumId=' + forumId)
  console.log(result);
  commentData.value = result
  commentDialog.value = true
}

const DeleteComment = async (idx, rowData) => {
  let result = await proxy.axios.get('comment/delete?id=' + rowData.id)
  if (!result) {
    return
  }
  proxy.msg.success('删除成功')
  commentData.value = commentData.value.filter(item => item.id != rowData.id)
}

</script>

<style>
.forum {
  position: relative;
  left: 8vw;
  width: 70vw;
}
</style>