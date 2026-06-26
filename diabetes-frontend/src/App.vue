<template>
  <div id="app-container">
    <!-- 全局动态背景层：极光光斑 + 科技网格 + 漂浮粒子 -->
    <div class="bg-fx" aria-hidden="true">
      <div class="aurora aurora-1"></div>
      <div class="aurora aurora-2"></div>
      <div class="aurora aurora-3"></div>
      <div class="grid-lines"></div>
      <div class="particles">
        <span v-for="n in 42" :key="n" class="particle" :style="particleStyle(n)"></span>
      </div>
    </div>
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from "vue";

// 背景粒子：多色 + 三层视差（大慢近景 / 中景 / 小快远景），营造氛围与深度
const PARTICLE_COLORS = ["34, 211, 238", "14, 165, 233", "125, 211, 252", "255, 255, 255"];
const particleStyle = (n) => {
  const color = PARTICLE_COLORS[n % PARTICLE_COLORS.length];
  const layer = n % 3;
  const size = layer === 0 ? 6 + (n % 3) : layer === 1 ? 3.5 + (n % 2) : 1.5 + (n % 2);
  const dur = layer === 0 ? 24 + (n % 9) : layer === 1 ? 16 + (n % 7) : 10 + (n % 5);
  const left = (n * 23 + 7) % 100;
  const delay = (n * 1.1) % 14;
  const drift = (n % 2 === 0 ? 1 : -1) * (18 + (n % 6) * 14);
  return {
    left: left + "%",
    width: size + "px",
    height: size + "px",
    animationDelay: delay + "s",
    animationDuration: dur + "s",
    "--drift": drift + "px",
    background: `radial-gradient(circle, rgba(${color}, 0.95), rgba(${color}, 0.12))`,
    boxShadow: `0 0 ${4 + size}px rgba(${color}, 0.75)`,
  };
};

// 点击交互元素时爆发粒子（按钮 / 卡片 / 导航等）
const BURST_COLORS = ["#22d3ee", "#0ea5e9", "#06b6d4", "#f59e0b", "#ffffff"];
const BURST_SELECTOR =
  "button, .el-button, .nav-item, .quick-item, .action-item, .checkin-item, .article-card, .doctor-card, .cat-tag, .predict-btn, .login-btn, .register-btn, .send-btn, .submit-btn, .history-item, .tab-pane";

const spawnParticles = (x, y) => {
  const wrap = document.createElement("div");
  wrap.className = "click-burst";
  wrap.style.left = x + "px";
  wrap.style.top = y + "px";
  const count = 12;
  for (let i = 0; i < count; i++) {
    const p = document.createElement("span");
    const angle = (Math.PI * 2 * i) / count + Math.random() * 0.5;
    const dist = 30 + Math.random() * 50;
    const color = BURST_COLORS[i % BURST_COLORS.length];
    p.style.setProperty("--dx", Math.cos(angle) * dist + "px");
    p.style.setProperty("--dy", Math.sin(angle) * dist + "px");
    const sz = 4 + Math.random() * 5;
    p.style.width = sz + "px";
    p.style.height = sz + "px";
    p.style.background = color;
    p.style.boxShadow = "0 0 8px " + color;
    wrap.appendChild(p);
  }
  document.body.appendChild(wrap);
  setTimeout(() => wrap.remove(), 700);
};

onMounted(() => {
  document.addEventListener("click", (e) => {
    const el = e.target.closest && e.target.closest(BURST_SELECTOR);
    if (el) spawnParticles(e.clientX, e.clientY);
  });
});
</script>

<style>
html, body, #app, #app-container {
  height: 100%;
  width: 100%;
  margin: 0;
  padding: 0;
}
#app-container { position: relative; }

/* ===== 动态背景层 ===== */
.bg-fx {
  position: fixed;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  pointer-events: none;
}

/* 极光光斑 */
.aurora {
  position: absolute;
  border-radius: 50%;
  filter: blur(70px);
  opacity: 0.55;
  animation: auroraMove 24s ease-in-out infinite;
}
.aurora-1 {
  width: 500px; height: 500px;
  top: -140px; left: -120px;
  background: radial-gradient(circle, rgba(34, 211, 238, 0.55), transparent 70%);
}
.aurora-2 {
  width: 440px; height: 440px;
  bottom: -120px; right: -90px;
  background: radial-gradient(circle, rgba(14, 165, 233, 0.5), transparent 70%);
  animation-delay: -8s;
}
.aurora-3 {
  width: 380px; height: 380px;
  top: 38%; left: 45%;
  background: radial-gradient(circle, rgba(6, 182, 212, 0.4), transparent 70%);
  animation-delay: -16s;
}

/* 科技网格线 */
.grid-lines {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(34, 211, 238, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(34, 211, 238, 0.06) 1px, transparent 1px);
  background-size: 42px 42px;
  -webkit-mask-image: radial-gradient(ellipse at center, rgba(0, 0, 0, 0.7), transparent 75%);
  mask-image: radial-gradient(ellipse at center, rgba(0, 0, 0, 0.7), transparent 75%);
}

/* 漂浮粒子 */
.particles { position: absolute; inset: 0; }
.particle {
  position: absolute;
  bottom: -12px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(34, 211, 238, 0.95), rgba(34, 211, 238, 0.15));
  box-shadow: 0 0 10px rgba(34, 211, 238, 0.7);
  animation: particleDrift linear infinite;
}

@keyframes auroraMove {
  0%   { transform: translate(0, 0) scale(1); }
  33%  { transform: translate(44px, -32px) scale(1.12); }
  66%  { transform: translate(-32px, 42px) scale(0.94); }
  100% { transform: translate(0, 0) scale(1); }
}
@keyframes particleDrift {
  0%   { transform: translateY(0) translateX(0); opacity: 0; }
  8%   { opacity: 0.9; }
  92%  { opacity: 0.9; }
  100% { transform: translateY(-106vh) translateX(var(--drift, 42px)); opacity: 0; }
}

/* ===== 点击粒子爆发 ===== */
.click-burst {
  position: fixed;
  z-index: 9999;
  pointer-events: none;
}
.click-burst span {
  position: absolute;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: clickBurst 0.65s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}
@keyframes clickBurst {
  0%   { transform: translate(-50%, -50%) translate(0, 0) scale(1); opacity: 1; }
  100% { transform: translate(-50%, -50%) translate(var(--dx), var(--dy)) scale(0.2); opacity: 0; }
}
</style>
