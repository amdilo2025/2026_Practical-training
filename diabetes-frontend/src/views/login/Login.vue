<template>
  <div class="login-page">
    <div class="login-bg"></div>
    <div class="login-container">
      <!-- Logo 区域 -->
      <div class="login-brand">
        <div class="brand-icon">
          <svg viewBox="0 0 48 48" width="60" height="60" fill="none">
            <circle cx="24" cy="24" r="22" fill="#22d3ee" opacity="0.1"/>
            <circle cx="24" cy="24" r="16" stroke="#22d3ee" stroke-width="1.5" opacity="0.4"/>
            <path d="M8 24 H17 L20 15 L24 33 L27 22 L29 24 H40" stroke="#22d3ee" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="24" cy="33" r="2.4" fill="#f59e0b"/>
          </svg>
        </div>
        <h1 class="brand-title">血糖管理助手</h1>
        <p class="brand-subtitle">智能监测 · 科学管理 · 健康生活</p>
      </div>

      <!-- 登录卡片 -->
      <div class="login-card">
        <el-tabs v-model="activeTab" :stretch="true" class="login-tabs">
          <el-tab-pane label="用户登录" name="patient">
            <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
              <el-form-item prop="username">
                <el-input v-model="form.username" placeholder="请输入用户名" size="large">
                  <template #prefix><el-icon><User /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password>
                  <template #prefix><el-icon><Lock /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-button type="primary" size="large" :loading="loading" @click="handleLogin" class="login-btn">
                登 录
              </el-button>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="管理登录" name="admin">
            <el-form ref="adminFormRef" :model="adminForm" :rules="rules" class="login-form">
              <el-form-item prop="username">
                <el-input v-model="adminForm.username" placeholder="管理员账号" size="large">
                  <template #prefix><el-icon><User /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="adminForm.password" type="password" placeholder="请输入密码" size="large" show-password>
                  <template #prefix><el-icon><Lock /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-button type="primary" size="large" :loading="loading" @click="handleAdminLogin" class="login-btn">
                管理员登录
              </el-button>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 注册入口 -->
      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register" class="register-link">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User, Lock } from "@element-plus/icons-vue";
import { userApi } from "../../api/index.js";
import { useUserStore } from "../../store/index.js";

const router = useRouter();
const userStore = useUserStore();

const activeTab = ref("patient");
const loading = ref(false);
const formRef = ref(null);
const adminFormRef = ref(null);

const form = reactive({ username: "zhangsan", password: "123456" });
const adminForm = reactive({ username: "admin", password: "123456" });

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;
  loading.value = true;
  try {
    const res = await userApi.login({ ...form, role: "patient" });
    if (res.code === 200) {
      userStore.setUser(res.data.token, res.data.user, "patient");
      ElMessage.success("登录成功");
      router.push("/patient/dashboard");
    } else {
      ElMessage.error(res.message || "登录失败");
    }
  } catch (e) {
    ElMessage.error("登录失败");
  }
  loading.value = false;
};

const handleAdminLogin = async () => {
  const valid = await adminFormRef.value.validate().catch(() => false);
  if (!valid) return;
  loading.value = true;
  try {
    const res = await userApi.login({ ...adminForm, role: "admin" });
    if (res.code === 200) {
      userStore.setUser(res.data.token, res.data.user, "admin");
      ElMessage.success("登录成功");
      router.push("/admin/dashboard");
    } else {
      ElMessage.error(res.message || "登录失败");
    }
  } catch (e) {
    ElMessage.error("登录失败");
  }
  loading.value = false;
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: transparent;
  padding: 32px 20px;
  position: relative;
  z-index: 1;
}
.login-bg { display: none; }
.login-container {
  width: 100%;
  max-width: 400px;
  position: relative;
  z-index: 2;
  animation: fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) both;
}
.login-brand { text-align: center; margin-bottom: 30px; }
.brand-icon {
  margin-bottom: 16px;
  display: inline-flex;
  filter: drop-shadow(0 0 16px rgba(34, 211, 238, 0.6));
  animation: floatY 6s ease-in-out infinite;
}
.brand-title {
  font-size: 30px;
  font-weight: 700;
  color: var(--text-bright);
  font-family: var(--font-display);
  letter-spacing: 0.1em;
  text-shadow: 0 0 20px rgba(34, 211, 238, 0.5);
}
.brand-subtitle {
  font-size: 13px;
  color: var(--cyan-bright);
  margin-top: 8px;
  letter-spacing: 0.18em;
  opacity: 0.85;
}
.login-card {
  position: relative;
  background: var(--card-strong);
  backdrop-filter: blur(var(--glass-blur));
  -webkit-backdrop-filter: blur(var(--glass-blur));
  border: 1px solid var(--card-border);
  border-radius: 22px;
  padding: 30px 24px 22px;
  box-shadow: var(--shadow-xl), 0 0 40px rgba(34, 211, 238, 0.15);
  overflow: hidden;
}
.login-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 3px;
  background: var(--grad-glow);
}
.login-tabs { margin-bottom: 8px; }
.login-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 600;
  height: 44px;
  line-height: 44px;
}
.login-form { margin-top: 10px; }
.login-form :deep(.el-input__wrapper) {
  border-radius: 12px !important;
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.9) !important;
  box-shadow: 0 0 0 1px rgba(34, 211, 238, 0.25) inset !important;
}
.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--cyan-bright) inset !important;
}
.login-form :deep(.el-input__wrapper.is-focus) {
  background: #fff !important;
  box-shadow: var(--ring), 0 0 0 1px var(--primary) inset !important;
}
.login-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  border-radius: 25px;
  margin-top: 12px;
  font-weight: 700;
  letter-spacing: 0.22em;
  background: var(--grad-primary) !important;
  border: none !important;
  box-shadow: 0 10px 28px rgba(14, 165, 233, 0.45);
}
.login-btn:hover { box-shadow: 0 14px 34px rgba(34, 211, 238, 0.6); transform: translateY(-1px); }
.login-footer {
  text-align: center;
  margin-top: 26px;
  font-size: 14px;
  color: var(--text-muted);
}
.register-link {
  color: var(--cyan-bright);
  text-decoration: none;
  font-weight: 700;
  margin-left: 4px;
  position: relative;
}
.register-link::after {
  content: '';
  position: absolute;
  left: 0; bottom: -2px;
  width: 100%; height: 1.5px;
  background: var(--cyan-bright);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.25s;
}
.register-link:hover::after { transform: scaleX(1); }
</style>
