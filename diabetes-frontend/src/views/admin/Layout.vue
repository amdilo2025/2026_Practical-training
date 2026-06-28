<template>
  <div class="admin-layout">
    <!-- 顶部导航 -->
    <div class="admin-header">
      <div class="header-left">
        <el-button text @click="drawerVisible = !drawerVisible" class="menu-btn">
          <el-icon :size="22"><Menu /></el-icon>
        </el-button>
        <span class="header-title">管理后台</span>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="admin-user">
            <div class="user-avatar">{{ adminInitial }}</div>
            <span class="user-name">{{ userStore.userInfo?.name || '管理员' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 抽屉菜单 -->
    <el-drawer v-model="drawerVisible" title="导航菜单" direction="ltr" size="260px" :with-header="false" class="admin-drawer">
      <div class="drawer-logo">
        <el-icon :size="24" color="#22d3ee"><MedicalKit /></el-icon>
        <span>血糖管理助手</span>
      </div>
      <el-menu :default-active="activeMenu" :router="true" @select="drawerVisible = false">
        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>管理首页</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/doctors">
          <el-icon><UserFilled /></el-icon>
          <span>医师管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/articles">
          <el-icon><Reading /></el-icon>
          <span>资讯管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/consultations">
          <el-icon><ChatDotSquare /></el-icon>
          <span>咨询管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/admin/ai-query">
          <el-icon><MagicStick /></el-icon>
          <span>AI智能查询</span>
        </el-menu-item>
      </el-menu>
    </el-drawer>

    <!-- 内容区域 -->
    <div class="admin-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessageBox } from "element-plus";
import { useUserStore } from "../../store/index.js";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const drawerVisible = ref(false);
const activeMenu = computed(() => route.path);

const adminInitial = computed(() => {
  const name = userStore.userInfo?.name || "管理员";
  return name.charAt(0);
});

const handleCommand = async (cmd) => {
  if (cmd === "logout") {
    await ElMessageBox.confirm("确定要退出登录吗？", "提示", { type: "warning" });
    userStore.logout();
    router.push("/login");
  }
};
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: transparent;
  position: relative;
  z-index: 1;
}
.admin-header {
  background: rgba(8, 47, 73, 0.6);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  color: var(--text-light);
  border-bottom: 1px solid rgba(34, 211, 238, 0.18);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-left { display: flex; align-items: center; gap: 12px; }
.menu-btn { color: var(--text-light); }
.menu-btn:hover { color: var(--cyan-bright); }
.header-title { font-size: 17px; font-weight: 700; color: var(--text-bright); font-family: var(--font-display); letter-spacing: 0.06em; text-shadow: 0 0 14px rgba(34, 211, 238, 0.5); }
.admin-user {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--text-light);
  font-size: 14px;
  font-weight: 600;
  padding: 5px 12px 5px 5px;
  border-radius: 24px;
  transition: background 0.2s;
}
.admin-user:hover { background: rgba(34, 211, 238, 0.14); }
.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--grad-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  font-family: var(--font-display);
  box-shadow: 0 0 14px rgba(34, 211, 238, 0.55);
}
.user-name { font-weight: 600; }
.admin-content { padding: 18px 16px 32px; max-width: 800px; margin: 0 auto; }

/* Drawer 样式覆盖 */
.admin-drawer :deep(.el-drawer__body) { padding: 0; }
.drawer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 22px 20px;
  font-size: 17px;
  font-weight: 700;
  color: var(--text-bright);
  font-family: var(--font-display);
  letter-spacing: 0.06em;
  border-bottom: 1px solid rgba(34, 211, 238, 0.15);
  background: rgba(6, 36, 61, 0.5);
}
.admin-drawer .el-menu { border-right: none; }
</style>
