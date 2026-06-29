import { createRouter, createWebHashHistory } from "vue-router";

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/login/Login.vue"),
    meta: { title: "登录" },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("../views/login/Register.vue"),
    meta: { title: "注册" },
  },
  // ==================== 病人端 ====================
  {
    path: "/patient",
    component: () => import("../views/patient/Layout.vue"),
    redirect: "/patient/dashboard",
    children: [
      {
        path: "dashboard",
        name: "PatientDashboard",
        component: () => import("../views/patient/Dashboard.vue"),
        meta: { title: "首页", icon: "Odometer" },
      },
      {
        path: "personal",
        name: "PersonalCenter",
        component: () => import("../views/patient/PersonalCenter.vue"),
        meta: { title: "个人中心", icon: "User" },
      },
      {
        path: "doctor",
        name: "DoctorConsult",
        component: () => import("../views/patient/DoctorConsult.vue"),
        meta: { title: "医师咨询", icon: "ChatDotSquare" },
      },
      {
        path: "risk",
        name: "RiskPredict",
        component: () => import("../views/patient/RiskPredict.vue"),
        meta: { title: "风险预测", icon: "Warning" },
      },
      {
        path: "plan",
        name: "LifePlan",
        component: () => import("../views/patient/LifePlan.vue"),
        meta: { title: "生活方案", icon: "Document" },
      },
      {
        path: "article",
        name: "HealthArticle",
        component: () => import("../views/patient/HealthArticle.vue"),
        meta: { title: "健康资讯", icon: "Reading" },
      },
      {
        path: "article/detail/:id",
        name: "ArticleDetail",
        component: () => import("../views/patient/ArticleDetail.vue"),
        meta: { title: "资讯详情", hidden: true },
      },
      {
        path: "checkin",
        name: "DailyCheckin",
        component: () => import("../views/patient/DailyCheckin.vue"),
        meta: { title: "生活打卡", icon: "Check" },
      },
      {
        path: "chat",
        name: "SmartAssistant",
        component: () => import("../views/patient/SmartAssistant.vue"),
        meta: { title: "智能助手", icon: "MagicStick" },
      },
    ],
  },
  // ==================== 管理员端 ====================
  {
    path: "/admin",
    component: () => import("../views/admin/Layout.vue"),
    redirect: "/admin/dashboard",
    children: [
      {
        path: "dashboard",
        name: "AdminDashboard",
        component: () => import("../views/admin/Dashboard.vue"),
        meta: { title: "管理首页", icon: "Odometer" },
      },
      {
        path: "users",
        name: "UserManage",
        component: () => import("../views/admin/UserManage.vue"),
        meta: { title: "用户管理", icon: "User" },
      },
      {
        path: "doctors",
        name: "DoctorManage",
        component: () => import("../views/admin/DoctorManage.vue"),
        meta: { title: "医师管理", icon: "UserFilled" },
      },
      {
        path: "articles",
        name: "ArticleManage",
        component: () => import("../views/admin/ArticleManage.vue"),
        meta: { title: "资讯管理", icon: "Reading" },
      },
      {
        path: "consultations",
        name: "ConsultationManage",
        component: () => import("../views/admin/ConsultationManage.vue"),
        meta: { title: "咨询管理", icon: "ChatDotSquare" },
      },
      {
        path: "statistics",
        name: "Statistics",
        component: () => import("../views/admin/Statistics.vue"),
        meta: { title: "数据统计", icon: "DataAnalysis" },
      },
      {
        path: "data-query",
        name: "DataQuery",
        component: () => import("../views/admin/DataQuery.vue"),
        meta: { title: "数据查询", icon: "Search" },
      },
    ],
  },
  { path: "/:pathMatch(.*)*", redirect: "/login" },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  if (to.path === "/login" || to.path === "/register") {
    next();
    return;
  }

  if (!token) {
    next("/login");
    return;
  }

  // 病人端路由检查
  if (to.path.startsWith("/patient") && role !== "patient") {
    next("/login");
    return;
  }

  // 管理员端路由检查
  if (to.path.startsWith("/admin") && role !== "admin") {
    next("/login");
    return;
  }

  next();
});

export default router;
