import axios from "axios";
import { ElMessage } from "element-plus";

const BASE = "/diabetes/api";

const api = axios.create({
  baseURL: BASE,
  timeout: 15000,
});

// 请求拦截器：自动携带Token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code === 500) {
      ElMessage.error(res.message || "服务器错误");
      return Promise.reject(new Error(res.message));
    }
    return res;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("token");
      window.location.hash = "#/login";
      ElMessage.error("登录已过期，请重新登录");
    } else {
      ElMessage.error("网络错误: " + (error.message || "无法连接到服务器"));
    }
    return Promise.reject(error);
  }
);

// ==================== 用户模块 ====================
export const userApi = {
  login: (data) => api.post("/login", data),
  register: (data) => api.post("/register", data),
  getById: (id) => api.get(`/user/get/${id}`),
  update: (data) => api.put("/user/update", data),
  delete: (id) => api.delete(`/user/delete/${id}`),
  list: () => api.get("/user/list"),
  getProfile: (id) => api.get(`/user/profile/${id}`),
  updateProfile: (data) => api.put("/user/profile/update", data),
  changePassword: (data) => api.put("/user/password", data),
};

// ==================== 医师模块 ====================
export const doctorApi = {
  list: () => api.get("/doctor/list"),
  getById: (id) => api.get(`/doctor/get/${id}`),
  add: (data) => api.post("/doctor/add", data),
  update: (data) => api.put("/doctor/update", data),
  delete: (id) => api.delete(`/doctor/delete/${id}`),
};

// ==================== 咨询模块 ====================
export const consultationApi = {
  listByUser: (userId) => api.get(`/consultation/listByUser/${userId}`),
  getById: (id) => api.get(`/consultation/get/${id}`),
  add: (data) => api.post("/consultation/add", data, { timeout: 60000 }),
  reply: (data) => api.put("/consultation/reply", data),
  listAll: () => api.get("/consultation/listAll"),
};

// ==================== 风险预测模块 ====================
export const riskApi = {
  predict: (data) => api.post("/risk/predict", data, { timeout: 60000 }),
  listByUser: (userId) => api.get(`/risk/list/${userId}`),
  trend: (userId) => api.get(`/risk/trend/${userId}`),
  getById: (id) => api.get(`/risk/get/${id}`),
};

// ==================== 健康资讯模块 ====================
export const articleApi = {
  listPublished: () => api.get("/article/listPublished"),
  list: (status) => api.get("/article/list", { params: { status } }),
  getById: (id) => api.get(`/article/get/${id}`),
  add: (data) => api.post("/article/add", data),
  update: (data) => api.put("/article/update", data),
  delete: (id) => api.delete(`/article/delete/${id}`),
  findByCategory: (category) => api.get(`/article/category/${category}`),
  countByCategory: () => api.get("/article/countByCategory"),
};

// ==================== 打卡模块 ====================
export const checkinApi = {
  list: (userId, days) => api.get(`/checkin/list/${userId}`, { params: { days } }),
  do: (data) => api.post("/checkin/do", data),
  stats: (userId, days) => api.get(`/checkin/stats/${userId}`, { params: { days } }),
  trend: (userId, days) => api.get(`/checkin/trend/${userId}`, { params: { days } }),
};

// ==================== 智能助手模块 ====================
export const chatApi = {
  send: (data) => api.post("/chat/send", data),
  history: (userId, sessionId) => api.get(`/chat/history/${userId}/${sessionId}`),
  sessions: (userId) => api.get(`/chat/sessions/${userId}`),
};

// ==================== 生活方案模块 ====================
export const planApi = {
  listByUser: (userId) => api.get(`/plan/list/${userId}`),
  getLatest: (userId, planType) => api.get(`/plan/latest/${userId}/${planType}`),
  add: (data) => api.post("/plan/add", data),
  delete: (id) => api.delete(`/plan/delete/${id}`),
};

// ==================== 管理模块 ====================
export const adminApi = {
  dashboard: () => api.get("/admin/dashboard"),
  statistics: () => api.get("/admin/statistics"),
};

// ==================== 仪表盘 ====================
export const dashboardApi = {
  patient: (userId) => api.get(`/dashboard/patient/${userId}`),
  admin: () => api.get("/dashboard/admin"),
};

export default api;
