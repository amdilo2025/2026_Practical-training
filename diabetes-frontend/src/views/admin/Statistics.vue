<template>
  <div class="statistics">
    <h3 class="page-title">数据统计</h3>

    <!-- 概览卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-val">{{ stats.totalUsers }}</div>
        <div class="stat-label">注册用户</div>
      </div>
      <div class="stat-card">
        <div class="stat-val">{{ stats.pendingConsultations || 0 }}</div>
        <div class="stat-label">待回复咨询</div>
      </div>
      <div class="stat-card">
        <div class="stat-val">{{ stats.repliedConsultations || 0 }}</div>
        <div class="stat-label">已回复咨询</div>
      </div>
    </div>

    <!-- 用户增长趋势 -->
    <el-card shadow="never" class="chart-card">
      <template #header><span style="font-weight: bold">用户增长趋势</span></template>
      <div ref="trendChartRef" style="height: 260px"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from "vue";
import * as echarts from "echarts";
import { adminApi } from "../../api/index.js";

const stats = ref({});
const trendChartRef = ref(null);
let trendChart = null;

onMounted(async () => {
  const res = await adminApi.statistics();
  if (res.code === 200) {
    stats.value = res.data;
    await nextTick();
    trendChart = echarts.init(trendChartRef.value);
    const trend = res.data.userTrend || [];
    trendChart.setOption({
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: trend.map(t => t.date) },
      yAxis: { type: "value" },
      series: [{ type: "line", data: trend.map(t => t.count), smooth: true, lineStyle: { color: "#0d9488", width: 3 }, itemStyle: { color: "#0d9488" }, areaStyle: { color: { type: "linear", x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: "rgba(13,148,136,0.35)" }, { offset: 1, color: "rgba(13,148,136,0.02)" }] } } }],
      grid: { left: "8%", right: "8%", bottom: "15%" },
    });
  }
});

onUnmounted(() => { trendChart?.dispose(); });
</script>

<style scoped>
.statistics { max-width: 600px; margin: 0 auto; }
.page-title { font-size: 22px; margin-bottom: 18px; font-family: var(--font-display); color: var(--gray-900); letter-spacing: 0.04em; }
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin-bottom: 16px; }
.stat-card { background: #fff; border: 1px solid var(--gray-100); border-radius: 16px; padding: 20px 12px; text-align: center; box-shadow: var(--shadow-sm); position: relative; overflow: hidden; }
.stat-card::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 3px; background: var(--grad-primary); }
.stat-val { font-size: 30px; font-weight: 700; color: var(--primary); font-family: var(--font-num); line-height: 1.1; }
.stat-label { font-size: 13px; color: var(--gray-500); margin-top: 5px; letter-spacing: 0.03em; }
.chart-card { margin-bottom: 16px; }
</style>
