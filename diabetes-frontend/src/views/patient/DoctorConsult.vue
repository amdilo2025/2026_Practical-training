<template>
  <div class="consult-page page-wrap">
    <div class="page-header anim-fade-in">
      <h3>医师咨询</h3>
    </div>

    <!-- 选择医师 -->
    <div class="info-card-title" style="padding: 0 0 10px">
      <el-icon :size="16" color="#0d9488"><UserFilled /></el-icon>
      选择医师
    </div>
    <div class="doctor-list anim-stagger">
      <div
        v-for="doc in doctors"
        :key="doc.id"
        class="doctor-card"
        :class="{ active: selectedDoctor?.id === doc.id }"
        @click="selectedDoctor = doc"
      >
        <span v-if="doc.isAi" class="recommend-badge">优先推荐</span>
        <div class="doc-avatar" :style="{ background: docColors(doc.id) }">
          <span>{{ doc.name?.charAt(0) }}</span>
          <img v-if="doc.avatar" :src="doc.avatar" :alt="doc.name" class="doc-avatar-img" @error="(e) => e.target.style.display = 'none'" />
        </div>
        <div class="doc-info">
          <div class="doc-name">{{ doc.name }}</div>
          <div class="doc-meta">{{ doc.department }} · {{ doc.title }}</div>
          <div class="doc-tags">
            <el-tag size="small" round>糖尿病</el-tag>
            <el-tag v-if="doc.goodAt" size="small" round type="info">{{ doc.goodAt?.split('、')[0] }}</el-tag>
          </div>
        </div>
        <div class="doc-status">
          <span class="status-dot" :class="{ 'ai-dot': doc.isAi }"></span>
          <span class="status-text">{{ doc.isAi ? 'AI医生' : '在线' }}</span>
        </div>
      </div>
    </div>

    <!-- 咨询输入 -->
    <div v-if="selectedDoctor" class="consult-card anim-fade-up">
      <div class="consult-card-header">
        <el-icon :size="16" color="#0d9488"><Edit /></el-icon>
        向 {{ selectedDoctor.name }} 咨询
      </div>
      <el-input v-model="consultContent" type="textarea" :rows="4" placeholder="请详细描述您的问题..." maxlength="500" show-word-limit />
      <el-button type="primary" :loading="loading" @click="handleSubmit" class="submit-btn anim-fade-up">
        <el-icon><Promotion /></el-icon> 提交咨询
      </el-button>
    </div>

    <!-- 咨询历史 -->
    <div class="history-section info-card" style="margin-top:16px">
      <div class="info-card-title">
        <el-icon :size="16" color="#0d9488"><ChatDotSquare /></el-icon>
        咨询历史
      </div>
      <div v-if="history.length > 0" class="history-list">
        <div v-for="item in history" :key="item.id" class="history-item" @click="showDetail(item)">
          <div class="h-item-header">
            <div class="h-doctor">
              <div class="h-avatar" :style="{ background: docColors(item.doctor_id) }">
                <span>{{ item.doctor_name?.charAt(0) || '医' }}</span>
                <img v-if="item.doctor_avatar" :src="item.doctor_avatar" :alt="item.doctor_name" class="h-avatar-img" @error="(e) => e.target.style.display = 'none'" />
              </div>
              <div>
                <div class="h-name">{{ item.doctor_name || '医师' }}</div>
                <div class="h-time">{{ formatTime(item.created_time) }}</div>
              </div>
            </div>
            <el-tag :type="item.status === '已回复' ? 'success' : 'warning'" size="small" round>
              {{ item.status }}
            </el-tag>
          </div>
          <div class="h-content">{{ item.content }}</div>
          <div v-if="item.reply" class="h-reply">
            <span class="reply-label">医师回复：</span>
            {{ item.reply }}
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <el-icon :size="36" color="#b3a487"><ChatDotSquare /></el-icon>
        <p>暂无咨询记录</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { doctorApi, consultationApi } from "../../api/index.js";
import { useUserStore } from "../../store/index.js";

const userStore = useUserStore();
const doctors = ref([]);
const selectedDoctor = ref(null);
const consultContent = ref("");
const history = ref([]);
const loading = ref(false);

const docColors = (id) => {
  const colors = ["#0d9488", "#c2710c", "#2f8a5f", "#0c6259", "#b7791f", "#115e59"];
  return colors[(id || 1) % colors.length];
};
const formatTime = (t) => t ? t.slice(0, 16).replace("T", " ") : "";

const loadData = async () => {
  const dRes = await doctorApi.list();
  if (dRes.code === 200) doctors.value = dRes.data || [];
  const hRes = await consultationApi.listByUser(userStore.userId);
  if (hRes.code === 200) history.value = hRes.data || [];
};

const handleSubmit = async () => {
  if (!consultContent.value.trim()) return ElMessage.warning("请输入问题");
  if (!selectedDoctor.value) return ElMessage.warning("请选择医师");
  loading.value = true;
  try {
    await consultationApi.add({ userId: userStore.userId, doctorId: selectedDoctor.value.id, content: consultContent.value });
    ElMessage.success("咨询已提交，请等待医师回复");
    consultContent.value = "";
    const hRes = await consultationApi.listByUser(userStore.userId);
    if (hRes.code === 200) history.value = hRes.data || [];
  } catch (_) { /* 忽略 */ }
  loading.value = false;
};

onMounted(loadData);
</script>

<style scoped>
.consult-page { padding-bottom: 32px; }

/* 医师卡片 */
.doctor-card {
  display: flex;
  gap: 14px;
  background: #fff;
  border: 1.5px solid var(--gray-100);
  border-radius: 16px;
  padding: 14px;
  margin-bottom: 10px;
  box-shadow: var(--shadow-xs);
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}
.doctor-card:hover { box-shadow: var(--shadow-md); transform: translateY(-1px); }
.recommend-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: linear-gradient(135deg, #f59e0b, #ea580c);
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(234, 88, 12, 0.35);
  letter-spacing: 0.04em;
  z-index: 2;
}
.status-dot.ai-dot { background: #f59e0b; box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.2); }
.doctor-card.active {
  border-color: var(--primary);
  box-shadow: var(--ring), var(--shadow-sm);
}
.doc-avatar {
  width: 50px;
  height: 50px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}
.doc-avatar span { font-size: 20px; font-weight: 700; color: #fff; font-family: var(--font-display); }
.doc-avatar-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.doc-info { flex: 1; min-width: 0; }
.doc-name { font-size: 15px; font-weight: 700; color: var(--gray-800); }
.doc-meta { font-size: 12px; color: var(--gray-500); margin: 4px 0 7px; }
.doc-tags { display: flex; gap: 5px; flex-wrap: wrap; }
.doc-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding-top: 4px;
}
.status-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--success); box-shadow: 0 0 0 3px rgba(47, 138, 95, 0.18); }
.status-text { font-size: 11px; color: var(--success); font-weight: 600; }

/* 咨询输入 */
.consult-card {
  background: #fff;
  border: 1px solid var(--gray-100);
  border-radius: 18px;
  padding: 18px;
  margin-top: 16px;
  box-shadow: var(--shadow-sm);
}
.consult-card-header {
  font-size: 14px;
  font-weight: 700;
  color: var(--gray-700);
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  letter-spacing: 0.03em;
}
.submit-btn {
  width: 100%;
  margin-top: 14px;
  height: 46px;
  background: var(--grad-primary) !important;
  border: none !important;
  box-shadow: 0 8px 18px rgba(13, 148, 136, 0.24);
}

/* 咨询历史 */
.history-list { }
.history-item {
  padding: 14px 0;
  border-bottom: 1px dashed var(--gray-100);
  cursor: pointer;
}
.history-item:last-child { border-bottom: none; }
.h-item-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8px; }
.h-doctor { display: flex; align-items: center; gap: 10px; }
.h-avatar {
  width: 38px; height: 38px;
  border-radius: 11px;
  display: flex; align-items: center; justify-content: center;
  position: relative;
  overflow: hidden;
}
.h-avatar span { font-size: 15px; font-weight: 700; color: #fff; font-family: var(--font-display); }
.h-avatar-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.h-name { font-size: 14px; font-weight: 700; color: var(--gray-700); }
.h-time { font-size: 12px; color: var(--gray-400); margin-top: 2px; font-family: var(--font-num); }
.h-content { font-size: 13px; color: var(--gray-500); line-height: 1.5; margin-bottom: 8px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.h-reply {
  font-size: 13px;
  color: var(--primary-700);
  padding: 11px 12px;
  background: var(--primary-50);
  border-left: 3px solid var(--primary);
  border-radius: 0 10px 10px 0;
  line-height: 1.55;
}
.reply-label { font-weight: 700; }
</style>
