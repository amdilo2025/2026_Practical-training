<template>
  <div id="app-container">
    <!-- Canvas 粒子背景：天蓝粒子 + 近距连线 + 径向柔光 -->
    <canvas ref="bgCanvas" class="bg-canvas" aria-hidden="true"></canvas>

    <!-- 水墨晕染层（鼠标/触摸产生的拖尾溅射）-->
    <div class="ink-layer" aria-hidden="true"></div>

    <router-view />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";

const bgCanvas = ref(null);

// ===================== Canvas 粒子背景 =====================
let animId = null;
let W = 0, H = 0;
const particles = [];
const PARTICLE_COUNT = 48;

function createParticles() {
  particles.length = 0;
  for (let i = 0; i < PARTICLE_COUNT; i++) {
    particles.push({
      x: Math.random() * W,
      y: Math.random() * H,
      vx: (Math.random() - 0.5) * 0.18,
      vy: (Math.random() - 0.5) * 0.18,
      r: Math.random() * 2.6 + 0.8,
      alpha: Math.random() * 0.16 + 0.05,
      phase: Math.random() * Math.PI * 2,
    });
  }
}

function resizeCanvas() {
  if (!bgCanvas.value) return;
  const cvs = bgCanvas.value;
  const dpr = Math.min(window.devicePixelRatio || 1, 2);
  W = window.innerWidth;
  H = window.innerHeight;
  cvs.width = W * dpr;
  cvs.height = H * dpr;
  cvs.style.width = W + "px";
  cvs.style.height = H + "px";
  const ctx = cvs.getContext("2d");
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  createParticles();
}

function draw() {
  if (!bgCanvas.value) return;
  const ctx = bgCanvas.value.getContext("2d");
  ctx.clearRect(0, 0, W, H);

  // 径向柔光（浅色天蓝底，比body稍亮一点的柔光）
  const grad = ctx.createRadialGradient(W * 0.45, H * 0.2, 0, W * 0.5, H * 0.5, Math.max(W, H) * 0.48);
  grad.addColorStop(0, "rgba(148, 197, 240, 0.18)");
  grad.addColorStop(0.5, "rgba(180, 210, 245, 0.08)");
  grad.addColorStop(1, "rgba(220, 234, 255, 0)");
  ctx.fillStyle = grad;
  ctx.fillRect(0, 0, W, H);

  const now = Date.now() * 0.0007;
  for (let i = 0; i < particles.length; i++) {
    const p = particles[i];
    // 呼吸式大小
    const pulse = Math.sin(now + p.phase) * 0.25 + 1;
    const r = p.r * pulse;
    const alpha = p.alpha * pulse;

    // 绘制粒子光点
    ctx.beginPath();
    ctx.arc(p.x, p.y, r, 0, Math.PI * 2);
    const glow = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, r * 2.5);
    glow.addColorStop(0, `rgba(91, 155, 213, ${alpha * 1.2})`);
    glow.addColorStop(0.5, `rgba(148, 197, 240, ${alpha * 0.45})`);
    glow.addColorStop(1, "rgba(180, 210, 245, 0)");
    ctx.fillStyle = glow;
    ctx.fill();

    // 近距连线
    for (let j = i + 1; j < particles.length; j++) {
      const q = particles[j];
      const dx = p.x - q.x;
      const dy = p.y - q.y;
      const dist = Math.sqrt(dx * dx + dy * dy);
      const maxDist = 140;
      if (dist < maxDist) {
        ctx.beginPath();
        ctx.moveTo(p.x, p.y);
        ctx.lineTo(q.x, q.y);
        ctx.strokeStyle = `rgba(91, 155, 213, ${0.05 * (1 - dist / maxDist)})`;
        ctx.lineWidth = 0.5;
        ctx.stroke();
      }
    }

    // 移动
    p.x += p.vx;
    p.y += p.vy;
    if (p.x < -30) p.x = W + 30;
    if (p.x > W + 30) p.x = -30;
    if (p.y < -30) p.y = H + 30;
    if (p.y > H + 30) p.y = -30;
  }

  animId = requestAnimationFrame(draw);
}

// ===================== 水墨晕染交互 =====================
let lastMX = 0, lastMY = 0;
let moveTick = 0;

function spawnInkTrail(x, y, sizeScale = 1) {
  const el = document.createElement("div");
  el.className = "ink-trail";
  const size = (14 + Math.random() * 18) * sizeScale;
  el.style.width = size + "px";
  el.style.height = size + "px";
  el.style.left = (x + (Math.random() - 0.5) * 6) + "px";
  el.style.top = (y + (Math.random() - 0.5) * 6) + "px";
  document.querySelector(".ink-layer").appendChild(el);
  setTimeout(() => el.remove(), 1600);
}

function spawnSpeck(x, y) {
  const el = document.createElement("div");
  el.className = "ink-speck";
  const size = 8 + Math.random() * 14;
  el.style.width = size + "px";
  el.style.height = size + "px";
  el.style.left = (x + (Math.random() - 0.5) * 30) + "px";
  el.style.top = (y + (Math.random() - 0.5) * 30) + "px";
  document.querySelector(".ink-layer").appendChild(el);
  setTimeout(() => el.remove(), 1800);
}

function spawnClickInk(x, y) {
  const el = document.createElement("div");
  el.className = "ink-click";
  const size = 24 + Math.random() * 34;
  el.style.width = size + "px";
  el.style.height = size + "px";
  el.style.left = x + "px";
  el.style.top = y + "px";
  document.querySelector(".ink-layer").appendChild(el);
  setTimeout(() => el.remove(), 1000);

  // 溅射小点
  const count = 2 + Math.floor(Math.random() * 3);
  for (let i = 0; i < count; i++) {
    const speck = document.createElement("div");
    speck.className = "ink-speck";
    const ss = 10 + Math.random() * 20;
    speck.style.width = ss + "px";
    speck.style.height = ss + "px";
    speck.style.left = (x + (Math.random() - 0.5) * 60) + "px";
    speck.style.top = (y + (Math.random() - 0.5) * 60) + "px";
    document.querySelector(".ink-layer").appendChild(speck);
    setTimeout(() => speck.remove(), 1800);
  }
}

function onMouseMove(e) {
  moveTick++;
  if (moveTick % 2 !== 0) { lastMX = e.clientX; lastMY = e.clientY; return; }
  const dist = Math.hypot(e.clientX - lastMX, e.clientY - lastMY);
  if (dist < 4) return;
  spawnInkTrail(e.clientX, e.clientY, 0.9);
  if (Math.random() < 0.25) spawnSpeck(e.clientX, e.clientY);
  lastMX = e.clientX;
  lastMY = e.clientY;
}

function onMouseDown(e) {
  spawnClickInk(e.clientX, e.clientY);
}

function onTouchStart(e) {
  const t = e.touches[0];
  if (t) spawnClickInk(t.clientX, t.clientY);
}

function onTouchMove(e) {
  const t = e.touches[0];
  if (!t) return;
  const dist = Math.hypot(t.clientX - lastMX, t.clientY - lastMY);
  if (dist < 6) return;
  spawnInkTrail(t.clientX, t.clientY, 0.85);
  lastMX = t.clientX;
  lastMY = t.clientY;
}

// ===================== 生命周期 =====================
onMounted(() => {
  resizeCanvas();
  if (bgCanvas.value) {
    animId = requestAnimationFrame(draw);
  }
  window.addEventListener("resize", resizeCanvas);
  document.addEventListener("mousemove", onMouseMove, { passive: true });
  document.addEventListener("mousedown", onMouseDown, { passive: true });
  document.addEventListener("touchstart", onTouchStart, { passive: true });
  document.addEventListener("touchmove", onTouchMove, { passive: true });
});

onUnmounted(() => {
  cancelAnimationFrame(animId);
  window.removeEventListener("resize", resizeCanvas);
  document.removeEventListener("mousemove", onMouseMove);
  document.removeEventListener("mousedown", onMouseDown);
  document.removeEventListener("touchstart", onTouchStart);
  document.removeEventListener("touchmove", onTouchMove);
});
</script>

<style>
html, body, #app, #app-container {
  height: 100%;
  width: 100%;
  margin: 0;
  padding: 0;
}
#app-container {
  position: relative;
  display: flex;
  justify-content: center;
}

/* ===== Canvas 粒子背景 ===== */
.bg-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

/* ===== 水墨晕染层 ===== */
.ink-layer {
  position: fixed;
  inset: 0;
  z-index: 9996;
  pointer-events: none;
}

/* ---- ink-trail：鼠标/触摸移动拖尾（浅色底用中蓝）---- */
.ink-trail {
  position: fixed;
  pointer-events: none;
  border-radius: 50%;
  background: radial-gradient(circle at center,
    rgba(75, 169, 230, 0.28) 0%,
    rgba(91, 155, 213, 0.1) 35%,
    rgba(148, 197, 240, 0.04) 60%,
    transparent 80%
  );
  transform: translate(-50%, -50%) scale(0);
  animation: inkTravel 1.8s cubic-bezier(0.05, 0.8, 0.2, 1) forwards;
}
@keyframes inkTravel {
  0%   { transform: translate(-50%, -50%) scale(0); opacity: 0.7; }
  30%  { opacity: 0.5; }
  65%  { opacity: 0.15; }
  100% { transform: translate(-50%, -50%) scale(7); opacity: 0; }
}

/* ---- ink-speck：随机溅射小点 ---- */
.ink-speck {
  position: fixed;
  pointer-events: none;
  border-radius: 50%;
  background: radial-gradient(circle at center,
    rgba(91, 155, 213, 0.15) 0%,
    transparent 70%
  );
  transform: translate(-50%, -50%) scale(0);
  animation: speckDrift 2s cubic-bezier(0.05, 0.8, 0.2, 1) forwards;
}
@keyframes speckDrift {
  0%   { transform: translate(-50%, -50%) scale(0); opacity: 0.45; }
  45%  { opacity: 0.2; }
  100% { transform: translate(-50%, -50%) scale(10); opacity: 0; }
}

/* ---- ink-click：点击晕染爆发（浅色底用中蓝墨韵）---- */
.ink-click {
  position: fixed;
  pointer-events: none;
  border-radius: 50%;
  background: radial-gradient(circle at center,
    rgba(91, 155, 213, 0.32) 0%,
    rgba(75, 169, 230, 0.14) 30%,
    rgba(148, 197, 240, 0.05) 55%,
    transparent 75%
  );
  transform: translate(-50%, -50%) scale(0);
  animation: inkBurst 1.1s cubic-bezier(0.05, 0.8, 0.2, 1) forwards;
}
@keyframes inkBurst {
  0%   { transform: translate(-50%, -50%) scale(0); opacity: 1; }
  25%  { opacity: 0.9; }
  55%  { opacity: 0.35; }
  100% { transform: translate(-50%, -50%) scale(9); opacity: 0; }
}

/* ===== 桌面端手机壳（浅色版）===== */
@media (min-width: 640px) {
  body {
    display: flex;
    justify-content: center;
    padding: 32px 0;
    background: #c8dcee;
  }
  #app-container {
    max-width: 480px;
    border-radius: 32px;
    overflow: hidden;
    box-shadow:
      0 0 0 1px rgba(148, 197, 240, 0.15),
      0 8px 48px rgba(91, 155, 213, 0.12),
      0 0 0 8px rgba(220, 234, 255, 0.6),
      0 0 0 9px rgba(148, 197, 240, 0.1);
    margin: 0 auto;
  }
  /* 管理端不套手机壳 */
  body:has(.admin-layout) {
    padding: 0;
    background: #dceaff;
  }
  body:has(.admin-layout) #app-container {
    max-width: 100%;
    width: 100%;
    border-radius: 0;
    overflow: visible;
    box-shadow: none;
  }
}

/* 尊重动效偏好 */
@media (prefers-reduced-motion: reduce) {
  .ink-trail, .ink-speck, .ink-click { display: none; }
  .bg-canvas { opacity: 0.3; }
}
</style>
