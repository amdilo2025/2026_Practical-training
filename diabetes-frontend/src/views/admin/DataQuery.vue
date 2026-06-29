<template>
  <div class="data-query">
    <h3 class="page-title">数据查询</h3>

    <el-card shadow="never" class="query-card">
      <template #header>
        <span class="card-header-title">自然语言查询</span>
      </template>

      <el-input
        v-model="queryText"
        type="textarea"
        :rows="3"
        placeholder="输入要查询的内容，例如：查一下最近一周新增的用户、血糖异常的用户有哪些、统计各科室的医师数量"
        maxlength="200"
        show-word-limit
        @keyup.enter.ctrl="doQuery"
      />
      <div class="query-bar">
        <div class="query-hints">
          <span class="hint-label">试试看：</span>
          <el-tag
            v-for="hint in hints"
            :key="hint"
            size="small"
            class="hint-tag"
            @click="queryText = hint; doQuery()"
          >{{ hint }}</el-tag>
        </div>
        <el-button
          type="primary"
          :loading="loading"
          :disabled="!queryText.trim()"
          @click="doQuery"
          class="query-btn"
        >
          <el-icon><Search /></el-icon> 查询
        </el-button>
      </div>
    </el-card>

    <el-card v-if="result" shadow="never" class="result-card">
      <template #header>
        <span class="card-header-title">
          查询结果
          <el-tag v-if="result.total !== undefined" size="small" round class="count-tag">
            共 {{ result.total }} 条
          </el-tag>
        </span>
      </template>

      <div v-if="result.columns && result.rows">
        <!-- 表格模式 -->
        <div class="table-wrap" v-if="result.rows.length > 0">
          <el-table :data="result.rows" border stripe size="small" style="width:100%">
            <el-table-column
              v-for="col in result.columns"
              :key="col"
              :prop="col"
              :label="col"
              min-width="120"
              show-overflow-tooltip
            />
          </el-table>
        </div>
        <el-empty v-else description="查询无结果" :image-size="80" />
      </div>

      <!-- 纯文本模式 -->
      <div v-else-if="result.text" class="result-text">{{ result.text }}</div>

      <!-- 兜底：整体展示 -->
      <pre v-else class="result-raw">{{ JSON.stringify(result, null, 2) }}</pre>
    </el-card>

    <el-card v-else-if="errorMsg" shadow="never" class="error-card">
      <el-alert :title="errorMsg" type="error" show-icon :closable="false" />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { Search } from "@element-plus/icons-vue";
import { adminApi } from "../../api/index.js";

const queryText = ref("");
const loading = ref(false);
const result = ref(null);
const errorMsg = ref("");

const hints = [
  "查一下最近一周新增的用户",
  "待回复的咨询有哪些",
  "统计各科室的医师数量",
];

const doQuery = async () => {
  if (!queryText.value.trim() || loading.value) return;
  loading.value = true;
  result.value = null;
  errorMsg.value = "";

  try {
    const res = await adminApi.dataQuery({ query: queryText.value });
    if (res.code === 200) {
      // 后端返回的 data 可能是 {body: {columns, rows, total}}，需要解包
      const raw = res.data;
      if (raw && raw.body) {
        result.value = raw.body;
      } else {
        result.value = raw;
      }
    } else {
      errorMsg.value = res.message || "查询失败";
    }
  } catch (e) {
    errorMsg.value = "查询请求失败，请检查网络或后端服务";
  }
  loading.value = false;
};
</script>

<style scoped>
.data-query { width: 100%; }
.page-title {
  font-size: 22px;
  margin-bottom: 18px;
  font-family: var(--font-display);
  color: var(--gray-900);
  letter-spacing: 0.04em;
}

.query-card { margin-bottom: 16px; }
.card-header-title {
  font-weight: 700;
  font-size: 14px;
  color: var(--gray-700);
}

.query-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-top: 14px;
  flex-wrap: wrap;
}
.query-hints {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
  flex: 1;
}
.hint-label { font-size: 12px; color: var(--gray-400); white-space: nowrap; }
.hint-tag { cursor: pointer; transition: all 0.15s; }
.hint-tag:hover { background: var(--primary-50); color: var(--primary-700); }
.query-btn { flex-shrink: 0; }

.result-card { margin-bottom: 16px; }
.count-tag { margin-left: 8px; }

.table-wrap {
  overflow-x: auto;
  border-radius: 10px;
  border: 1px solid var(--gray-100);
}

.result-text {
  font-size: 14px;
  color: var(--gray-700);
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}
.result-raw {
  font-size: 12px;
  color: var(--gray-500);
  background: var(--cream);
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  white-space: pre-wrap;
}

.error-card { margin-bottom: 16px; }
</style>
