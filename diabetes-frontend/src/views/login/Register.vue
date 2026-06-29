<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-header">
        <div class="back-link" @click="$router.push('/login')">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回登录</span>
        </div>
        <div class="register-brand">
          <h2>创建账号</h2>
          <p>加入血糖管理，开启健康生活</p>
        </div>
      </div>
      <div class="register-card">
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" placeholder="请输入姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-select v-model="form.gender" placeholder="选择">
                  <el-option label="男" value="男" />
                  <el-option label="女" value="女" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
          </el-form-item>
          <el-form-item label="年龄" prop="age">
            <el-input-number v-model="form.age" :min="1" :max="150" style="width:100%" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="至少6位密码" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" show-password />
          </el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleRegister" class="register-btn">
            注 册
          </el-button>
        </el-form>
      </div>
      <div class="login-hint">
        <span>已有账号？</span>
        <router-link to="/login">去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { userApi } from "../../api/index.js";

const router = useRouter();
const loading = ref(false);
const formRef = ref(null);

const form = reactive({
  username: "", name: "", phone: "", age: 30, gender: "男",
  password: "", confirmPassword: "",
});

const validatePass = (_rule, value, callback) => {
  if (value !== form.password) callback(new Error("两次密码不一致"));
  else callback();
};

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }, { min: 6, message: "至少6位", trigger: "blur" }],
  confirmPassword: [{ required: true, message: "请确认密码", trigger: "blur" }, { validator: validatePass, trigger: "blur" }],
};

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;
  loading.value = true;
  try {
    const res = await userApi.register({
      username: form.username, password: form.password,
      name: form.name, phone: form.phone, age: form.age, gender: form.gender,
    });
    if (res.code === 200) {
      ElMessage.success("注册成功");
      router.push("/login");
    } else {
      ElMessage.error(res.message || "注册失败");
    }
  } catch (_) { /* 忽略 */ }
  loading.value = false;
};
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  padding: 0 20px;
  background: transparent;
  position: relative;
  z-index: 1;
}
.register-container {
  max-width: 420px;
  margin: 0 auto;
  padding: 28px 0 36px;
  position: relative;
  z-index: 2;
  animation: fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) both;
}
.register-header { margin-bottom: 26px; }
.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--text-light);
  cursor: pointer;
  margin-bottom: 22px;
  font-weight: 600;
  padding: 6px 14px 6px 10px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(34, 211, 238, 0.25);
  backdrop-filter: blur(8px);
  transition: all 0.2s;
}
.back-link:hover { color: var(--cyan-bright); border-color: var(--cyan-bright); background: rgba(34, 211, 238, 0.15); }
.register-brand h2 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-bright);
  font-family: var(--font-display);
  letter-spacing: 0.06em;
  text-shadow: 0 0 18px rgba(34, 211, 238, 0.45);
}
.register-brand p {
  font-size: 14px;
  color: var(--cyan-bright);
  margin-top: 8px;
  letter-spacing: 0.05em;
  opacity: 0.85;
}
.register-card {
  position: relative;
  background: var(--card-strong);
  backdrop-filter: blur(var(--glass-blur));
  -webkit-backdrop-filter: blur(var(--glass-blur));
  border: 1px solid var(--card-border);
  border-radius: 22px;
  padding: 26px 22px 22px;
  box-shadow: var(--shadow-xl), 0 0 40px rgba(34, 211, 238, 0.15);
  overflow: hidden;
}
.register-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 3px;
  background: var(--grad-glow);
}
.register-card :deep(.el-form-item__label) {
  font-size: 13px;
  font-weight: 600;
  color: var(--gray-600);
  padding-bottom: 4px;
  letter-spacing: 0.03em;
}
.register-card :deep(.el-input__wrapper),
.register-card :deep(.el-select .el-input__wrapper) {
  border-radius: 11px !important;
  background: rgba(255, 255, 255, 0.9) !important;
  box-shadow: 0 0 0 1px rgba(34, 211, 238, 0.25) inset !important;
}
.register-card :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--cyan-bright) inset !important;
}
.register-card :deep(.el-input__wrapper.is-focus) {
  background: #fff !important;
  box-shadow: var(--ring), 0 0 0 1px var(--primary) inset !important;
}
.register-btn {
  width: 100%;
  height: 50px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.22em;
  margin-top: 12px;
  background: var(--grad-primary) !important;
  border: none !important;
  box-shadow: 0 10px 28px rgba(14, 165, 233, 0.45);
}
.register-btn:hover { box-shadow: 0 14px 34px rgba(34, 211, 238, 0.6); transform: translateY(-1px); }
.login-hint {
  text-align: center;
  margin-top: 22px;
  font-size: 14px;
  color: var(--text-muted);
}
.login-hint a {
  color: var(--cyan-bright);
  text-decoration: none;
  font-weight: 700;
  margin-left: 4px;
  position: relative;
}
.login-hint a::after {
  content: '';
  position: absolute;
  left: 0; bottom: -2px;
  width: 100%; height: 1.5px;
  background: var(--cyan-bright);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.25s;
}
.login-hint a:hover::after { transform: scaleX(1); }
</style>
