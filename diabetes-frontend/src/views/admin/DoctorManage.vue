<template>
  <div class="doctor-manage">
    <div class="page-header">
      <h3 class="page-title">医师管理</h3>
      <el-button type="primary" size="small" @click="showAdd">新增医师</el-button>
    </div>

    <el-card shadow="never">
      <div class="table-wrap">
      <el-table :data="doctors" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="name" label="姓名" width="70" />
        <el-table-column prop="department" label="科室" width="80" />
        <el-table-column prop="title" label="职称" width="90" />
        <el-table-column prop="phone" label="电话" width="110" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="showEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑医师' : '新增医师'" width="95%" top="5vh">
      <el-form ref="formRef" :model="form" label-width="80px">
        <el-form-item label="姓名" required><el-input v-model="form.name" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="科室"><el-input v-model="form.department" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="职称"><el-input v-model="form.title" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="擅长领域"><el-input v-model="form.goodAt" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.introduction" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { doctorApi } from "../../api/index.js";

const doctors = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const form = ref({ name: "", department: "", title: "", phone: "", goodAt: "", introduction: "" });

const loadData = async () => {
  const res = await doctorApi.list();
  if (res.code === 200) doctors.value = res.data || [];
};

const showAdd = () => {
  isEdit.value = false;
  form.value = { name: "", department: "", title: "", phone: "", goodAt: "", introduction: "" };
  dialogVisible.value = true;
};

const showEdit = (row) => {
  isEdit.value = true;
  form.value = { ...row };
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (isEdit.value) {
    await doctorApi.update(form.value);
    ElMessage.success("更新成功");
  } else {
    await doctorApi.add(form.value);
    ElMessage.success("添加成功");
  }
  dialogVisible.value = false;
  loadData();
};

const handleDelete = async (id) => {
  await ElMessageBox.confirm("确定删除？", "提示", { type: "warning" });
  await doctorApi.delete(id);
  ElMessage.success("删除成功");
  loadData();
};

onMounted(loadData);
</script>

<style scoped>
.doctor-manage { width: 100%; }
.table-wrap { overflow-x: auto; border-radius: 10px; border: 1px solid var(--gray-100); }
.page-title { font-size: 22px; margin-bottom: 18px; font-family: var(--font-display); color: var(--gray-900); letter-spacing: 0.04em; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
.page-header .page-title { margin-bottom: 0; }
</style>
