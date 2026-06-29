<template>
  <div class="patient-dashboard">
    <!-- 用户问候卡片 -->
    <div class="greeting-card anim-fade-up">
      <div class="greeting-content">
        <div class="greeting-text">
          <div class="greeting-tag">{{ timeGreeting }}</div>
          <h3>{{ userInfo?.name || '用户' }}<span class="greeting-suffix">，您好</span></h3>
          <p class="greeting-status" v-if="todaySugar">
            <span class="status-dot" :class="sugarStatus"></span>
            今日血糖: {{ todaySugar }}
          </p>
          <p class="greeting-status" v-else>
            <span class="status-dot"></span>
            今日尚未记录血糖
          </p>
        </div>
        <div class="greeting-avatar">
          <div class="avatar-circle">
            <span class="avatar-text">{{ avatarChar }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-section anim-fade-up" style="animation-delay:0.1s">
      <div class="quick-title">快捷功能</div>
      <div class="quick-grid anim-stagger">
        <div class="quick-item" @click="$router.push('/patient/checkin')">
          <div class="quick-icon blue"><el-icon :size="22"><Check /></el-icon></div>
          <span>今日打卡</span>
        </div>
        <div class="quick-item" @click="$router.push('/patient/risk')">
          <div class="quick-icon orange"><el-icon :size="22"><DataAnalysis /></el-icon></div>
          <span>风险评估</span>
        </div>
        <div class="quick-item" @click="$router.push('/patient/doctor')">
          <div class="quick-icon green"><el-icon :size="22"><ChatDotSquare /></el-icon></div>
          <span>医师咨询</span>
        </div>
        <div class="quick-item" @click="$router.push('/patient/chat')">
          <div class="quick-icon purple"><el-icon :size="22"><MagicStick /></el-icon></div>
          <span>智能助手</span>
        </div>
      </div>
    </div>

    <!-- 推荐阅读 · 自动滚动 -->
    <div class="scroll-section anim-fade-up" style="animation-delay:0.14s">
      <div class="scroll-section-title">推荐阅读</div>
      <div class="scroll-viewport" ref="scrollViewport"
        @mouseenter="pauseScroll"
        @mouseleave="resumeScroll"
        @touchstart="pauseScroll"
        @touchend="resumeScroll">
        <div class="scroll-track" ref="scrollTrack" :class="{ paused: scrollPaused }">
          <div
            v-for="a in doubledArticles"
            :key="a.uid"
            class="scroll-card"
            @click="$router.push(`/patient/article/detail/${a.id}`)"
          >
            <div class="scroll-meta">
              <span class="scroll-tag">{{ a.category }}</span>
              <span>{{ a.createTime?.slice(0,10) || '' }}</span>
            </div>
            <h3 class="scroll-title">{{ a.title }}</h3>
            <p class="scroll-excerpt">{{ a.summary || '点击查看详情' }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据图表 -->
    <div class="section-card anim-fade-up" style="animation-delay:0.18s">
      <div class="section-header">
        <div class="section-title-wrap">
          <el-icon :size="18" color="#5b9bd5"><DataAnalysis /></el-icon>
          <span>资讯分类分布</span>
        </div>
      </div>
      <div ref="pieChartRef" style="height: 200px; width: 100%;"></div>
    </div>

    <!-- 健康资讯 -->
    <div class="section-card anim-fade-up" style="animation-delay:0.2s">
      <div class="section-header">
        <div class="section-title-wrap">
          <el-icon :size="18" color="#5b9bd5"><Reading /></el-icon>
          <span>健康资讯</span>
        </div>
        <el-button text size="small" @click="$router.push('/patient/article')">
          查看更多 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div class="article-list">
        <div v-for="(item, i) in articles.slice(0, 3)" :key="item.id" class="article-item" @click="$router.push(`/patient/article/detail/${item.id}`)">
          <div class="article-index">{{ String(i+1).padStart(2,'0') }}</div>
          <div class="article-info">
            <div class="article-title">{{ item.title }}</div>
            <div class="article-meta">
              <el-tag size="small" round>{{ item.category }}</el-tag>
              <span class="article-views">{{ item.views || 0 }} 次阅读</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最近咨询 -->
    <div class="section-card anim-fade-up" style="animation-delay:0.3s">
      <div class="section-header">
        <div class="section-title-wrap">
          <el-icon :size="18" color="#5b9bd5"><ChatDotSquare /></el-icon>
          <span>最近咨询</span>
        </div>
        <el-button text size="small" @click="$router.push('/patient/doctor')">
          全部 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div v-if="consultations.length > 0" class="consult-list">
        <div v-for="item in consultations.slice(0, 2)" :key="item.id" class="consult-item">
          <div class="consult-header">
            <span class="consult-doctor">{{ item.doctor_name || '医师' }}</span>
            <el-tag :type="item.status === '已回复' ? 'success' : 'warning'" size="small" round>
              {{ item.status }}
            </el-tag>
          </div>
          <div class="consult-content">{{ item.content }}</div>
        </div>
      </div>
      <div v-else class="empty-state">
        <el-icon :size="36" color="#94a3b8"><ChatDotSquare /></el-icon>
        <p>暂无咨询记录</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, onUnmounted } from "vue";
import { useUserStore } from "../../store/index.js";
import { dashboardApi, articleApi } from "../../api/index.js";
import * as echarts from "echarts";

const userStore = useUserStore();
const userInfo = ref({});
const articles = ref([]);
const consultations = ref([]);
const todaySugar = ref("");
const pieChartRef = ref(null);
let pieChart = null;

const scrollViewport = ref(null);
const scrollTrack = ref(null);
const scrollPaused = ref(false);
let scrollAnimId = null;

const avatarChar = computed(() => {
  const name = userInfo.value?.name || "";
  return name.charAt(0) || "U";
});

const timeGreeting = computed(() => {
  const h = new Date().getHours();
  if (h < 6) return "夜深了";
  if (h < 9) return "早上好";
  if (h < 12) return "上午好";
  if (h < 14) return "中午好";
  if (h < 18) return "下午好";
  return "晚上好";
});

const sugarStatus = computed(() => {
  if (!todaySugar.value) return "";
  const val = parseFloat(todaySugar.value);
  if (val > 7) return "high";
  if (val < 4) return "low";
  return "normal";
});

// 推荐阅读 — 双份数据实现无缝循环
const doubledArticles = computed(() => {
  const list = articles.value || [];
  if (list.length < 4) return list;
  const uid = () => Math.random().toString(36).slice(2);
  const copy = list.map(a => ({ ...a, uid: uid() }));
  const copy2 = list.map(a => ({ ...a, uid: uid() }));
  return [...copy, ...copy2];
});

const pauseScroll = () => { scrollPaused.value = true; };
const resumeScroll = () => { scrollPaused.value = false; };

onMounted(async () => {
  userInfo.value = userStore.userInfo || {};
  try {
    const res = await dashboardApi.patient(userStore.userId);
    if (res.code === 200) {
      articles.value = res.data.recommendedArticles || [];
      consultations.value = res.data.recentConsultations || [];
    }
  } catch (_) { /* 忽略 */ }

  await nextTick();
  try {
    const catRes = await articleApi.countByCategory();
    if (catRes.code === 200) {
      pieChart = echarts.init(pieChartRef.value);
      const data = (catRes.data || []).map(d => ({ name: d.category, value: d.count }));
      const colors = ["#5b9bd5","#7cc3f2","#4ba9e6","#2d6fa0","#f59e0b"];
      pieChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}篇 ({d}%)' },
        series: [{
          type: 'pie', radius: ['45%', '70%'], center: ['50%', '50%'],
          label: { show: true, fontSize: 11, formatter: '{b}' },
          data: data,
          itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
          color: colors,
          emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.08)' } }
        }]
      });
    }
  } catch (_) { /* 忽略 */ }
  window.addEventListener('resize', () => pieChart?.resize());
});

onUnmounted(() => {
  window.removeEventListener('resize', () => pieChart?.resize());
  pieChart?.dispose();
  cancelAnimationFrame(scrollAnimId);
});
</script>

<style scoped>
.patient-dashboard { padding: 0 16px 30px; max-width: 500px; margin: 0 auto; }

/* ========== 问候卡片（天蓝渐变→浅色版）========== */
.greeting-card {
  position: relative;
  background: linear-gradient(135deg, #e8f4ff 0%, #dceaff 40%, #e0f0fe 100%);
  border: 1px solid rgba(148, 197, 240, 0.25);
  border-radius: 20px;
  margin: 16px 0;
  overflow: hidden;
  box-shadow: 0 6px 22px rgba(91, 155, 213, 0.1);
}
.greeting-content {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 22px 20px;
}
.greeting-tag {
  display: inline-block;
  font-family: 'Inter', var(--font-sans);
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 2px;
  color: #5b9bd5;
  background: rgba(91, 155, 213, 0.1);
  border-radius: 20px;
  padding: 4px 12px;
  margin-bottom: 8px;
}
.greeting-text h3 {
  font-size: 22px;
  color: #1a3d5e;
  font-weight: 700;
  font-family: var(--font-display);
  letter-spacing: 0.04em;
}
.greeting-suffix { font-weight: 400; opacity: 0.7; }
.greeting-status {
  font-size: 13px;
  color: #6a8fad;
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 7px;
}
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #94c5e0;
  display: inline-block;
}
.status-dot.normal { background: #22c55e; box-shadow: 0 0 6px rgba(34,197,94,0.35); }
.status-dot.high { background: #ef4444; box-shadow: 0 0 6px rgba(239,68,68,0.35); }
.status-dot.low { background: #f59e0b; box-shadow: 0 0 6px rgba(245,158,11,0.35); }
.avatar-circle {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: rgba(91, 155, 213, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(91, 155, 213, 0.2);
}
.avatar-text {
  font-size: 22px;
  font-weight: 700;
  color: #2d6fa0;
  font-family: var(--font-display);
}

/* ========== 快捷操作 ========== */
.quick-section { margin-bottom: 12px; }
.quick-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--gray-800);
  margin-bottom: 10px;
  letter-spacing: 0.04em;
  font-family: var(--font-display);
}
.quick-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}
.quick-item {
  background: rgba(255,255,255,0.6);
  border: 1px solid rgba(148, 197, 240, 0.15);
  border-radius: 16px;
  padding: 16px 6px;
  text-align: center;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  user-select: none;
}
.quick-item:hover { background: rgba(255,255,255,0.85); box-shadow: var(--shadow-md); transform: translateY(-2px); }
.quick-item:active { transform: scale(0.96); }
.quick-item span {
  display: block;
  font-size: 12px;
  color: var(--gray-600);
  margin-top: 8px;
  font-weight: 600;
}
.quick-icon {
  width: 42px;
  height: 42px;
  border-radius: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}
.quick-icon.blue { background: rgba(91,155,213,0.1); color: #5b9bd5; }
.quick-icon.orange { background: rgba(245,158,11,0.1); color: #f59e0b; }
.quick-icon.green { background: rgba(34,197,94,0.1); color: #22c55e; }
.quick-icon.purple { background: rgba(45,111,160,0.08); color: #2d6fa0; }

/* ========== 自动滚动推荐阅读（molan style）========== */
.scroll-section { margin-bottom: 12px; }
.scroll-section-title {
  font-size: 12px;
  font-family: 'Inter', var(--font-sans);
  font-weight: 500;
  letter-spacing: 2px;
  color: #7aaeda;
  padding: 0 4px 10px 4px;
  border-bottom: 1px solid rgba(148, 197, 240, 0.18);
  display: flex;
  align-items: center;
  gap: 8px;
}
.scroll-section-title::after {
  content: '';
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, rgba(148,197,240,0.2), transparent);
}
.scroll-viewport {
  position: relative;
  height: 220px;
  overflow: hidden;
  margin-top: 4px;
  mask-image: linear-gradient(to bottom, transparent 0%, #000 5%, #000 82%, transparent 100%);
  -webkit-mask-image: linear-gradient(to bottom, transparent 0%, #000 5%, #000 82%, transparent 100%);
}
.scroll-track {
  animation: dashScroll 28s linear infinite;
}
.scroll-track.paused { animation-play-state: paused; }
@keyframes dashScroll {
  0%   { transform: translateY(0); }
  100% { transform: translateY(-50%); }
}
.scroll-card {
  padding: 14px 16px;
  margin: 6px 0;
  border-radius: 14px;
  background: rgba(255,255,255,0.55);
  border: 1px solid rgba(148, 197, 240, 0.12);
  cursor: pointer;
  user-select: none;
  transition: all 0.22s cubic-bezier(0.22, 1, 0.36, 1);
}
.scroll-card:hover { background: rgba(255,255,255,0.78); border-color: rgba(91,155,213,0.2); }
.scroll-card:active { transform: scale(0.98); }
.scroll-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
  font-family: 'Inter', var(--font-sans);
  font-size: 10px;
  color: #8aabc4;
}
.scroll-tag {
  padding: 2px 8px;
  border-radius: 10px;
  background: rgba(91,155,213,0.1);
  color: #5b9bd5;
  font-size: 9px;
  font-weight: 500;
}
.scroll-title {
  font-size: 13px;
  font-weight: 600;
  color: #1a3d5e;
  line-height: 1.5;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.scroll-excerpt {
  font-size: 11px;
  line-height: 1.5;
  color: #8aabc4;
  font-weight: 300;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ========== 通用卡片 ========== */
.section-card {
  background: rgba(255,255,255,0.55);
  border: 1px solid rgba(148, 197, 240, 0.15);
  border-radius: 18px;
  padding: 18px 16px;
  margin-bottom: 12px;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow: 0 2px 10px rgba(91,155,213,0.06);
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}
.section-title-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 700;
  color: var(--gray-800);
}
.section-header :deep(.el-button) {
  font-size: 13px;
  color: var(--gray-500);
}

/* 资讯列表 */
.article-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid rgba(148,197,240,0.15);
  cursor: pointer;
}
.article-item:last-child { border-bottom: none; }
.article-index {
  font-size: 15px;
  font-weight: 700;
  color: #b8ddfb;
  min-width: 24px;
  line-height: 1.4;
  font-family: var(--font-num);
}
.article-info { flex: 1; min-width: 0; }
.article-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--gray-800);
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.article-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}
.article-views {
  font-size: 11px;
  color: var(--gray-400);
}

/* 咨询列表 */
.consult-item { padding: 12px 0; border-bottom: 1px solid rgba(148,197,240,0.15); }
.consult-item:last-child { border-bottom: none; }
.consult-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.consult-doctor {
  font-size: 14px;
  font-weight: 700;
  color: var(--primary-700);
}
.consult-content {
  font-size: 13px;
  color: var(--gray-500);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}
.empty-state {
  text-align: center;
  padding: 24px 0;
  color: var(--gray-400);
}
.empty-state p { margin-top: 8px; font-size: 13px; }
</style>
