<template>
  <div class="ai-query-page">
    <div class="page-header">
      <el-icon :size="24" color="#22d3ee"><MagicStick /></el-icon>
      <span class="page-title">AI 智能查询</span>
    </div>
    <p class="page-desc">用自然语言向 AI 提问，快速查询平台数据</p>

    <!-- 输入区域 -->
    <div class="query-input-area">
      <el-input
        v-model="question"
        type="textarea"
        :rows="3"
        placeholder="输入你想查询的内容，例如：查询所有正常状态的用户、统计各风险等级的人数、查询待回复的咨询..."
        maxlength="500"
        show-word-limit
      />
      <el-button
        type="primary"
        :loading="loading"
        :disabled="!question.trim()"
        class="query-btn"
        @click="handleQuery"
      >
        <el-icon v-if="!loading"><Search /></el-icon>
        {{ loading ? '查询中...' : '开始查询' }}
      </el-button>
    </div>

    <!-- 快捷问题 -->
    <div class="quick-questions">
      <span class="quick-label">快捷查询：</span>
      <el-tag
        v-for="q in quickQuestions"
        :key="q"
        class="quick-tag"
        @click="question = q; handleQuery()"
      >
        {{ q }}
      </el-tag>
    </div>

    <!-- 结果区域 -->
    <div v-if="result" class="result-area">
      <div class="result-header">
        <span class="result-label">查询结果</span>
        <el-button text size="small" type="primary" @click="copyResult">复制结果</el-button>
      </div>
      <div class="result-table-wrapper" v-if="isTableData">
        <el-table :data="result" border stripe max-height="500" style="width:100%">
          <el-table-column
            v-for="col in columns"
            :key="col"
            :prop="col"
            :label="getColumnLabel(col)"
            :min-width="120"
            show-overflow-tooltip
          />
        </el-table>
      </div>
      <div v-else class="result-json">
        <pre>{{ JSON.stringify(result, null, 2) }}</pre>
      </div>
    </div>

    <!-- 错误提示 -->
    <el-alert v-if="error" :title="error" type="error" show-icon closable @close="error=''" class="error-alert" />
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { adminApi } from "../../api/index.js";
import { ElMessage } from "element-plus";

const question = ref("");
const loading = ref(false);
const result = ref(null);
const error = ref("");

const quickQuestions = [
  "查询所有正常状态的用户",
  "统计各风险等级的用户数量",
  "查询待回复的咨询",
  "统计各科室的医师数量",
  "查询各分类健康资讯的数量和浏览量",
  "统计每个用户的打卡完成率",
];

// 英文字段名 → 中文列名
const fieldLabelMap = {
  id: "ID", username: "用户名", password: "密码", name: "姓名", phone: "手机号",
  avatar: "头像", age: "年龄", gender: "性别", height: "身高(cm)", weight: "体重(kg)",
  diabetes_type: "糖尿病类型", family_history: "家族史", status: "状态",
  created_time: "创建时间", updated_time: "更新时间", role: "角色",
  department: "科室", title: "职称", introduction: "简介", good_at: "擅长领域",
  is_ai: "AI医生", doctor_id: "医师ID", user_id: "用户ID",
  content: "内容", reply: "回复", reply_time: "回复时间",
  fasting_blood_sugar: "空腹血糖", postprandial_blood_sugar: "餐后血糖",
  blood_pressure_systolic: "收缩压", blood_pressure_diastolic: "舒张压",
  bmi: "BMI", risk_level: "风险等级", risk_score: "风险评分", advice: "建议",
  summary: "摘要", cover: "封面", category: "分类", author: "作者", views: "浏览量",
  checkin_date: "打卡日期", checkin_type: "打卡类型", remark: "备注",
  session_id: "会话ID", plan_type: "方案类型", checkin_status: "完成状态",
};

const getColumnLabel = (key) => fieldLabelMap[key] || key;

const columns = computed(() => {
  if (!result.value || !Array.isArray(result.value) || result.value.length === 0) return [];
  return Object.keys(result.value[0]);
});

const isTableData = computed(() => {
  return Array.isArray(result.value) && result.value.length > 0 && typeof result.value[0] === "object";
});

const handleQuery = async () => {
  if (!question.value.trim()) return;
  loading.value = true;
  error.value = "";
  result.value = null;
  try {
    const res = await adminApi.aiQuery(question.value);
    result.value = res.data;
    if (!res.data || (Array.isArray(res.data) && res.data.length === 0)) {
      ElMessage.info("未查询到相关数据");
    }
  } catch (e) {
    error.value = e.response?.data?.message || e.message || "查询失败";
  } finally {
    loading.value = false;
  }
};

const copyResult = () => {
  navigator.clipboard.writeText(JSON.stringify(result.value, null, 2))
    .then(() => ElMessage.success("已复制到剪贴板"))
    .catch(() => ElMessage.error("复制失败"));
};
</script>

<style scoped>
.ai-query-page {
  max-width: 900px;
  margin: 0 auto;
}
.page-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-bright);
  font-family: var(--font-display);
}
.page-desc {
  color: var(--text-secondary);
  font-size: 13px;
  margin-bottom: 20px;
}
.query-input-area {
  background: rgba(8, 47, 73, 0.3);
  border: 1px solid rgba(34, 211, 238, 0.15);
  border-radius: 12px;
  padding: 16px;
}
.query-input-area :deep(.el-textarea__inner) {
  background: rgba(6, 36, 61, 0.5);
  border: 1px solid rgba(34, 211, 238, 0.2);
  color: var(--text-light);
  font-size: 14px;
}
.query-btn {
  margin-top: 12px;
  width: 100%;
  background: linear-gradient(135deg, #2563eb, #22d3ee);
  border: none;
  height: 42px;
  font-size: 15px;
  font-weight: 600;
}
.quick-questions {
  margin-top: 16px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}
.quick-label {
  color: var(--text-secondary);
  font-size: 12px;
}
.quick-tag {
  cursor: pointer;
  background: rgba(34, 211, 238, 0.1);
  border-color: rgba(34, 211, 238, 0.25);
  color: var(--text-tertiary);
  transition: all 0.2s;
}
.quick-tag:hover {
  background: rgba(34, 211, 238, 0.2);
  color: var(--cyan-bright);
}
.result-area {
  margin-top: 20px;
  background: rgba(8, 47, 73, 0.3);
  border: 1px solid rgba(34, 211, 238, 0.15);
  border-radius: 12px;
  padding: 16px;
}
.result-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.result-label {
  font-size: 15px;
  font-weight: 600;
  color: var(--cyan-bright);
}
.result-json pre {
  color: var(--text-light);
  font-size: 12px;
  max-height: 400px;
  overflow: auto;
  background: rgba(6, 36, 61, 0.5);
  padding: 12px;
  border-radius: 8px;
}
.result-table-wrapper :deep(.el-table) {
  background: transparent;
}
.result-table-wrapper :deep(.el-table th) {
  background: rgba(34, 211, 238, 0.1);
  color: var(--cyan-bright);
}
.result-table-wrapper :deep(.el-table td) {
  color: var(--text-light);
}
.error-alert {
  margin-top: 16px;
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.3);
}
</style>
