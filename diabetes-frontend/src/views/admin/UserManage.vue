<template>
  <div class="user-manage">
    <h3 class="page-title">用户管理</h3>
    <el-card shadow="never">
      <el-table :data="users" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column prop="name" label="姓名" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" width="110" />
        <el-table-column prop="age" label="年龄" width="50" />
        <el-table-column prop="gender" label="性别" width="50" />
        <el-table-column prop="status" label="状态" width="70">
          <template #default="scope">
            <el-tag :type="scope.row.status === '正常' ? 'success' : 'danger'" size="small">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="scope">
            <el-button :type="scope.row.status === '正常' ? 'warning' : 'success'" size="small" @click="toggleStatus(scope.row)">
              {{ scope.row.status === '正常' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { userApi } from "../../api/index.js";

const users = ref([]);

const loadData = async () => {
  const res = await userApi.list();
  if (res.code === 200) users.value = res.data || [];
};

const toggleStatus = async (row) => {
  await ElMessageBox.confirm(`确定${row.status === '正常' ? '禁用' : '启用'}用户 ${row.name}？`, "提示", { type: "warning" });
  row.status = row.status === '正常' ? '禁用' : '正常';
  await userApi.update({ id: row.id, status: row.status });
  ElMessage.success("操作成功");
  loadData();
};

onMounted(loadData);
</script>

<style scoped>
.user-manage { max-width: 600px; margin: 0 auto; }
.page-title { font-size: 22px; margin-bottom: 18px; font-family: var(--font-display); color: var(--gray-900); letter-spacing: 0.04em; }
</style>
