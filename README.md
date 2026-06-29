# 糖尿病预治智能助手

> 基于 SpringBoot + MyBatis-Plus + Vue3 + Dify AI 的糖尿病健康管理平台  
> 适配手机竖屏，支持病人端与管理员端双角色

---

## 📋 项目概述

本项目是一个面向糖尿病患者的健康管理助手系统，提供血糖管理、风险预测、医师咨询、健康资讯、生活打卡、AI智能问答等核心功能。系统分为**病人端**和**管理员端**，后端集成 **Dify AI 智能体** 实现智能对话与风险预测。

### 八大业务模块

| 模块 | 说明 |
|------|------|
| **用户模块** | 注册、登录、JWT鉴权、个人中心 |
| **医师咨询模块** | 在线选择医师、提交咨询、查看回复 |
| **风险预测模块** | 录入身体指标，AI预测糖尿病风险等级 |
| **生活方案模块** | AI生成个性化饮食/运动方案 |
| **健康资讯模块** | 糖尿病知识库，分类浏览与详情查看 |
| **生活打卡模块** | 每日饮食/运动/服药/测血糖打卡与统计 |
| **智能助手模块** | Dify AI对话机器人，回答糖尿病相关问题 |
| **管理员模块** | 用户管理、医师管理、资讯管理、咨询管理、数据统计 |

---

## 🏗 技术架构

```
┌─────────────────────────────────────────────────────────────┐
│                   前端 (Vue3 + ElementPlus)                  │
│  病人端 (底部导航)  │  管理员端 (顶栏+抽屉菜单)  │  ECharts  │
├─────────────────────────────────────────────────────────────┤
│                     HTTP API (Axios → Proxy)                 │
├─────────────────────────────────────────────────────────────┤
│              后端 (SpringBoot + MyBatis-Plus)               │
│  Controller → Service → Mapper → MySQL  │  JWT  │  Redis   │
├──────────────────────┬──────────────────────────────────────┤
│     Dify AI 智能体    │            MySQL 数据库              │
│  聊天对话  │  风险预测  │  用户/医师/资讯/打卡等 9 张表      │
└──────────────────────┴──────────────────────────────────────┘
```

---

## 🔧 后端 (SpringBoot + MyBatis-Plus)

### 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 1.8+ | 开发语言 |
| Spring Boot | 2.7.18 | 框架 |
| MyBatis-Plus | 3.5.7 | ORM |
| MySQL | 8.0+ | 数据库 |
| Maven | 3.6+ | 构建工具 |
| JWT (jjwt) | 0.12.5 | 登录鉴权 |
| HuTool | 5.8.28 | 工具类（MD5加密等） |
| Lombok | — | ⚠️ 暂未使用，手动getter/setter |

### 项目结构

```
diabetes-backend/
├── pom.xml                              # Maven依赖配置
├── sql/init.sql                         # 数据库初始化脚本（9张表+测试数据）
└── src/main/java/com/diabetes/
    ├── DiabetesApplication.java         # 启动类
    ├── config/
    │   ├── CorsConfig.java              # 跨域配置
    │   └── MyBatisPlusConfig.java       # 分页插件
    ├── controller/                      # REST控制器（10个）
    │   ├── UserController.java          # 登录/注册/用户管理
    │   ├── DoctorController.java        # 医师管理
    │   ├── ConsultationController.java  # 咨询管理
    │   ├── RiskPredictionController.java # 风险预测
    │   ├── HealthArticleController.java # 健康资讯
    │   ├── DailyCheckinController.java  # 生活打卡
    │   ├── ChatController.java          # 智能助手（Dify对接）
    │   ├── LifePlanController.java      # 生活方案
    │   ├── AdminController.java         # 管理后台
    │   └── DashboardController.java     # 首页仪表盘
    ├── entity/                          # 数据库实体（9个）
    │   ├── User.java / Admin.java / Doctor.java
    │   ├── Consultation.java / RiskPrediction.java
    │   ├── HealthArticle.java / DailyCheckin.java
    │   ├── ChatMessage.java / LifePlan.java
    ├── entity/dto/                      # 数据传输对象（5个）
    ├── mapper/                          # MyBatis Mapper接口+@Select注解SQL
    ├── service/                         # 业务接口
    │   └── impl/                        # 业务实现
    ├── common/
    │   ├── R.java                       # 统一返回结果
    │   └── ResultCode.java              # 状态码
    └── utils/
        ├── JwtUtil.java                 # JWT工具类
        └── DifyClient.java              # Dify AI集成客户端
```

### API 接口概览

| 请求方式 | 接口路径 | 说明 |
|---------|---------|------|
| POST | `/diabetes/api/login` | 登录（病人/管理员） |
| POST | `/diabetes/api/register` | 注册 |
| GET | `/diabetes/api/user/list` | 用户列表（管理员） |
| GET | `/diabetes/api/doctor/list` | 医师列表 |
| POST | `/diabetes/api/consultation/add` | 提交咨询 |
| PUT | `/diabetes/api/consultation/reply` | 回复咨询（管理员） |
| POST | `/diabetes/api/risk/predict` | AI风险预测 |
| GET | `/diabetes/api/article/listPublished` | 健康资讯列表 |
| POST | `/diabetes/api/checkin/do` | 打卡 |
| POST | `/diabetes/api/chat/send` | 智能助手对话（调用Dify） |
| GET | `/diabetes/api/admin/dashboard` | 管理首页统计 |

### 数据库表结构

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `user` | 病人用户表 | username, password, name, phone, age, diabetes_type |
| `admin` | 管理员表 | username, password, name, role |
| `doctor` | 医师表 | name, department, title, good_at |
| `consultation` | 咨询记录表 | user_id, doctor_id, content, reply, status |
| `risk_prediction` | 风险预测表 | user_id, age, bmi, blood_sugar, risk_level, risk_score |
| `health_article` | 健康资讯表 | title, summary, content(HTML), category, author |
| `daily_checkin` | 打卡记录表 | user_id, checkin_date, checkin_type, status |
| `chat_message` | 智能助手对话表 | user_id, role(user/ai), content, session_id |
| `life_plan` | 生活方案表 | user_id, plan_type(diet/exercise), content(JSON) |

---

## 🎨 前端 (Vue3 + ElementPlus)

### 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.x | 框架 |
| Vue Router | 4.x | 路由 |
| Pinia | 2.x | 状态管理 |
| Element Plus | 2.14+ | UI组件库 |
| Axios | 1.x | HTTP请求 |
| ECharts | 5.x | 图表 |
| Vue CLI | 5.x | 构建工具 |

### 项目结构

```
diabetes-frontend/
├── package.json                         # 依赖配置
├── vue.config.js                        # 代理配置 → localhost:8090
├── public/index.html
└── src/
    ├── main.js                          # 入口：挂载ElementPlus/ECharts/Pinia
    ├── App.vue                          # 根组件
    ├── assets/theme.css                 # 全局主题样式（医用蓝色系+动画）
    ├── api/index.js                     # Axios封装 + 10个模块API
    ├── store/index.js                   # Pinia用户状态
    ├── router/index.js                  # 路由配置 + 路由守卫
    ├── utils/                           # 工具函数
    └── views/
        ├── login/
        │   ├── Login.vue                # 登录页（SVG医疗图标+渐变背景）
        │   └── Register.vue             # 注册页
        ├── patient/                     # 病人端（底部导航栏）
        │   ├── Layout.vue               # 布局：顶部栏+底部5Tab导航
        │   ├── Dashboard.vue            # 首页（问候卡+快捷功能+饼图+资讯+咨询）
        │   ├── PersonalCenter.vue       # 个人中心（头像卡片+信息+操作列表）
        │   ├── DoctorConsult.vue        # 医师咨询（医师卡片+咨询输入+历史）
        │   ├── RiskPredict.vue          # 风险预测（指标录入+结果展示）
        │   ├── LifePlan.vue             # 生活方案（饮食/运动Tab）
        │   ├── HealthArticle.vue        # 健康资讯（emoji分类+封面卡片）
        │   ├── ArticleDetail.vue        # 资讯详情（封面+骨架屏+HTML渲染）
        │   ├── DailyCheckin.vue         # 生活打卡（emoji打卡项+饼图+记录）
        │   └── SmartAssistant.vue       # AI智能助手（聊天气泡+快捷问题）
        └── admin/                       # 管理员端（抽屉菜单）
            ├── Layout.vue               # 布局：顶栏+侧边抽屉
            ├── Dashboard.vue            # 管理首页（统计卡片+用户趋势图）
            ├── UserManage.vue           # 用户管理
            ├── DoctorManage.vue         # 医师管理（CRUD）
            ├── ArticleManage.vue        # 资讯管理（发布/编辑/草稿）
            ├── ConsultationManage.vue   # 咨询管理（回复/查看）
            └── Statistics.vue           # 数据统计
```

### 前端特点

- **📱 竖版手机适配**：max-width 480px，底部固定导航栏，触摸友好的交互
- **🎯 双角色路由**：JWT鉴权 + 路由守卫自动跳转
- **🎬 动画系统**：卡片逐项入场、骨架屏加载、按钮缩放反馈
- **📊 ECharts图表**：首页资讯分类饼图、打卡统计饼图、管理端趋势图
- **🎨 医用蓝色主题**：#2563eb 为主色，圆角卡片 + 柔和阴影

---

## 🤖 Dify AI 智能体集成

### 架构图

```
用户提问 → 前端(axios) → ChatController → DifyClient → Dify API(v1/chat-messages)
                                                              ↓
用户 ← 前端展示 ← ChatController ← 解析JSON ← Dify API返回(answer+conversation_id)
```

### 集成方式

`DifyClient.java` 是封装Dify API的核心工具类，提供两个接口：

| 方法 | 用途 | Dify API |
|------|------|---------|
| `chat(query, userId, conversationId)` | 智能对话 | POST /v1/chat-messages |
| `predictRisk(params)` | 风险预测 | POST /v1/chat-messages |

### 对话流程

1. **新对话**：前端不传 sessionId，后端标记为 `"new"`，Dify自动创建对话
2. **返回结果**：Dify返回 `answer`（回答）+ `conversation_id`（会话ID）
3. **继续对话**：前端保存 `conversation_id`，后续消息带上它即可保持对话上下文

### Dify 配置步骤

1. 注册 [cloud.dify.ai](https://cloud.dify.ai)
2. 创建 **聊天助手** 应用
3. 设置系统提示词：
   ```
   你是一个专业的糖尿病健康管理助手。你可以：
   1. 回答关于糖尿病的饮食、运动、用药等方面的问题
   2. 根据用户提供的身体指标给出健康建议
   3. 提供糖尿病相关的科普知识
   回答请用中文，语气亲切专业，建议具体可行。
   ```
4. 可选：上传糖尿病知识文档创建知识库（RAG增强）
5. 发布应用，获取 API 密钥
6. 配置到 `application.properties`：

```properties
dify.api-url=https://api.dify.ai/v1
dify.api-key=app-你的API密钥
```

### 降级策略

当 Dify API 调用失败时（网络问题/密钥无效等），系统自动降级为**本地模拟回复**，涵盖饮食、运动、血糖、用药等常见问题，保证功能可用性。

---

## 🚀 快速启动

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+
- npm 8+

### 后端启动

```bash
# 1. 修改数据库密码
# 编辑 src/main/resources/application.properties
# spring.datasource.password=你的MySQL密码

# 2. 初始化数据库
mysql -u root -p < sql/init.sql

# 3. 启动后端
cd diabetes-backend
mvn spring-boot:run
# 启动在 http://localhost:8090/diabetes
```

### 前端启动

```bash
cd diabetes-frontend
npm install
npm run serve
# 启动在 http://localhost:8080
```

### 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 病人 | zhangsan | 123456 |
| 病人 | lisi | 123456 |
| 病人 | wangwu | 123456 |
| 病人 | zhaoliu | 123456 |
| 管理员 | admin | 123456 |

---

## 📊 测试数据

数据库初始化脚本包含完整测试数据：

| 数据表 | 数量 | 说明 |
|--------|:---:|------|
| 用户 | 8 | 含各类型糖尿病患者 |
| 医师 | 8 | 内分泌/营养/中医/运动康复等 |
| 健康资讯 | 20 | 含完整HTML内容 |
| 咨询记录 | 12 | 含医师回复 |
| 打卡记录 | 40+ | 近7天连续数据 |
| 风险预测 | 10 | 含同一用户多次记录 |
| 生活方案 | 7 | 饮食/运动方案 |
| 智能对话 | 12 | 多轮对话历史 |

---

## 📝 项目说明

本项目为课程实训作品，使用 **Dify AI 智能体** 实现 AI 对话与风险预测功能，前端采用 Vue3 + ElementPlus 构建移动端适配界面，后端采用 SpringBoot + MyBatis-Plus 提供 RESTful API。
