<template>
  <div class="patient-layout">
    <!-- 顶部状态栏 -->
    <div class="top-bar">
      <div class="top-bar-left">
        <span class="app-name">血糖管理助手</span>
      </div>
      <div class="top-bar-right">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="badge">
          <el-icon :size="20" color="#5b9bd5"><Bell /></el-icon>
        </el-badge>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-area">
      <router-view />
    </div>

    <!-- 底部导航栏 -->
    <div class="bottom-nav">
      <div
        v-for="item in navItems"
        :key="item.path"
        class="nav-item"
        :class="{ active: activeMenu === item.path }"
        @click="goTo(item.path)"
      >
        <div class="nav-icon">
          <el-icon :size="22">
            <component :is="item.icon" />
          </el-icon>
        </div>
        <span class="nav-label">{{ item.label }}</span>
        <div v-if="activeMenu === item.path" class="nav-indicator"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();
const unreadCount = ref(0);

const activeMenu = computed(() => {
  const path = route.path;
  if (path.startsWith("/patient/personal")) return "/patient/personal";
  if (path.startsWith("/patient/doctor")) return "/patient/doctor";
  if (path.startsWith("/patient/checkin")) return "/patient/checkin";
  if (path.startsWith("/patient/chat")) return "/patient/chat";
  if (path.startsWith("/patient/article")) return "/patient/article";
  return "/patient/dashboard";
});

const navItems = [
  { path: "/patient/dashboard", label: "首页", icon: "Odometer" },
  { path: "/patient/doctor", label: "咨询", icon: "ChatDotSquare" },
  { path: "/patient/checkin", label: "打卡", icon: "Check" },
  { path: "/patient/chat", label: "AI助手", icon: "MagicStick" },
  { path: "/patient/personal", label: "我的", icon: "User" },
];

const goTo = (path) => {
  if (route.path !== path) router.push(path);
};
</script>

<style scoped>
.patient-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: transparent;
  position: relative;
  z-index: 1;
}
.top-bar {
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  height: 54px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 18px;
  border-bottom: 1px solid rgba(148, 197, 240, 0.2);
  flex-shrink: 0;
  position: sticky;
  top: 0;
  z-index: 100;
}
.app-name {
  font-size: 17px;
  font-weight: 700;
  color: var(--primary-700);
  letter-spacing: 0.08em;
  display: inline-flex;
  align-items: center;
}
.app-name::before {
  content: '';
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: var(--primary);
  margin-right: 9px;
  box-shadow: 0 0 0 3px rgba(91, 155, 213, 0.18), 0 0 10px rgba(91, 155, 213, 0.5);
  animation: breathe 2.4s ease-in-out infinite;
}
.badge :deep(.el-badge__content) {
  border: none;
  background: var(--danger);
}
.content-area {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  padding-bottom: 72px;
}
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 62px;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  justify-content: space-around;
  align-items: center;
  border-top: 1px solid rgba(148, 197, 240, 0.2);
  z-index: 1000;
  box-shadow: 0 -8px 28px rgba(91, 155, 213, 0.1);
  padding-bottom: env(safe-area-inset-bottom);
}
.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  height: 100%;
  cursor: pointer;
  color: var(--text-muted);
  transition: color 0.2s;
  position: relative;
  -webkit-tap-highlight-color: transparent;
}
.nav-item.active { color: var(--primary); }
.nav-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 30px;
  width: 46px;
  border-radius: 14px;
  transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
}
.nav-item.active .nav-icon {
  transform: translateY(-2px);
  background: rgba(91, 155, 213, 0.1);
  box-shadow: 0 0 16px rgba(91, 155, 213, 0.25);
}
.nav-label {
  font-size: 10px;
  margin-top: 3px;
  font-weight: 600;
  letter-spacing: 0.04em;
}
.nav-indicator {
  position: absolute;
  top: 0;
  width: 22px;
  height: 3px;
  background: var(--grad-glow);
  border-radius: 0 0 3px 3px;
  box-shadow: 0 0 8px rgba(91, 155, 213, 0.5);
}
</style>
