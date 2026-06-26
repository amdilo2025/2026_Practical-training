<template>
  <div class="article-detail">
    <div class="detail-header">
      <el-button text @click="$router.back()" class="back-btn">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
    </div>

    <div v-if="article" class="detail-content anim-fade-up">
      <div class="detail-cover" :style="{ background: `linear-gradient(135deg, #0f766e, #0c6259)` }">
        <span class="detail-category-tag">{{ article.category }}</span>
      </div>
      <div class="detail-body-wrap">
        <h1 class="detail-title">{{ article.title }}</h1>
        <div class="detail-meta">
          <span class="meta-author">{{ article.author }}</span>
          <span class="meta-divider">·</span>
          <span>{{ formatTime(article.createdTime) }}</span>
          <span class="meta-divider">·</span>
          <span>{{ article.views }} 次阅读</span>
        </div>
        <div class="detail-body" v-html="article.content"></div>
      </div>
    </div>
    <div v-else class="loading-state">
      <div class="skeleton" style="height:200px;margin-bottom:16px"></div>
      <div class="skeleton" style="height:24px;width:70%;margin-bottom:12px"></div>
      <div class="skeleton" style="height:16px;margin-bottom:8px"></div>
      <div class="skeleton" style="height:16px;width:85%;margin-bottom:8px"></div>
      <div class="skeleton" style="height:16px;width:60%"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { articleApi } from "../../api/index.js";

const route = useRoute();
const article = ref(null);

const formatTime = (t) => t ? t.slice(0, 10) : "";

onMounted(async () => {
  const res = await articleApi.getById(route.params.id);
  if (res.code === 200) article.value = res.data;
});
</script>

<style scoped>
.article-detail { padding-bottom: 24px; max-width: 500px; margin: 0 auto; }
.detail-header {
  position: sticky;
  top: 0;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  z-index: 10;
  padding: 10px 16px;
  border-bottom: 1px solid var(--gray-100);
}
.back-btn { color: var(--gray-600); font-size: 14px; font-weight: 600; }
.back-btn:hover { color: var(--primary); }
.detail-cover {
  height: 184px;
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  padding: 16px;
  position: relative;
}
.detail-category-tag {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(6px);
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 700;
  color: var(--primary-700);
  letter-spacing: 0.04em;
}
.detail-body-wrap {
  padding: 22px 16px 8px;
  background: var(--card);
  border-radius: 22px 22px 0 0;
  margin-top: -10px;
  position: relative;
  z-index: 1;
  box-shadow: 0 -6px 18px rgba(120, 90, 40, 0.06);
}
.detail-title { font-size: 24px; font-weight: 700; line-height: 1.35; margin-bottom: 12px; color: var(--gray-900); font-family: var(--font-display); letter-spacing: 0.03em; }
.detail-meta { display: flex; align-items: center; gap: 7px; font-size: 13px; color: var(--gray-400); margin-bottom: 20px; }
.detail-meta .meta-author { color: var(--primary-700); font-weight: 600; }
.meta-divider { color: var(--gray-200); }
.detail-body { font-size: 15px; line-height: 1.85; color: var(--gray-700); }
.detail-body img { max-width: 100%; border-radius: 12px; margin: 16px 0; }
.detail-body h3 { font-size: 18px; margin: 22px 0 10px; color: var(--gray-900); font-family: var(--font-display); }
.detail-body p { margin-bottom: 14px; }
.loading-state { padding: 20px 16px; }
</style>
