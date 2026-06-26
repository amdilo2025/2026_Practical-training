<template>
  <div class="article-manage">
    <div class="page-header">
      <h3 class="page-title">资讯管理</h3>
      <el-button type="primary" size="small" @click="showAdd">发布资讯</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="articles" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="title" label="标题" min-width="120" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="70">
          <template #default="scope"><el-tag size="small">{{ scope.row.category }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="views" label="浏览" width="60" />
        <el-table-column prop="status" label="状态" width="70">
          <template #default="scope"><el-tag :type="scope.row.status === '已发布' ? 'success' : 'info'" size="small">{{ scope.row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="showEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑资讯' : '发布资讯'" width="95%" top="3vh" :fullscreen="true">
      <el-form ref="formRef" :model="form" label-width="80px">
        <el-form-item label="标题" required><el-input v-model="form.title" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="分类"><el-select v-model="form.category" style="width:100%"><el-option v-for="c in categories" :key="c" :label="c" :value="c" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="作者"><el-input v-model="form.author" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="12" /></el-form-item>
        <el-form-item label="封面"><el-input v-model="form.cover" placeholder="封面图片URL" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button @click="saveAsDraft">存草稿</el-button>
        <el-button type="primary" @click="handleSubmit">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { articleApi } from "../../api/index.js";

const articles = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const categories = ["饮食", "运动", "用药", "科普", "并发症"];
const form = ref({ title: "", category: "科普", author: "", summary: "", content: "", cover: "", status: "已发布" });

const loadData = async () => {
  const res = await articleApi.list();
  if (res.code === 200) articles.value = res.data || [];
};

const showAdd = () => {
  isEdit.value = false;
  form.value = { title: "", category: "科普", author: "", summary: "", content: "", cover: "", status: "已发布" };
  dialogVisible.value = true;
};

const showEdit = (row) => {
  isEdit.value = true;
  form.value = { ...row };
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  form.value.status = "已发布";
  if (isEdit.value) {
    await articleApi.update(form.value);
    ElMessage.success("更新成功");
  } else {
    await articleApi.add(form.value);
    ElMessage.success("发布成功");
  }
  dialogVisible.value = false;
  loadData();
};

const saveAsDraft = async () => {
  form.value.status = "草稿";
  if (isEdit.value) {
    await articleApi.update(form.value);
  } else {
    await articleApi.add(form.value);
  }
  ElMessage.success("已保存为草稿");
  dialogVisible.value = false;
  loadData();
};

const handleDelete = async (id) => {
  await ElMessageBox.confirm("确定删除？", "提示", { type: "warning" });
  await articleApi.delete(id);
  ElMessage.success("删除成功");
  loadData();
};

onMounted(loadData);
</script>

<style scoped>
.article-manage { max-width: 600px; margin: 0 auto; }
.page-title { font-size: 22px; margin-bottom: 18px; font-family: var(--font-display); color: var(--gray-900); letter-spacing: 0.04em; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
.page-header .page-title { margin-bottom: 0; }
</style>
