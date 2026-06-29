<template>
  <div class="health-article">
    <div class="page-header anim-fade-up">
      <h3>健康资讯</h3>
    </div>

    <!-- 分类标签 -->
    <div class="category-tabs anim-fade-up" style="animation-delay:0.1s">
      <div
        v-for="cat in categories"
        :key="cat.key"
        class="cat-tag"
        :class="{ active: activeCategory === cat.key }"
        @click="activeCategory = cat.key"
      >
        <span class="cat-icon">{{ cat.icon }}</span>
        <span>{{ cat.label }}</span>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="article-list anim-stagger">
      <div v-for="item in filteredList" :key="item.id" class="article-card" @click="goDetail(item.id)">
        <div class="article-cover" :style="{ backgroundImage: `linear-gradient(135deg, ${getGradient(item.category)})` }">
          <img class="cover-img" :src="getCover(item.category, item.id)" @error="onCoverError" loading="lazy" alt="" />
          <div class="cover-overlay"></div>
          <span class="cover-category">{{ item.category }}</span>
        </div>
        <div class="article-body">
          <div class="article-title">{{ item.title }}</div>
          <div class="article-summary">{{ item.summary || '暂无摘要' }}</div>
          <div class="article-footer">
            <span class="article-author">{{ item.author }}</span>
            <span class="article-views">
              <el-icon :size="12"><View /></el-icon>
              {{ item.views || 0 }}
            </span>
          </div>
        </div>
      </div>
      <div v-if="filteredList.length === 0" class="empty-state">
        <el-icon :size="40" color="#b3a487"><Reading /></el-icon>
        <p>暂无资讯</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { View } from "@element-plus/icons-vue";
import { articleApi } from "../../api/index.js";

const router = useRouter();
const articles = ref([]);
const activeCategory = ref("all");

const categories = [
  { key: "all", label: "全部", icon: "📋" },
  { key: "饮食", label: "饮食", icon: "🍎" },
  { key: "运动", label: "运动", icon: "🏃" },
  { key: "用药", label: "用药", icon: "💊" },
  { key: "科普", label: "科普", icon: "📖" },
  { key: "并发症", label: "并发症", icon: "⚠️" },
];

const filteredList = computed(() => {
  if (activeCategory.value === "all") return articles.value;
  return articles.value.filter(a => a.category === activeCategory.value);
});

const getGradient = (cat) => {
  const map = { "饮食": "#5b9bd5,#7cc3f2", "运动": "#4ba9e6,#2d6fa0", "用药": "#e8920f,#c2710c", "科普": "#7cc3f2,#5b9bd5", "并发症": "#e8807f,#c2710c" };
  return map[cat] || "#8a7c64,#635543";
};

// 分类封面图池（Unsplash 真实照片，已实测国内可访问，<1s）
const coverPool = {
  "饮食": ["1490645935967-10de6ba17061", "1512621776951-a57141f2eefd", "1498837167922-ddd27525d352", "1467003909585-2f8a72700288", "1565299624946-b28f40a0ae38"],
  "运动": ["1571019613454-1cb2f99b2d8b", "1517836357463-d25dfeac3438", "1571902943202-507ec2618e8f"],
  "用药": ["1584308666744-24d5c474f2ae", "1576091160550-2173dba999ef", "1631549916768-4119b2e5f926", "1587854692152-cbe660dbde88"],
  "科普": ["1507842217343-583bb7270b66", "1532012197267-da84d127e765"],
  "并发症": ["1559757148-5c350d0d3c56", "1629909613654-28e377c37b09"],
};
const getCover = (category, id) => {
  const pool = coverPool[category] || coverPool["科普"];
  const idx = (id || 0) % pool.length;
  return `https://images.unsplash.com/photo-${pool[idx]}?w=600&h=360&fit=crop&q=80`;
};
// 图片加载失败时隐藏，露出底层渐变作为兜底，避免破图
const onCoverError = (e) => { e.target.style.display = "none"; };

const goDetail = (id) => router.push(`/patient/article/detail/${id}`);

onMounted(async () => {
  const res = await articleApi.listPublished();
  if (res.code === 200) articles.value = res.data || [];
});
</script>

<style scoped>
.health-article { padding: 0 16px 20px; max-width: 500px; margin: 0 auto; }
.page-header { padding: 16px 0 12px; }
.page-header h3 { font-size: 22px; font-weight: 700; color: var(--gray-900); font-family: var(--font-display); letter-spacing: 0.04em; }

/* 分类标签 */
.category-tabs {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 8px;
  margin-bottom: 4px;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}
.category-tabs::-webkit-scrollbar { display: none; }
.cat-tag {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 16px;
  border-radius: 20px;
  background: #fff;
  color: var(--gray-600);
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  cursor: pointer;
  box-shadow: var(--shadow-xs);
  transition: all 0.2s;
  border: 1.5px solid transparent;
}
.cat-tag.active {
  background: var(--primary-50);
  color: var(--primary-700);
  border-color: var(--primary-300);
  font-weight: 700;
}
.cat-icon { font-size: 15px; }

/* 文章卡片 */
.article-card {
  background: #fff;
  border: 1px solid var(--gray-100);
  border-radius: 18px;
  overflow: hidden;
  margin-bottom: 14px;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: transform 0.18s cubic-bezier(0.16, 1, 0.3, 1), box-shadow 0.2s;
}
.article-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
.article-card:active { transform: scale(0.98); }
.article-cover {
  height: 140px;
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  padding: 12px;
  background-size: cover;
  position: relative;
  overflow: hidden;
}
.cover-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
  transition: transform 0.45s cubic-bezier(0.16, 1, 0.3, 1);
}
.article-card:hover .cover-img { transform: scale(1.06); }
.cover-overlay {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0) 38%, rgba(0, 0, 0, 0.48) 100%);
}
.cover-category {
  position: relative;
  z-index: 2;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(6px);
  padding: 4px 13px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
  color: var(--gray-800);
  letter-spacing: 0.04em;
}
.article-body { padding: 14px 16px 16px; }
.article-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--gray-900);
  margin-bottom: 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.article-summary {
  font-size: 13px;
  color: var(--gray-500);
  line-height: 1.55;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--gray-400);
}
.article-author { font-weight: 600; color: var(--gray-500); }
.article-views { display: flex; align-items: center; gap: 3px; font-family: var(--font-num); }
.empty-state {
  text-align: center;
  padding: 48px 0;
  color: var(--gray-400);
}
.empty-state p { margin-top: 12px; font-size: 14px; }
</style>
