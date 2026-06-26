<template>
  <div class="admin-dashboard">
    <h3 class="page-title">管理首页</h3>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-val">{{ stats.totalUsers }}</div>
        <div class="stat-label">用户总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-val">{{ stats.pendingConsultations }}</div>
        <div class="stat-label">待回复咨询</div>
      </div>
      <div class="stat-card">
        <div class="stat-val">{{ stats.totalArticles }}</div>
        <div class="stat-label">资讯总数</div>
      </div>
    </div>

    <!-- 快速入口 -->
    <el-card shadow="never" class="quick-card">
      <template #header><span style="font-weight: bold">快速入口</span></template>
      <div class="quick-grid">
        <div class="quick-item" @click="$router.push('/admin/users')">
          <el-icon :size="28" color="#0d9488"><User /></el-icon><span>用户管理</span>
        </div>
        <div class="quick-item" @click="$router.push('/admin/doctors')">
          <el-icon :size="28" color="#2f8a5f"><UserFilled /></el-icon><span>医师管理</span>
        </div>
        <div class="quick-item" @click="$router.push('/admin/articles')">
          <el-icon :size="28" color="#c2710c"><Reading /></el-icon><span>资讯管理</span>
        </div>
        <div class="quick-item" @click="$router.push('/admin/consultations')">
          <el-icon :size="28" color="#c0392b"><ChatDotSquare /></el-icon><span>咨询管理</span>
        </div>
      </div>
    </el-card>

    <!-- 用户趋势 -->
    <el-card shadow="never" class="chart-card">
      <template #header><span style="font-weight: bold">近30天用户增长</span></template>
      <div ref="chartRef" style="height: 260px"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from "vue";
import * as echarts from "echarts";
import { adminApi } from "../../api/index.js";

const stats = ref({ totalUsers: 0, pendingConsultations: 0, totalArticles: 0 });
const chartRef = ref(null);
let chart = null;

onMounted(async () => {
  const res = await adminApi.dashboard();
  if (res.code === 200) {
    stats.value = res.data;
    await nextTick();
    chart = echarts.init(chartRef.value);
    const trend = res.data.userTrend || [];
    chart.setOption({
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: trend.map(t => t.date) },
      yAxis: { type: "value" },
      series: [{ type: "line", data: trend.map(t => t.count), smooth: true, lineStyle: { color: "#0d9488", width: 3 }, itemStyle: { color: "#0d9488" }, areaStyle: { color: { type: "linear", x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: "rgba(13,148,136,0.35)" }, { offset: 1, color: "rgba(13,148,136,0.02)" }] } } }],
      grid: { left: "8%", right: "8%", bottom: "15%" },
    });
  }
});

onUnmounted(() => { chart?.dispose(); });
</script>

<style scoped>
.admin-dashboard { max-width: 600px; margin: 0 auto; }
.page-title { font-size: 22px; margin-bottom: 18px; font-family: var(--font-display); color: var(--gray-900); letter-spacing: 0.04em; }
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin-bottom: 16px; }
.stat-card {
  background: #fff;
  border: 1px solid var(--gray-100);
  border-radius: 16px;
  padding: 20px 12px;
  text-align: center;
  box-shadow: var(--shadow-sm);
  position: relative;
  overflow: hidden;
}
.stat-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 3px;
  background: var(--grad-primary);
}
.stat-val { font-size: 30px; font-weight: 700; color: var(--primary); font-family: var(--font-num); line-height: 1.1; }
.stat-label { font-size: 13px; color: var(--gray-500); margin-top: 5px; letter-spacing: 0.03em; }
.quick-card { margin-bottom: 16px; }
.quick-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 9px;
  padding: 18px 12px;
  border-radius: 16px;
  background: var(--cream);
  border: 1px solid var(--gray-100);
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}
.quick-item:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); background: #fff; }
.quick-item span { font-size: 13px; color: var(--gray-700); font-weight: 600; }
.chart-card { margin-bottom: 16px; }
</style>
