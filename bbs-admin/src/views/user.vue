<template>
  <div class="user">
    <div>
      <el-form :model="queryForm" label-width="60px" :inline="true">
        <el-form-item label="账号">
          <el-input v-model="queryForm.account" clearable style="width:300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="init()">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div style="margin-top: 1%;">
      <el-table :data="tableData" style="width: 100%;" max-height="500" stripe border highlight-current-row
        size="large">
        <el-table-column align="center" prop="account" label="账号" />
        <el-table-column align="center" prop="name" label="名称" />
        <el-table-column align="center" prop="mail" label="邮箱" />
        <el-table-column align="center" prop="createTime" label="注册时间" />
        <el-table-column align="center" prop="updateTime" label="修改时间" />
        <el-table-column align="center" label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleDetail(scope.$index, scope.row)">详情</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--分页查询，绑定了queryForm的当前页page和每页大小pageSize-->
      <el-pagination background layout="total, sizes, prev, pager, next" style="margin-top: 2%;"
        :page-sizes="[10, 20, 50]" @size-change="init" @current-change="init" v-model:page-size="queryForm.pageSize"
        v-model:current-page="queryForm.page" :total="totalData" />
    </div>

    <el-dialog v-model="detailDialog" title="用户详情" append-to-body>
      <el-form :model="detailForm">
        <el-form-item label="账号" prop="account" :label-width="formLabelWidth">
          <el-input v-model="detailForm.account" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="name" :label-width="formLabelWidth">
          <el-input v-model="detailForm.name" disabled />
        </el-form-item>
        <el-form-item prop="head" label="头像" :label-width="formLabelWidth">
          <el-image style="height: 200px;" :src="imageBaseURL + detailForm.headPath" fit="fill"
            :preview-src-list="[imageBaseURL + detailForm.head]" />
        </el-form-item>
        <el-form-item label="邮箱" prop="mail" :label-width="formLabelWidth">
          <el-input v-model="detailForm.mail" disabled />
        </el-form-item>
        <el-form-item label="注册时间" prop="createTime" :label-width="formLabelWidth">
          <el-input v-model="detailForm.createTime" disabled />
        </el-form-item>
        <el-form-item label="修改时间" prop="updateTime" :label-width="formLabelWidth">
          <el-input v-model="detailForm.updateTime" disabled />
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>

</template>

<script setup>
import { getCurrentInstance, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { imageBaseURL } from "../global/index.js"
const { proxy } = getCurrentInstance()

//查询表单，绑定el-pagination组件中的属性
const queryForm = reactive({})
//总数据数
const totalData = ref(0)

//详情开关
const detailDialog = ref(false)
//详情表单
const detailForm = reactive({})

const formLabelWidth = ref(80);

//表格数据
let tableData = ref([]);
//用户数据展示初始化/查询
const init = async () => {
  //设置查询参数
  let params = Object.assign({}, queryForm);
  //查询
  let result = await proxy.axios.post('user/list', params)
  if (!result) {
    return;
  }
  //设置数据到表格
  tableData.value = result.list;
  //设置数据总数
  totalData.value = result.total;
  console.log(totalData.value);
}
init();

//用户详细信息框
const handleDetail = (idx, rowData) => {
  Object.assign(detailForm, rowData)
  detailDialog.value = true
}
//删除用户
const handleDelete = async (idx, rowData) => {
  let result = await proxy.axios.get('user/delete?id=' + rowData.id)
  if (!result) {
    return
  }
  proxy.msg.success('删除成功')
  init()
}


</script>

<style>
.user {
  position: relative;
  left: 8vw;
  width: 70vw;
}
</style>