<template>
  <div class="risk-predict">
    <div class="page-header"><h3>糖尿病风险预测</h3></div>

    <!-- 风险录入表单 -->
    <el-card shadow="never" class="form-card">
      <template #header><span style="font-weight: bold">输入身体指标</span></template>
      <el-form ref="formRef" :model="form" label-width="100px" size="small">
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="form.age" :min="1" :max="150" style="width:100%" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="身高(cm)" prop="height"><el-input-number v-model="form.height" :min="50" :max="250" :step="0.5" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="体重(kg)" prop="weight"><el-input-number v-model="form.weight" :min="20" :max="300" :step="0.5" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="空腹血糖" prop="fastingBloodSugar">
          <el-input-number v-model="form.fastingBloodSugar" :min="2" :max="30" :step="0.1" :precision="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="餐后血糖" prop="postprandialBloodSugar">
          <el-input-number v-model="form.postprandialBloodSugar" :min="2" :max="30" :step="0.1" :precision="1" style="width:100%" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="收缩压" prop="bloodPressureSystolic"><el-input-number v-model="form.bloodPressureSystolic" :min="60" :max="250" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="舒张压" prop="bloodPressureDiastolic"><el-input-number v-model="form.bloodPressureDiastolic" :min="40" :max="150" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="家族史">
          <el-radio-group v-model="form.familyHistory">
            <el-radio :value="0">无</el-radio>
            <el-radio :value="1">有</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="handlePredict" class="predict-btn">
          <el-icon><DataAnalysis /></el-icon> AI风险预测
        </el-button>
      </el-form>
    </el-card>

    <!-- 预测结果 -->
    <el-card v-if="result" shadow="never" class="result-card" :class="result.riskLevel">
      <div class="result-header">
        <div class="risk-badge">{{ result.riskLevel }}</div>
        <div class="risk-score">风险评分: {{ result.riskScore }}分</div>
      </div>
      <div v-if="result.bmi" class="bmi-info">BMI: {{ result.bmi }}</div>
      <div class="advice">{{ result.advice }}</div>
    </el-card>

    <!-- 历史记录 -->
    <div class="history-section">
      <div class="section-title">历史预测记录</div>
      <div v-for="item in history" :key="item.id" class="history-item" @click="showDetail(item)">
        <div class="history-left">
          <div class="history-level" :class="item.riskLevel">{{ item.riskLevel }}</div>
          <div class="history-date">{{ formatTime(item.createdTime) }}</div>
        </div>
        <div class="history-right">评分: {{ item.riskScore }}</div>
      </div>
      <div v-if="history.length === 0" class="empty-tip">暂无记录</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { riskApi } from "../../api/index.js";
import { useUserStore } from "../../store/index.js";

const userStore = useUserStore();
const loading = ref(false);
const result = ref(null);
const history = ref([]);

const form = ref({
  age: 45, height: 170, weight: 70, fastingBloodSugar: 6.1,
  postprandialBloodSugar: 8.5, bloodPressureSystolic: 130,
  bloodPressureDiastolic: 85, familyHistory: 0,
});

const formatTime = (t) => t ? t.slice(0, 10) : "";

const handlePredict = async () => {
  loading.value = true;
  try {
    const res = await riskApi.predict({ userId: userStore.userId, ...form.value });
    if (res.code === 200) {
      result.value = res.data;
      ElMessage.success("预测完成");
      loadHistory();
    }
  } catch (_) { /* eslint-disable-line no-empty */ }
  loading.value = false;
};

const loadHistory = async () => {
  const res = await riskApi.listByUser(userStore.userId);
  if (res.code === 200) history.value = res.data || [];
};

onMounted(loadHistory);
</script>

<style scoped>
.risk-predict { padding: 16px; max-width: 500px; margin: 0 auto; }
.page-header h3 { font-size: 22px; margin-bottom: 16px; font-family: var(--font-display); color: var(--gray-900); letter-spacing: 0.04em; }
.form-card { margin-bottom: 16px; }
.predict-btn {
  width: 100%;
  margin-top: 10px;
  border-radius: 24px;
  height: 46px;
  background: var(--grad-primary) !important;
  border: none !important;
  box-shadow: 0 8px 18px rgba(13, 148, 136, 0.24);
}
.result-card { margin-bottom: 16px; padding: 18px; text-align: center; position: relative; overflow: hidden; }
.result-card.高风险 { border-left: 4px solid var(--danger); background: linear-gradient(135deg, var(--danger-bg), #fff); }
.result-card.中风险 { border-left: 4px solid var(--warning); background: linear-gradient(135deg, var(--warning-bg), #fff); }
.result-card.低风险 { border-left: 4px solid var(--success); background: linear-gradient(135deg, var(--success-bg), #fff); }
.result-header { margin-bottom: 12px; }
.risk-badge { font-size: 26px; font-weight: 700; margin-bottom: 6px; font-family: var(--font-display); letter-spacing: 0.06em; }
.高风险 .risk-badge { color: var(--danger); }
.中风险 .risk-badge { color: var(--warning); }
.低风险 .risk-badge { color: var(--success); }
.risk-score { font-size: 14px; color: var(--gray-700); font-family: var(--font-num); }
.bmi-info { font-size: 14px; color: var(--gray-800); margin-bottom: 10px; font-weight: 600; }
.advice { font-size: 14px; color: #0f172a; line-height: 1.65; text-align: left; padding: 12px 14px; background: rgba(255, 255, 255, 0.65); border: 1px solid rgba(34, 211, 238, 0.18); border-radius: 10px; }
.history-section { background: #fff; border: 1px solid var(--gray-100); border-radius: 16px; padding: 16px; box-shadow: var(--shadow-sm); }
.section-title { font-size: 15px; font-weight: 700; margin-bottom: 12px; color: var(--gray-700); letter-spacing: 0.03em; }
.history-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 0; border-bottom: 1px dashed var(--gray-100); cursor: pointer; }
.history-item:last-child { border-bottom: none; }
.history-left { display: flex; align-items: center; gap: 10px; }
.history-level { padding: 3px 10px; border-radius: 8px; font-size: 12px; color: #fff; font-weight: 700; letter-spacing: 0.04em; }
.history-level.高风险 { background: var(--danger); }
.history-level.中风险 { background: var(--warning); }
.history-level.低风险 { background: var(--success); }
.history-date { font-size: 13px; color: var(--gray-400); font-family: var(--font-num); }
.history-right { font-size: 13px; color: var(--gray-600); font-family: var(--font-num); }
.empty-tip { text-align: center; padding: 24px; color: var(--gray-400); }
</style>
