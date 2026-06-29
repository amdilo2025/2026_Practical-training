-- ============================================================
-- 糖尿病预治智能助手 - 达梦DM8数据库初始化脚本
-- ============================================================

-- 创建数据库（达梦单实例下创建模式）
CREATE SCHEMA IF NOT EXISTS diabetes_db AUTHORIZATION SYSDBA;
SET SCHEMA 'diabetes_db';

-- 1. 用户表（病人端）
CREATE TABLE IF NOT EXISTS "user" (
    id INT IDENTITY(1,1) PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    name VARCHAR(50) DEFAULT NULL COMMENT '姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    age INT DEFAULT NULL COMMENT '年龄',
    gender VARCHAR(10) DEFAULT NULL COMMENT '性别(男/女)',
    height DECIMAL(5,2) DEFAULT NULL COMMENT '身高(cm)',
    weight DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
    diabetes_type VARCHAR(20) DEFAULT NULL COMMENT '糖尿病类型(1型/2型/妊娠期)',
    family_history BOOLEAN DEFAULT 0 COMMENT '家族史(0无/1有)',
    status VARCHAR(10) DEFAULT '正常' COMMENT '状态(正常/禁用)',
    created_time DATETIME DEFAULT SYSDATE COMMENT '创建时间',
    updated_time DATETIME DEFAULT SYSDATE ON UPDATE SYSDATE COMMENT '更新时间',
    INDEX idx_username (username)
) STORAGE(ON "MAIN", CLUSTERBTR) COMMENT='用户表(病人端)';

-- 2. 管理员表
CREATE TABLE IF NOT EXISTS admin (
    id INT IDENTITY(1,1) PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员账号',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    name VARCHAR(50) DEFAULT NULL COMMENT '姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    role VARCHAR(20) DEFAULT '管理员' COMMENT '角色(超级管理员/管理员)',
    status VARCHAR(10) DEFAULT '正常' COMMENT '状态',
    created_time DATETIME DEFAULT SYSDATE COMMENT '创建时间',
    INDEX idx_admin_username (username)
) STORAGE(ON "MAIN", CLUSTERBTR) COMMENT='管理员表';

-- 3. 医师表
CREATE TABLE IF NOT EXISTS doctor (
    id INT IDENTITY(1,1) PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '医师姓名',
    department VARCHAR(50) DEFAULT NULL COMMENT '科室',
    title VARCHAR(50) DEFAULT NULL COMMENT '职称(主任医师/副主任医师/主治医师)',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    introduction TEXT DEFAULT NULL COMMENT '医师简介',
    good_at VARCHAR(255) DEFAULT NULL COMMENT '擅长领域',
    status VARCHAR(10) DEFAULT '在职' COMMENT '状态(在职/离职)',
    created_time DATETIME DEFAULT SYSDATE COMMENT '创建时间'
) STORAGE(ON "MAIN", CLUSTERBTR) COMMENT='医师表';

-- 4. 咨询记录表
CREATE TABLE IF NOT EXISTS consultation (
    id INT IDENTITY(1,1) PRIMARY KEY COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '用户ID',
    doctor_id INT NOT NULL COMMENT '医师ID',
    content TEXT DEFAULT NULL COMMENT '咨询内容',
    reply TEXT DEFAULT NULL COMMENT '医师回复',
    status VARCHAR(20) DEFAULT '待回复' COMMENT '状态(待回复/已回复/已完成)',
    created_time DATETIME DEFAULT SYSDATE COMMENT '咨询时间',
    reply_time DATETIME DEFAULT NULL COMMENT '回复时间',
    INDEX idx_consult_user (user_id),
    INDEX idx_consult_doc (doctor_id),
    CONSTRAINT fk_consult_user FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT fk_consult_doc FOREIGN KEY (doctor_id) REFERENCES doctor(id)
) STORAGE(ON "MAIN", CLUSTERBTR) COMMENT='咨询记录表';

-- 5. 风险预测记录表
CREATE TABLE IF NOT EXISTS risk_prediction (
    id INT IDENTITY(1,1) PRIMARY KEY COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '用户ID',
    age INT DEFAULT NULL COMMENT '年龄',
    height DECIMAL(5,2) DEFAULT NULL COMMENT '身高(cm)',
    weight DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
    fasting_blood_sugar DECIMAL(5,2) DEFAULT NULL COMMENT '空腹血糖(mmol/L)',
    postprandial_blood_sugar DECIMAL(5,2) DEFAULT NULL COMMENT '餐后血糖(mmol/L)',
    blood_pressure_systolic INT DEFAULT NULL COMMENT '收缩压(mmHg)',
    blood_pressure_diastolic INT DEFAULT NULL COMMENT '舒张压(mmHg)',
    family_history BOOLEAN DEFAULT 0 COMMENT '家族史(0无/1有)',
    bmi DECIMAL(4,1) DEFAULT NULL COMMENT 'BMI值',
    risk_level VARCHAR(10) DEFAULT NULL COMMENT '风险等级(低风险/中风险/高风险)',
    risk_score INT DEFAULT NULL COMMENT '风险评分(0-100)',
    advice TEXT DEFAULT NULL COMMENT 'AI建议',
    created_time DATETIME DEFAULT SYSDATE COMMENT '预测时间',
    INDEX idx_risk_user (user_id),
    CONSTRAINT fk_risk_user FOREIGN KEY (user_id) REFERENCES "user"(id)
) STORAGE(ON "MAIN", CLUSTERBTR) COMMENT='风险预测记录表';

-- 6. 健康资讯表
CREATE TABLE IF NOT EXISTS health_article (
    id INT IDENTITY(1,1) PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    summary VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
    content TEXT DEFAULT NULL COMMENT '文章内容(富文本)',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    category VARCHAR(50) DEFAULT NULL COMMENT '分类(饮食/运动/用药/科普/并发症)',
    author VARCHAR(50) DEFAULT NULL COMMENT '作者',
    views INT DEFAULT 0 COMMENT '浏览次数',
    status VARCHAR(10) DEFAULT '已发布' COMMENT '状态(草稿/已发布)',
    created_time DATETIME DEFAULT SYSDATE COMMENT '发布时间',
    updated_time DATETIME DEFAULT SYSDATE ON UPDATE SYSDATE COMMENT '更新时间',
    INDEX idx_art_cate (category)
) STORAGE(ON "MAIN", CLUSTERBTR) COMMENT='健康资讯表';

-- 7. 打卡记录表
CREATE TABLE IF NOT EXISTS daily_checkin (
    id INT IDENTITY(1,1) PRIMARY KEY COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '用户ID',
    checkin_date DATE NOT NULL COMMENT '打卡日期',
    checkin_type VARCHAR(20) NOT NULL COMMENT '打卡类型(饮食/运动/服药/测血糖)',
    status BOOLEAN DEFAULT 1 COMMENT '完成状态(0未完成/1已完成)',
    content VARCHAR(255) DEFAULT NULL COMMENT '打卡内容(如:早餐吃了什么/运动项目)',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    created_time DATETIME DEFAULT SYSDATE COMMENT '创建时间',
    INDEX idx_check_user (user_id),
    INDEX idx_check_date (checkin_date),
    UNIQUE KEY uk_user_date_type (user_id, checkin_date, checkin_type),
    CONSTRAINT fk_check_user FOREIGN KEY (user_id) REFERENCES "user"(id)
) STORAGE(ON "MAIN", CLUSTERBTR) COMMENT='打卡记录表';

-- 8. 智能助手对话记录表
CREATE TABLE IF NOT EXISTS chat_message (
    id INT IDENTITY(1,1) PRIMARY KEY COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '用户ID',
    role VARCHAR(10) NOT NULL COMMENT '角色(user/ai)',
    content TEXT NOT NULL COMMENT '消息内容',
    session_id VARCHAR(50) DEFAULT NULL COMMENT '会话ID',
    created_time DATETIME DEFAULT SYSDATE COMMENT '发送时间',
    INDEX idx_chat_user (user_id),
    INDEX idx_chat_session (session_id),
    CONSTRAINT fk_chat_user FOREIGN KEY (user_id) REFERENCES "user"(id)
) STORAGE(ON "MAIN", CLUSTERBTR) COMMENT='智能助手对话记录表';

-- 9. 生活方案表（JSON类型适配为CLOB）
CREATE TABLE IF NOT EXISTS life_plan (
    id INT IDENTITY(1,1) PRIMARY KEY COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '用户ID',
    plan_type VARCHAR(20) NOT NULL COMMENT '方案类型(diet/exercise/sleep)',
    title VARCHAR(100) DEFAULT NULL COMMENT '方案标题',
    content CLOB DEFAULT NULL COMMENT '方案内容(JSON字符串)',
    status VARCHAR(10) DEFAULT '有效' COMMENT '状态(有效/过期)',
    created_time DATETIME DEFAULT SYSDATE COMMENT '创建时间',
    INDEX idx_plan_user (user_id),
    CONSTRAINT fk_plan_user FOREIGN KEY (user_id) REFERENCES "user"(id)
) STORAGE(ON "MAIN", CLUSTERBTR) COMMENT='生活方案表';

-- ============================================================
-- 测试数据（适配达梦语法）
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE chat_message;
TRUNCATE TABLE life_plan;
TRUNCATE TABLE daily_checkin;
TRUNCATE TABLE consultation;
TRUNCATE TABLE risk_prediction;
TRUNCATE TABLE health_article;
TRUNCATE TABLE doctor;
TRUNCATE TABLE "user";
TRUNCATE TABLE admin;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 测试用户（密码均为 123456，MD5加密值：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO "user" (username, password, name, phone, age, gender, height, weight, diabetes_type, family_history, status) VALUES
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138001', 45, '男', 170.00, 75.00, '2型', 1, '正常'),
('lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13800138002', 52, '男', 175.00, 80.00, '2型', 0, '正常'),
('wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', '13800138003', 35, '女', 160.00, 55.00, NULL, 1, '正常'),
('zhaoliu', 'e10adc3949ba59abbe56e057f20f883e', '赵六', '13800138004', 60, '男', 168.00, 72.00, '2型', 1, '正常'),
('sunqi', 'e10adc3949ba59abbe56e057f20f883e', '孙七', '13800138005', 28, '女', 165.00, 58.00, '1型', 0, '正常'),
('zhouba', 'e10adc3949ba59abbe56e057f20f883e', '周八', '13800138006', 48, '男', 172.00, 85.00, '2型', 1, '正常'),
('wujiu', 'e10adc3949ba59abbe56e057f20f883e', '吴九', '13800138007', 38, '女', 158.00, 62.00, '妊娠期', 0, '正常'),
('zhengshi', 'e10adc3949ba59abbe56e057f20f883e', '郑十', '13800138008', 55, '男', 180.00, 90.00, '2型', 1, '禁用');

-- 2. 测试管理员
INSERT INTO admin (username, password, name, phone, role) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13900000001', '超级管理员');

-- 3. 测试医师（8位）
INSERT INTO doctor (name, department, title, phone, introduction, good_at) VALUES
('赵建国', '内分泌科', '主任医师', '13700000001', '从事糖尿病临床诊疗25年，中华医学会糖尿病分会委员', '糖尿病诊断与治疗、胰岛素泵精细调控'),
('钱华', '内分泌科', '副主任医师', '13700000002', '专注糖尿病并发症研究十余年，发表SCI论文12篇', '糖尿病足、糖尿病视网膜病变、神经病变'),
('孙丽', '营养科', '主治医师', '13700000003', '国家注册营养师，擅长糖尿病医学营养治疗', '糖尿病饮食方案定制、营养咨询与教育'),
('李强', '内分泌科', '主任医师', '13700000004', '三甲医院内分泌科主任，糖尿病治疗领域专家', '难治性糖尿病、甲状腺疾病、代谢综合征'),
('周敏', '心血管科', '副主任医师', '13700000005', '擅长糖尿病合并心血管疾病的综合治疗', '糖尿病合并高血压、冠心病、血脂异常'),
('吴晓', '中医科', '主治医师', '13700000006', '中西医结合治疗糖尿病，中医调理慢性并发症', '糖尿病中医调理、中药辅助降糖'),
('郑明', '内分泌科', '主治医师', '13700000007', '糖尿病教育与管理专家，擅长患者自我管理指导', '糖尿病自我管理、血糖监测与用药指导'),
('陈芳', '运动康复科', '主治医师', '13700000008', '运动医学硕士，擅长糖尿病运动处方制定', '糖尿病运动康复、运动风险评估');

-- 4. 测试健康资讯（20篇，内容省略，保持原结构）
INSERT INTO health_article (title, summary, content, category, author, views) VALUES
('糖尿病患者饮食指南（完整版）', '全面系统的糖尿病饮食指导', '<h3>一、饮食总原则</h3>...', '饮食', '孙丽', 325),
('适合糖尿病患者的运动方式详解', '科学运动帮助控制血糖', '<h3>一、运动的好处</h3>...', '运动', '陈芳', 210),
('糖尿病用药须知——常见降糖药详解', '详细说明各类降糖药物', '<h3>一、口服降糖药</h3>...', '用药', '赵建国', 456),
('糖尿病并发症预防指南', '全面认识糖尿病并发症', '<h3>一、常见并发症</h3>...', '并发症', '钱华', 287),
('血糖监测的正确方法——学会自己测血糖', '学会正确使用血糖仪', '<h3>一、监测时间</h3>...', '科普', '郑明', 167),
('糖尿病患者如何科学吃水果', '水果并非禁忌，学会选择', '<h3>一、推荐水果</h3>...', '饮食', '孙丽', 198),
('糖尿病自我管理——做好自己的健康管家', '学会自我管理', '<h3>一、记录日记</h3>...', '科普', '郑明', 134),
('糖尿病足的预防与日常护理', '关注双足健康', '<h3>一、每日检查</h3>...', '并发症', '钱华', 245),
('糖尿病与高血压——双重管理', '糖尿病合并高血压管理', '<h3>一、两者关系</h3>...', '科普', '周敏', 112),
('糖尿病的中医调理方法', '中医药辅助治疗', '<h3>一、中医认识</h3>...', '用药', '吴晓', 89),
('妊娠期糖尿病的全程管理', '妊娠期糖尿病诊疗', '<h3>一、筛查与诊断</h3>...', '科普', '钱华', 76),
('糖尿病患者旅游出行注意事项', '安全出行指南', '<h3>一、行前准备</h3>...', '科普', '李强', 63),
('胰岛素注射技术详解', '正确注射胰岛素', '<h3>一、注射部位</h3>...', '用药', '赵建国', 156),
('运动前后如何预防低血糖', '安全运动防低血糖', '<h3>一、运动前</h3>...', '运动', '陈芳', 94),
('糖尿病患者如何科学选择主食', '粗细搭配选主食', '<h3>一、主食的重要性</h3>...', '饮食', '孙丽', 87),
('糖尿病患者饮酒指南', '饮酒对血糖的影响', '<h3>一、酒精对血糖的影响</h3>...', '科普', '李强', 56),
('糖尿病与睡眠——不可忽视的血糖影响因素', '睡眠与血糖关系', '<h3>一、睡眠与血糖的关系</h3>...', '科普', '周敏', 68),
('糖尿病肾病——早期发现与综合管理', '保护肾脏健康', '<h3>一、什么是糖尿病肾病</h3>...', '并发症', '赵建国', 112),
('糖尿病患者的心理调适——别让情绪影响血糖', '关注心理健康', '<h3>一、糖尿病与心理健康</h3>...', '科普', '郑明', 73),
('糖尿病患者冬季保健指南', '冬季血糖管理', '<h3>一、冬季血糖特点</h3>...', '科普', '钱华', 45);

-- 5. 测试咨询记录（12条）
INSERT INTO consultation (user_id, doctor_id, content, reply, status, created_time, reply_time) VALUES
(1, 1, '最近空腹血糖波动较大', '空腹血糖波动可能与饮食有关...', '已回复', SYSDATE-2, SYSDATE-1),
(1, 2, '我的脚有时会感觉麻木', '糖尿病神经病变是常见并发症...', '已回复', SYSDATE-5, SYSDATE-4),
(2, 3, '请问糖尿病可以吃水果吗？', '可以适量食用低GI水果...', '已回复', SYSDATE-3, SYSDATE-2),
(3, 4, '家族有病史，想了解如何预防？', '有家族史是高危因素...', '已回复', SYSDATE-1, SYSDATE-0.5),
(4, 1, '服用二甲双胍后胃部不适', '二甲双胍胃肠道反应常见...', '已回复', SYSDATE-4, SYSDATE-3),
(5, 4, '1型糖尿病血糖忽高忽低', '1型糖尿病血糖波动大...', '已回复', SYSDATE-6, SYSDATE-5),
(2, 6, '中医可以治疗糖尿病吗？', '中医药有辅助作用...', '已回复', SYSDATE-7, SYSDATE-6),
(6, 5, '糖尿病合并高血压注意什么？', '双重管理很重要...', '已回复', SYSDATE-8, SYSDATE-7),
(7, 3, '妊娠期糖尿病产后会恢复吗？', '大部分产后会恢复...', '已回复', SYSDATE-9, SYSDATE-8),
(1, 7, '工作压力大，血糖控制不好', NULL, '待回复', SYSDATE-0.1, NULL),
(4, 1, '想了解胰岛素泵治疗', NULL, '待回复', SYSDATE-0.05, NULL),
(6, 8, '运动后心慌出汗是低血糖吗？', '运动后心慌可能是低血糖...', '已回复', SYSDATE-10, SYSDATE-9);

-- 6. 测试打卡记录（近30天数据，示例）
INSERT INTO daily_checkin (user_id, checkin_date, checkin_type, status, content) VALUES
(1, CURRENT_DATE, '饮食', 1, '早餐：全麦面包2片+鸡蛋1个'),
(1, CURRENT_DATE, '运动', 1, '快走30分钟'),
(1, CURRENT_DATE-1, '饮食', 1, '早餐：燕麦粥+鸡蛋'),
(1, CURRENT_DATE-1, '运动', 1, '太极拳40分钟'),
(2, CURRENT_DATE, '饮食', 1, '早餐：杂粮粥+鸡蛋'),
(2, CURRENT_DATE, '服药', 1, '格列美脲1mg 早餐前');

-- 7. 测试风险预测记录（10条）
INSERT INTO risk_prediction (user_id, age, height, weight, fasting_blood_sugar, postprandial_blood_sugar, blood_pressure_systolic, blood_pressure_diastolic, family_history, bmi, risk_level, risk_score, advice) VALUES
(1, 45, 170.00, 75.00, 6.5, 9.2, 130, 85, 1, 25.9, '中风险', 65, '建议控制饮食，增加运动量'),
(1, 44, 170.00, 76.00, 6.8, 10.1, 135, 88, 1, 26.3, '高风险', 78, '血糖控制不佳，建议就医'),
(2, 52, 175.00, 80.00, 5.8, 8.5, 125, 82, 0, 26.1, '低风险', 35, '目前血糖控制较好'),
(3, 35, 160.00, 55.00, 5.0, 6.8, 118, 76, 1, 21.5, '低风险', 20, '各项指标良好'),
(4, 60, 168.00, 72.00, 7.2, 11.0, 142, 90, 1, 25.5, '高风险', 82, '血糖血压均偏高'),
(5, 28, 165.00, 58.00, 6.0, 9.5, 120, 78, 0, 21.3, '中风险', 55, '1型糖尿病需严格控糖'),
(6, 48, 172.00, 85.00, 6.0, 8.2, 138, 92, 1, 28.7, '中风险', 60, 'BMI偏高建议减重'),
(7, 38, 158.00, 62.00, 5.2, 7.0, 122, 80, 0, 24.8, '低风险', 28, '妊娠期需监测血糖'),
(4, 60, 168.00, 73.00, 7.5, 11.5, 145, 95, 1, 25.8, '高风险', 88, '建议立即就医'),
(1, 45, 170.00, 74.00, 6.3, 8.8, 128, 82, 1, 25.6, '中风险', 58, '较上次有所改善');

-- 8. 测试生活方案（7条，JSON适配为字符串）
INSERT INTO life_plan (user_id, plan_type, title, content) VALUES
(1, 'diet', '一周饮食方案', '{"breakfast":"全麦面包2片+鸡蛋1个","lunch":"杂粮饭150g+瘦肉50g","tips":"少油少盐"}'),
(1, 'exercise', '运动方案', '{"type":"有氧运动","frequency":"每周5次","duration":"30-45分钟"}'),
(2, 'diet', '控糖饮食方案', '{"breakfast":"燕麦粥+鸡蛋","lunch":"糙米饭+鸡肉","tips":"控制主食量"}'),
(2, 'exercise', '中老年运动方案', '{"type":"低强度有氧","frequency":"每周4次","activities":["太极拳","散步"]}'),
(3, 'diet', '减重饮食方案', '{"breakfast":"鸡蛋1个+全麦面包","tips":"控制总热量1200千卡/天"}'),
(4, 'diet', '降糖降压饮食方案', '{"breakfast":"荞麦面+鸡蛋","tips":"严格低盐<5g/天"}'),
(5, 'diet', '1型糖尿病饮食方案', '{"breakfast":"全麦面包+鸡蛋","tips":"学习碳水化合物计数法"}');

-- 9. 测试智能助手对话（多轮）
INSERT INTO chat_message (user_id, role, content, session_id) VALUES
(1, 'user', '糖尿病可以完全治愈吗？', 'session_001'),
(1, 'ai', '目前糖尿病尚无法完全治愈，但可控制血糖', 'session_001'),
(1, 'user', '我应该多久测一次血糖？', 'session_001'),
(1, 'ai', '稳定患者每周2-4次，波动大时每天监测', 'session_001'),
(2, 'user', '运动怕低血糖怎么办？', 'session_003'),
(2, 'ai', '运动前测血糖，随身携带糖果', 'session_003');