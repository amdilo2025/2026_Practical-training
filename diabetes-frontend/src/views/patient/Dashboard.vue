<template>
  <div class="patient-dashboard">
    <!-- 用户问候卡片 -->
    <div class="greeting-card anim-fade-up">
      <div class="greeting-bg"></div>
      <div class="greeting-content">
        <div class="greeting-text">
          <div class="greeting-time">{{ timeGreeting }}</div>
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
          <div class="quick-icon blue">
            <el-icon :size="24"><Check /></el-icon>
          </div>
          <span>今日打卡</span>
        </div>
        <div class="quick-item" @click="$router.push('/patient/risk')">
          <div class="quick-icon orange">
            <el-icon :size="24"><DataAnalysis /></el-icon>
          </div>
          <span>风险评估</span>
        </div>
        <div class="quick-item" @click="$router.push('/patient/doctor')">
          <div class="quick-icon green">
            <el-icon :size="24"><ChatDotSquare /></el-icon>
          </div>
          <span>医师咨询</span>
        </div>
        <div class="quick-item" @click="$router.push('/patient/chat')">
          <div class="quick-icon purple">
            <el-icon :size="24"><MagicStick /></el-icon>
          </div>
          <span>智能助手</span>
        </div>
      </div>
    </div>

    <!-- 数据图表 -->
    <div class="section-card anim-fade-up" style="animation-delay:0.18s">
      <div class="section-header">
        <div class="section-title-wrap">
          <el-icon :size="18" color="#22d3ee"><DataAnalysis /></el-icon>
          <span>资讯分类分布</span>
        </div>
      </div>
      <div ref="pieChartRef" style="height: 200px; width: 100%;"></div>
    </div>

    <!-- 健康资讯 -->
    <div class="section-card anim-fade-up" style="animation-delay:0.2s">
      <div class="section-header">
        <div class="section-title-wrap">
          <el-icon :size="18" color="#22d3ee"><Reading /></el-icon>
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
          <el-icon :size="18" color="#06b6d4"><ChatDotSquare /></el-icon>
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

onMounted(async () => {
  userInfo.value = userStore.userInfo || {};
  try {
    const res = await dashboardApi.patient(userStore.userId);
    if (res.code === 200) {
      articles.value = res.data.recommendedArticles || [];
      consultations.value = res.data.recentConsultations || [];
    }
  } catch (_) { /* 忽略 */ }

  // 初始化饼图
  await nextTick();
  try {
    const catRes = await articleApi.countByCategory();
    if (catRes.code === 200) {
      pieChart = echarts.init(pieChartRef.value);
      const data = (catRes.data || []).map(d => ({ name: d.category, value: d.count }));
      const colors = ["#22d3ee","#0ea5e9","#06b6d4","#0369a1","#f59e0b"];
      pieChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c}篇 ({d}%)' },
        series: [{
          type: 'pie', radius: ['45%', '70%'], center: ['50%', '50%'],
          label: { show: true, fontSize: 11, formatter: '{b}' },
          data: data,
          itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
          color: colors,
          emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.15)' } }
        }]
      });
    }
  } catch (_) { /* 忽略 */ }
  window.addEventListener('resize', () => pieChart?.resize());
});

onUnmounted(() => {
  window.removeEventListener('resize', () => pieChart?.resize());
  pieChart?.dispose();
});
</script>

<style scoped>
.patient-dashboard { padding: 0 16px 20px; max-width: 500px; margin: 0 auto; }

/* 问候卡片 */
.greeting-card {
  position: relative;
  background: var(--grad-primary);
  border-radius: 20px;
  margin: 16px 0;
  overflow: hidden;
  box-shadow: 0 14px 30px rgba(13, 148, 136, 0.26);
}
.greeting-card::after {
  content: '';
  position: absolute;
  bottom: -44px;
  left: -22px;
  width: 160px;
  height: 160px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(194, 113, 12, 0.34), transparent 70%);
}
.greeting-bg {
  position: absolute;
  top: -30px;
  right: -30px;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}
.greeting-bg::after {
  content: '';
  position: absolute;
  top: 22px;
  right: 22px;
  width: 110px;
  height: 110px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
}
.greeting-content {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 22px 20px;
}
.greeting-time {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.75);
  margin-bottom: 4px;
  letter-spacing: 0.05em;
}
.greeting-text h3 {
  font-size: 22px;
  color: #fff;
  font-weight: 700;
  font-family: var(--font-display);
  letter-spacing: 0.04em;
}
.greeting-suffix { font-weight: 400; opacity: 0.82; }
.greeting-status {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.88);
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 7px;
}
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  display: inline-block;
}
.status-dot.normal { background: #6ee7b7; }
.status-dot.high { background: #fca5a5; }
.status-dot.low { background: #fcd34d; }
.avatar-circle {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(255, 255, 255, 0.4);
}
.avatar-text {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  font-family: var(--font-display);
}

/* 快捷操作 */
.quick-section { margin-bottom: 16px; }
.quick-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--gray-800);
  margin-bottom: 12px;
  letter-spacing: 0.03em;
}
.quick-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}
.quick-item {
  background: #fff;
  border: 1px solid var(--gray-100);
  border-radius: 16px;
  padding: 16px 6px;
  text-align: center;
  box-shadow: var(--shadow-xs);
  cursor: pointer;
  transition: transform 0.18s cubic-bezier(0.16, 1, 0.3, 1), box-shadow 0.2s;
}
.quick-item:hover { box-shadow: var(--shadow-md); transform: translateY(-2px); }
.quick-item:active { transform: scale(0.96); }
.quick-item span {
  display: block;
  font-size: 12px;
  color: var(--gray-600);
  margin-top: 8px;
  font-weight: 600;
}
.quick-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}
.quick-icon.blue { background: var(--primary-50); color: var(--primary); }
.quick-icon.orange { background: var(--accent-bg); color: var(--accent); }
.quick-icon.green { background: var(--success-bg); color: var(--success); }
.quick-icon.purple { background: #e7f6f1; color: var(--primary-700); }

/* 通用卡片 */
.section-card {
  background: #fff;
  border: 1px solid var(--gray-100);
  border-radius: 18px;
  padding: 18px 16px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-sm);
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
  font-size: 16px;
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
  border-bottom: 1px dashed var(--gray-100);
  cursor: pointer;
}
.article-item:last-child { border-bottom: none; }
.article-index {
  font-size: 15px;
  font-weight: 700;
  color: var(--gray-300);
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
.consult-list { }
.consult-item { padding: 12px 0; border-bottom: 1px dashed var(--gray-100); }
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
