-- ============================================================
-- 糖尿病预治智能助手 - 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS diabetes_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE diabetes_db;

-- 1. 用户表（病人端）
CREATE TABLE IF NOT EXISTS `user` (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
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
    family_history TINYINT(1) DEFAULT 0 COMMENT '家族史(0无/1有)',
    status VARCHAR(10) DEFAULT '正常' COMMENT '状态(正常/禁用)',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表(病人端)';

-- 2. 管理员表
CREATE TABLE IF NOT EXISTS `admin` (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员账号',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    name VARCHAR(50) DEFAULT NULL COMMENT '姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    role VARCHAR(20) DEFAULT '管理员' COMMENT '角色(超级管理员/管理员)',
    status VARCHAR(10) DEFAULT '正常' COMMENT '状态',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_admin_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 3. 医师表
CREATE TABLE IF NOT EXISTS `doctor` (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '医师姓名',
    department VARCHAR(50) DEFAULT NULL COMMENT '科室',
    title VARCHAR(50) DEFAULT NULL COMMENT '职称(主任医师/副主任医师/主治医师)',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    introduction TEXT DEFAULT NULL COMMENT '医师简介',
    good_at VARCHAR(255) DEFAULT NULL COMMENT '擅长领域',
    status VARCHAR(10) DEFAULT '在职' COMMENT '状态(在职/离职)',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医师表';

-- 4. 咨询记录表
CREATE TABLE IF NOT EXISTS `consultation` (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '用户ID',
    doctor_id INT NOT NULL COMMENT '医师ID',
    content TEXT DEFAULT NULL COMMENT '咨询内容',
    reply TEXT DEFAULT NULL COMMENT '医师回复',
    status VARCHAR(20) DEFAULT '待回复' COMMENT '状态(待回复/已回复/已完成)',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '咨询时间',
    reply_time DATETIME DEFAULT NULL COMMENT '回复时间',
    INDEX idx_consultation_user (user_id),
    INDEX idx_consultation_doctor (doctor_id),
    CONSTRAINT fk_consultation_user FOREIGN KEY (user_id) REFERENCES `user`(id),
    CONSTRAINT fk_consultation_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='咨询记录表';

-- 5. 风险预测记录表
CREATE TABLE IF NOT EXISTS `risk_prediction` (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '用户ID',
    age INT DEFAULT NULL COMMENT '年龄',
    height DECIMAL(5,2) DEFAULT NULL COMMENT '身高(cm)',
    weight DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
    fasting_blood_sugar DECIMAL(5,2) DEFAULT NULL COMMENT '空腹血糖(mmol/L)',
    postprandial_blood_sugar DECIMAL(5,2) DEFAULT NULL COMMENT '餐后血糖(mmol/L)',
    blood_pressure_systolic INT DEFAULT NULL COMMENT '收缩压(mmHg)',
    blood_pressure_diastolic INT DEFAULT NULL COMMENT '舒张压(mmHg)',
    family_history TINYINT(1) DEFAULT 0 COMMENT '家族史(0无/1有)',
    bmi DECIMAL(4,1) DEFAULT NULL COMMENT 'BMI值',
    risk_level VARCHAR(10) DEFAULT NULL COMMENT '风险等级(低风险/中风险/高风险)',
    risk_score INT DEFAULT NULL COMMENT '风险评分(0-100)',
    advice TEXT DEFAULT NULL COMMENT 'AI建议',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '预测时间',
    INDEX idx_risk_user (user_id),
    CONSTRAINT fk_risk_user FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险预测记录表';

-- 6. 健康资讯表
CREATE TABLE IF NOT EXISTS `health_article` (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    summary VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
    content TEXT DEFAULT NULL COMMENT '文章内容(富文本)',
    cover VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    category VARCHAR(50) DEFAULT NULL COMMENT '分类(饮食/运动/用药/科普/并发症)',
    author VARCHAR(50) DEFAULT NULL COMMENT '作者',
    views INT DEFAULT 0 COMMENT '浏览次数',
    status VARCHAR(10) DEFAULT '已发布' COMMENT '状态(草稿/已发布)',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_article_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康资讯表';

-- 7. 打卡记录表
CREATE TABLE IF NOT EXISTS `daily_checkin` (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '用户ID',
    checkin_date DATE NOT NULL COMMENT '打卡日期',
    checkin_type VARCHAR(20) NOT NULL COMMENT '打卡类型(饮食/运动/服药/测血糖)',
    status TINYINT(1) DEFAULT 1 COMMENT '完成状态(0未完成/1已完成)',
    content VARCHAR(255) DEFAULT NULL COMMENT '打卡内容(如:早餐吃了什么/运动项目)',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_checkin_user (user_id),
    INDEX idx_checkin_date (checkin_date),
    UNIQUE KEY uk_user_date_type (user_id, checkin_date, checkin_type),
    CONSTRAINT fk_checkin_user FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡记录表';

-- 8. 智能助手对话记录表
CREATE TABLE IF NOT EXISTS `chat_message` (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '用户ID',
    role VARCHAR(10) NOT NULL COMMENT '角色(user/ai)',
    content TEXT NOT NULL COMMENT '消息内容',
    session_id VARCHAR(50) DEFAULT NULL COMMENT '会话ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    INDEX idx_chat_user (user_id),
    INDEX idx_chat_session (session_id),
    CONSTRAINT fk_chat_user FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能助手对话记录表';

-- 9. 生活方案表
CREATE TABLE IF NOT EXISTS `life_plan` (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id INT NOT NULL COMMENT '用户ID',
    plan_type VARCHAR(20) NOT NULL COMMENT '方案类型(diet/exercise/sleep)',
    title VARCHAR(100) DEFAULT NULL COMMENT '方案标题',
    content JSON DEFAULT NULL COMMENT '方案内容(JSON)',
    status VARCHAR(10) DEFAULT '有效' COMMENT '状态(有效/过期)',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_plan_user (user_id),
    CONSTRAINT fk_plan_user FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生活方案表';

-- ============================================================
-- 测试数据
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE chat_message;
TRUNCATE TABLE life_plan;
TRUNCATE TABLE daily_checkin;
TRUNCATE TABLE consultation;
TRUNCATE TABLE risk_prediction;
TRUNCATE TABLE health_article;
TRUNCATE TABLE doctor;
TRUNCATE TABLE `user`;
TRUNCATE TABLE admin;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 测试用户（密码均为 123456）
INSERT INTO `user` (username, password, name, phone, age, gender, height, weight, diabetes_type, family_history, status) VALUES
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

-- 3. 测试医师（扩充到8位）
INSERT INTO doctor (name, department, title, phone, introduction, good_at) VALUES
('赵建国', '内分泌科', '主任医师', '13700000001', '从事糖尿病临床诊疗25年，中华医学会糖尿病分会委员', '糖尿病诊断与治疗、胰岛素泵精细调控'),
('钱华', '内分泌科', '副主任医师', '13700000002', '专注糖尿病并发症研究十余年，发表SCI论文12篇', '糖尿病足、糖尿病视网膜病变、神经病变'),
('孙丽', '营养科', '主治医师', '13700000003', '国家注册营养师，擅长糖尿病医学营养治疗', '糖尿病饮食方案定制、营养咨询与教育'),
('李强', '内分泌科', '主任医师', '13700000004', '三甲医院内分泌科主任，糖尿病治疗领域专家', '难治性糖尿病、甲状腺疾病、代谢综合征'),
('周敏', '心血管科', '副主任医师', '13700000005', '擅长糖尿病合并心血管疾病的综合治疗', '糖尿病合并高血压、冠心病、血脂异常'),
('吴晓', '中医科', '主治医师', '13700000006', '中西医结合治疗糖尿病，中医调理慢性并发症', '糖尿病中医调理、中药辅助降糖'),
('郑明', '内分泌科', '主治医师', '13700000007', '糖尿病教育与管理专家，擅长患者自我管理指导', '糖尿病自我管理、血糖监测与用药指导'),
('陈芳', '运动康复科', '主治医师', '13700000008', '运动医学硕士，擅长糖尿病运动处方制定', '糖尿病运动康复、运动风险评估');

-- 4. 测试健康资讯（扩充到14篇，内容丰富）
INSERT INTO health_article (title, summary, content, category, author, views) VALUES
('糖尿病患者饮食指南（完整版）', '全面系统的糖尿病饮食指导，涵盖三餐搭配、食物选择、禁忌等', '<h3>一、饮食总原则</h3><p>控制总热量摄入，保持理想体重。碳水化合物占总热量50-60%，蛋白质15-20%，脂肪25-30%。</p><h3>二、推荐食物</h3><p><b>主食：</b>全麦面包、糙米、燕麦、荞麦等粗粮<br><b>蔬菜：</b>绿叶蔬菜不限量，每餐至少200g<br><b>蛋白质：</b>鱼、虾、去皮鸡肉、豆制品<br><b>水果：</b>低GI水果，两餐之间食用</p><h3>三、禁忌食物</h3><p>含糖饮料、糕点、油炸食品、肥肉、动物内脏</p><h3>四、餐次安排</h3><p>少食多餐，每日5-6餐，每餐七八分饱</p>', '饮食', '孙丽', 325),
('适合糖尿病患者的运动方式详解', '科学运动帮助控制血糖，推荐多种适合糖友的运动', '<h3>一、运动的好处</h3><p>运动可以提高胰岛素敏感性，帮助降低血糖，改善心血管健康，控制体重。</p><h3>二、推荐运动</h3><p><b>1. 快走：</b>每天30分钟，心率达到（170-年龄）<br><b>2. 太极拳：</b>每周3-5次，每次40分钟<br><b>3. 游泳：</b>水温适宜，全身运动<br><b>4. 骑自行车：</b>室内室外均可<br><b>5. 力量训练：</b>每周2次，增强肌肉量</p><h3>三、注意事项</h3><p>运动前后测血糖，避免空腹运动，随身携带糖果预防低血糖。</p>', '运动', '陈芳', 210),
('糖尿病用药须知——常见降糖药详解', '详细说明各类降糖药物的用法、注意事项和可能的副作用', '<h3>一、口服降糖药</h3><p><b>二甲双胍：</b>一线用药，餐后服用减少胃肠反应<br><b>磺脲类：</b>促进胰岛素分泌，注意低血糖风险<br><b>DPP-4抑制剂：</b>平稳降糖，低血糖风险低<br><b>SGLT2抑制剂：</b>通过尿液排糖，注意泌尿系感染</p><h3>二、胰岛素</h3><p>适用于1型糖尿病和口服药控制不佳的2型糖尿病，分长效、短效、预混等多种类型。</p><h3>三、用药原则</h3><p>遵医嘱用药，不可自行增减或停药。定期复查血糖、肝肾功能。用药期间注意低血糖症状。</p>', '用药', '赵建国', 456),
('糖尿病并发症预防指南', '全面认识糖尿病并发症，学会预防和早期发现', '<h3>一、常见并发症</h3><p><b>1. 糖尿病足：</b>足部护理，每天检查双脚<br><b>2. 视网膜病变：</b>每年眼底检查<br><b>3. 糖尿病肾病：</b>控制血压，定期查尿蛋白<br><b>4. 神经病变：</b>手脚麻木、疼痛的早期识别</p><h3>二、预防措施</h3><p>严格控制血糖、血压、血脂；戒烟限酒；定期体检；保持良好生活习惯。</p><h3>三、早期信号</h3><p>视力模糊、手脚发麻、伤口不易愈合、反复感染等需及时就医。</p>', '并发症', '钱华', 287),
('血糖监测的正确方法——学会自己测血糖', '学会正确使用血糖仪，准确掌握血糖变化规律', '<h3>一、监测时间</h3><p><b>空腹血糖：</b>隔夜禁食8-12小时，晨起<br><b>餐后2小时血糖：</b>从吃第一口饭开始计时<br><b>睡前血糖：</b>避免夜间低血糖<br><b>随机血糖：</b>任何时间</p><h3>二、目标范围</h3><p><b>空腹血糖：</b>4.4-7.0 mmol/L<br><b>餐后2h血糖：</b><10.0 mmol/L<br><b>糖化血红蛋白：</b><7.0%</p><h3>三、注意事项</h3><p>用酒精消毒，待干后采血；轮换手指；记录血糖日记；定期校准血糖仪。</p>', '科普', '郑明', 167),
('糖尿病患者如何科学吃水果', '水果并非禁忌，学会选择和把握时机', '<h3>一、推荐水果（低GI）</h3><p>苹果、梨、草莓、蓝莓、柚子、樱桃、猕猴桃</p><h3>二、避免水果（高GI）</h3><p>西瓜、荔枝、龙眼、芒果、熟透的香蕉、葡萄干</p><h3>三、食用建议</h3><p>两餐之间作为加餐食用，每次不超过200g，相当于一个中等大小苹果。吃水果后适当减少主食量。</p>', '饮食', '孙丽', 198),
('糖尿病自我管理——做好自己的健康管家', '学会自我管理，让血糖控制更轻松', '<h3>一、记录日记</h3><p>记录血糖、饮食、运动、用药、症状等信息，复诊时带给医生。</p><h3>二、定期复查</h3><p>每3个月查糖化血红蛋白，每年全面体检一次。</p><h3>三、心理调节</h3><p>保持乐观心态，参加病友交流活动，必要时寻求心理咨询。</p><h3>四、紧急预案</h3><p>随身携带糖尿病急救卡，备好糖果和饼干应对低血糖。</p>', '科普', '郑明', 134),
('糖尿病足的预防与日常护理', '关注双足健康，远离糖尿病足', '<h3>一、每日检查</h3><p>每天检查双脚有无破损、水泡、红肿、变色。用镜子查看脚底。</p><h3>二、正确洗脚</h3><p>水温不超过37℃，用温和肥皂，擦干特别是趾缝。</p><h3>三、选择合适的鞋袜</h3><p>穿宽松舒适、透气性好的鞋袜，避免赤脚行走。</p><h3>四、及时就医</h3><p>发现任何足部问题及时就医，不要自行处理。</p>', '并发症', '钱华', 245),
('糖尿病与高血压——双重管理', '糖尿病合并高血压的综合管理策略', '<h3>一、两者关系</h3><p>糖尿病患者高血压发生率是非糖尿病的2倍，两者相互影响加重血管损害。</p><h3>二、治疗目标</h3><p>血压控制目标：<130/80 mmHg，合并肾病者<125/75 mmHg。</p><h3>三、综合管理</h3><p>低盐饮食（每日<5g），规律运动，遵医嘱用药，定期监测血压和肾功能。</p>', '科普', '周敏', 112),
('糖尿病的中医调理方法', '中医药在糖尿病治疗中的辅助作用', '<h3>一、中医认识</h3><p>糖尿病属中医"消渴"范畴，病机以阴虚燥热为主。</p><h3>二、常用中药</h3><p>黄芪、山药、葛根、天花粉、黄连等，需辨证论治。</p><h3>三、中医食疗</h3><p>苦瓜、山药、枸杞、桑叶等有辅助降糖作用。</p><h3>四、注意事项</h3><p>中医药不可替代西药降糖治疗，需在专业医师指导下使用。</p>', '用药', '吴晓', 89),
('妊娠期糖尿病的全程管理', '妊娠期糖尿病的诊断、治疗和产后随访', '<h3>一、筛查与诊断</h3><p>孕24-28周进行75g OGTT检查，空腹≥5.1、1h≥10.0、2h≥8.5 mmol/L任一项超标即可诊断。</p><h3>二、治疗</h3><p>医学营养治疗为主，血糖控制不佳时使用胰岛素。目标：空腹<5.3、餐后1h<7.8、餐后2h<6.7 mmol/L。</p><h3>三、产后随访</h3><p>产后6-12周复查OGTT，之后每1-3年筛查一次。</p>', '科普', '钱华', 76),
('糖尿病患者旅游出行注意事项', '安全出行，享受旅途', '<h3>一、行前准备</h3><p>携带充足的药物和血糖试纸、医生处方、急救卡。准备两倍于平时用量的药械。</p><h3>二、旅途中的血糖管理</h3><p>按时用药，注意时差调整。随身携带零食防止低血糖。多饮水，定时活动双脚。</p><h3>三、饮食注意</h3><p>尽量规律进餐，选择清淡食物，控制食量。</p>', '科普', '李强', 63),
('胰岛素注射技术详解', '正确注射胰岛素，确保疗效', '<h3>一、注射部位</h3><p>腹部吸收最快，上臂外侧、大腿外侧、臀部外上侧。轮换部位避免脂肪增生。</p><h3>二、注射方法</h3><p>捏起皮肤，45-90度角进针，缓慢推注，停留10秒后拔出。</p><h3>三、储存方法</h3><p>未开封胰岛素冷藏（2-8℃），开封后室温使用（不超过28℃），有效期28天。</p>', '用药', '赵建国', 156),
('运动前后如何预防低血糖', '安全运动，远离低血糖风险', '<h3>一、运动前</h3><p>测血糖，若<5.6 mmol/L先补充碳水化合物。避免空腹运动。</p><h3>二、运动中</h3><p>携带糖果或含糖饮料，出现心慌手抖立即停止运动并补充糖分。</p><h3>三、运动后</h3><p>运动后24小时内仍需防范延迟性低血糖，睡前测血糖。</p>', '运动', '陈芳', 94),
('糖尿病患者如何科学选择主食', '粗细搭配，选对主食血糖更平稳', '<h3>一、主食的重要性</h3><p>主食是人体能量的主要来源，糖尿病患者不是不能吃主食，而是要"会吃"。完全不吃主食反而可能导致低血糖和营养不良。</p><h3>二、推荐的主食</h3><p><b>① 全谷物类：</b>糙米、燕麦、荞麦、藜麦、全麦面包<br><b>② 杂豆类：</b>红豆、绿豆、鹰嘴豆（可混合米饭煮）<br><b>③ 薯类：</b>红薯、紫薯、山药（代替部分米面）<br><b>④ 其他：</b>玉米、小米、薏仁</p><h3>三、不推荐的主食</h3><p>白米饭、白馒头、白面包、稀粥、糯米饭、粽子等精制碳水化合物，升糖指数高，容易导致餐后血糖飙升。</p><h3>四、吃主食的技巧</h3><p>1. 每餐主食量控制在拳头大小（约100-150g熟重）<br>2. 先吃蔬菜和蛋白质，最后吃主食<br>3. 干饭比稀饭好（稀饭消化快升糖快）<br>4. 粗细搭配，粗粮占1/3到1/2</p>', '饮食', '孙丽', 87),
('糖尿病患者饮酒指南', '饮酒对血糖的影响及安全饮酒建议', '<h3>一、酒精对血糖的影响</h3><p>酒精会抑制肝脏糖异生，容易导致低血糖，尤其是空腹饮酒时。同时酒精热量高，长期大量饮酒会影响血糖控制和体重管理。</p><h3>二、饮酒建议</h3><p>如果血糖控制良好且无并发症，可少量饮酒：<br><b>① 男性：</b>每天不超过25克酒精（约啤酒750ml或红酒250ml或白酒75ml）<br><b>② 女性：</b>每天不超过15克酒精（约啤酒450ml或红酒150ml或白酒50ml）</p><h3>三、注意事项</h3><p>1. 切勿空腹饮酒，饮酒前先吃主食<br>2. 饮酒后注意监测血糖，特别是夜间血糖<br>3. 1型糖尿病、血糖控制差、有肝病或神经病变者应禁酒<br>4. 酒精可能导致延迟性低血糖（饮酒后8-12小时）</p>', '科普', '李强', 56),
('糖尿病与睡眠——不可忽视的血糖影响因素', '睡眠质量与血糖控制的密切关系', '<h3>一、睡眠与血糖的关系</h3><p>长期睡眠不足（<6小时）或睡眠质量差会导致：<br>① 胰岛素敏感性下降，血糖升高<br>② 食欲激素紊乱，更容易饥饿和暴食<br>③ 皮质醇水平升高，促进糖异生<br>④ 疲劳感增加，运动意愿降低</p><h3>二、改善睡眠的建议</h3><p>1. 固定作息时间，每天同一时间睡觉和起床<br>2. 睡前1小时避免使用手机、电脑等电子设备<br>3. 保持卧室黑暗、安静、凉爽<br>4. 下午4点后避免咖啡、浓茶等刺激性饮品<br>5. 睡前可泡脚、听轻音乐放松<br>6. 适量运动有助于改善睡眠，但睡前2小时避免剧烈运动</p><h3>三、警惕睡眠呼吸暂停</h3><p>糖尿病患者睡眠呼吸暂停综合征的发生率较高，典型表现为打鼾、呼吸暂停、白天嗜睡。如怀疑需进行多导睡眠监测。治疗睡眠呼吸暂停有助于改善血糖控制。</p>', '科普', '周敏', 68),
('糖尿病肾病——早期发现与综合管理', '保护肾脏，从早期筛查开始', '<h3>一、什么是糖尿病肾病</h3><p>糖尿病肾病是糖尿病最常见的微血管并发症之一，是终末期肾病的主要病因。约20-40%的糖尿病患者会发展为糖尿病肾病。</p><h3>二、早期信号</h3><p><b>① 微量白蛋白尿：</b>尿白蛋白/肌酐比值30-300mg/g，是早期肾病的标志<br><b>② 夜尿增多：</b>夜间排尿次数增多，提示肾小管功能受损<br><b>③ 水肿：</b>眼睑或下肢水肿，提示肾功能下降<br>④ 乏力、食欲不振等非特异性症状</p><h3>三、筛查与诊断</h3><p>1. 1型糖尿病诊断5年后、2型糖尿病确诊时即应开始筛查<br>2. 每年至少检测一次尿白蛋白/肌酐比值和血肌酐<br>3. 根据eGFR和尿白蛋白进行分期和风险分层</p><h3>四、综合管理</h3><p><b>① 严格控制血糖：</b>糖化血红蛋白目标<7.0%<br><b>② 严格控制血压：</b><130/80 mmHg，首选ACEI/ARB类药物<br><b>③ 限制蛋白质摄入：</b>每日0.8-1.0g/kg体重<br><b>④ 低盐饮食：</b>每日<5g<br><b>⑤ 避免肾毒性药物：</b>如非甾体抗炎药（布洛芬等）<br><b>⑥ 定期随访：</b>每3-6个月复查肾功能和尿蛋白</p>', '并发症', '赵建国', 112),
('糖尿病患者的心理调适——别让情绪影响血糖', '关注心理健康，积极面对糖尿病', '<h3>一、糖尿病与心理健康</h3><p>糖尿病患者抑郁症和焦虑症的患病率是普通人群的2-3倍。长期的疾病管理压力、对并发症的担忧、生活方式的改变等都可能导致心理问题。反过来，负面情绪也会通过激素分泌影响血糖控制。</p><h3>二、常见的心理问题</h3><p><b>① 糖尿病困扰：</b>对疾病管理的疲惫感和挫败感<br><b>② 低血糖恐惧：</b>担心低血糖发生，过度控制导致血糖反而升高<br><b>③ 饮食焦虑：</b>对食物的过度担忧，社交饮食的回避<br><b>④ 并发症担忧：</b>对未来健康状况的过度焦虑<br><b>⑤ 病耻感：</b>因疾病感到被歧视或自卑</p><h3>三、应对策略</h3><p>1. 正确认识疾病：糖尿病是可管理的慢性病，不影响正常生活和工作<br>2. 设定合理目标：不追求完美，循序渐进，允许偶尔的波动<br>3. 寻求社会支持：参加病友群、与家人朋友沟通、加入糖尿病教育项目<br>4. 培养兴趣爱好：转移注意力，增加生活乐趣<br>5. 学习放松技巧：深呼吸、冥想、渐进式肌肉放松<br>6. 必要时寻求专业帮助：心理咨询师或精神科医生</p><h3>四、给家人的建议</h3><p>家人的理解和支持对糖尿病患者的心理健康至关重要。不要过度指责或监督，而是给予鼓励和陪伴，一起学习糖尿病知识，共同参与健康生活方式的改变。</p>', '科普', '郑明', 73),
('糖尿病患者冬季保健指南', '寒冷季节的血糖管理和健康防护', '<h3>一、冬季血糖特点</h3><p>冬季气温低，人体为保持体温会分泌更多升糖激素，加上户外活动减少、节日饮食增多等因素，冬季血糖往往比夏季偏高，糖化血红蛋白平均升高0.3-0.5%。</p><h3>二、保暖防寒</h3><p>1. 注意四肢末梢保暖，糖尿病患者常伴有周围神经病变，对温度不敏感，易发生冻伤<br>2. 使用暖水袋时水温不超过60℃，用毛巾包裹后使用，避免直接接触皮肤导致低温烫伤<br>3. 穿宽松保暖的鞋袜，避免过紧影响血液循环</p><h3>三、适度运动</h3><p>1. 选择室内运动：太极拳、瑜伽、室内健身操等<br>2. 天气好时可在中午气温较高时外出散步<br>3. 运动前充分热身10分钟，避免运动损伤<br>4. 雾霾天气避免户外运动</p><h3>四、饮食管理</h3><p>1. 冬季进补要科学，避免高热量高脂肪的滋补品<br>2. 节日聚餐注意控制食量，先吃蔬菜和蛋白质<br>3. 多喝温水，保持充足水分<br>4. 火锅选择清汤锅底，少食蘸料</p><h3>五、皮肤护理</h3><p>冬季空气干燥，糖尿病患者皮肤更易干裂。洗澡水温不宜过高，浴后涂抹保湿乳液。每日检查双脚，发现破损及时处理。</p>', '科普', '钱华', 45);

-- 5. 测试咨询记录（扩充到12条）
INSERT INTO consultation (user_id, doctor_id, content, reply, status, created_time, reply_time) VALUES
(1, 1, '最近空腹血糖波动较大，从6.0到7.2之间，请问需要注意什么？', '空腹血糖波动可能与饮食、运动、药物等因素有关。建议连续监测3天并记录饮食情况，如果持续偏高建议来院调整用药。', '已回复', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 2, '我的脚有时会感觉麻木，是不是糖尿病足的早期症状？', '糖尿病神经病变是常见并发症，建议尽快来院做神经传导速度检查。日常注意足部护理，穿宽松舒适的鞋子。', '已回复', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 3, '请问糖尿病可以吃水果吗？哪些水果比较适合？', '可以适量食用低GI水果，如苹果、梨、草莓、柚子等。建议在两餐之间食用，每次不超过200克。', '已回复', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 4, '我今年35岁，没有确诊糖尿病，但家族有病史，想了解如何预防？', '有家族史是糖尿病的高危因素。建议保持健康体重，BMI控制在24以下；规律运动每周150分钟；每年体检查空腹血糖和糖化血红蛋白。', '已回复', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(4, 1, '服用二甲双胍后胃部不适，恶心，有什么办法缓解？', '二甲双胍的胃肠道反应常见。建议改为餐后立即服用，从低剂量开始逐渐加量。如持续不适可咨询医生更换缓释剂型。', '已回复', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5, 4, '我是一型糖尿病患者，最近血糖忽高忽低，应该怎么调整？', '1型糖尿病血糖波动大，建议：1.记录饮食和胰岛素用量的对应关系；2.考虑使用实时动态血糖监测；3.学习碳水化合物计数法。建议在医院进行胰岛素泵治疗评估。', '已回复', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 6, '中医可以治疗糖尿病吗？效果怎么样？', '中医药在糖尿病防治中有辅助作用，可改善症状、调节体质、延缓并发症发生。但不能替代西药降糖治疗，建议中西医结合治疗。', '已回复', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
(6, 5, '我有糖尿病合并高血压，平时应该注意些什么？', '双重管理很重要：1.低盐低脂饮食；2.每日监测血压和血糖；3.规律服用降压药和降糖药；4.定期检查心肾功能。血压控制在130/80以下。', '已回复', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(7, 3, '妊娠期糖尿病产后会恢复正常吗？', '大部分产妇产后血糖会恢复正常，但有部分会发展为2型糖尿病。建议产后6-12周复查OGTT，之后每1-3年筛查。哺乳期注意营养均衡。', '已回复', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(1, 7, '最近工作压力大，血糖控制不好了，怎么办？', '压力和情绪波动会影响血糖。建议：1.学习压力管理技巧；2.保证充足睡眠；3.规律运动释放压力；4.必要时寻求心理咨询。同时密切监测血糖变化。', '待回复', DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL),
(4, 1, '我想了解一下胰岛素泵治疗，适合我吗？', '胰岛素泵适用于1型糖尿病和部分血糖控制不佳的2型糖尿病。优点是模拟生理胰岛素分泌，血糖控制更平稳。建议来院做全面评估。', '待回复', DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL),
(6, 8, '运动后有时会心慌出汗，是低血糖吗？怎么预防？', '运动后心慌出汗很可能是低血糖。建议运动前测血糖，<5.6时先加餐；运动强度循序渐进；随身携带糖果；运动后睡前再测一次血糖。', '已回复', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY));

-- 6. 测试打卡记录（近30天连续数据）
INSERT INTO daily_checkin (user_id, checkin_date, checkin_type, status, content) VALUES
-- 用户1（张三）近7天完整打卡
(1, CURDATE(), '饮食', 1, '早餐：全麦面包2片+鸡蛋1个+牛奶250ml'),
(1, CURDATE(), '运动', 1, '快走30分钟'),
(1, CURDATE(), '服药', 1, '二甲双胍0.5g 早晚各一次'),
(1, CURDATE(), '测血糖', 1, '空腹血糖 6.2'),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '饮食', 1, '早餐：燕麦粥+鸡蛋+凉拌黄瓜'),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '运动', 1, '太极拳40分钟'),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '服药', 1, '按时服药'),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '测血糖', 1, '空腹血糖 6.8'),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '饮食', 1, '午餐：杂粮饭+清蒸鱼+炒青菜'),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '运动', 1, '散步30分钟'),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '服药', 1, '按时服药'),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '饮食', 1, '早餐：荞麦面+鸡蛋'),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '运动', 0, NULL),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '服药', 1, '按时服药'),
(1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), '饮食', 1, '晚餐：蔬菜汤+鸡肉沙拉'),
(1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), '运动', 1, '游泳40分钟'),
(1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), '服药', 1, '按时服药'),
(1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '饮食', 1, '早餐：小米粥+包子'),
(1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '运动', 1, '快走35分钟'),
(1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '服药', 1, '按时服药'),
(1, DATE_SUB(CURDATE(), INTERVAL 6 DAY), '饮食', 1, '午餐：糙米饭+西兰花炒鸡胸肉'),
(1, DATE_SUB(CURDATE(), INTERVAL 6 DAY), '运动', 0, NULL),
(1, DATE_SUB(CURDATE(), INTERVAL 6 DAY), '服药', 1, '按时服药'),
-- 用户2（李四）近7天数据
(2, CURDATE(), '饮食', 1, '早餐：杂粮粥+鸡蛋'),
(2, CURDATE(), '服药', 1, '格列美脲1mg 早餐前'),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '饮食', 1, '午餐：米饭+红烧鱼+青菜'),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '运动', 1, '太极拳30分钟'),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '服药', 1, '按时服药'),
(2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '饮食', 1, '早餐：燕麦+牛奶+苹果'),
(2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '运动', 0, NULL),
(2, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '饮食', 1, '晚餐：玉米+清炒白菜+豆腐汤'),
(2, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '服药', 1, '按时服药'),
-- 用户3（王五）近7天数据
(3, CURDATE(), '饮食', 1, '早餐：全麦面包+酸奶'),
(3, CURDATE(), '运动', 1, '瑜伽30分钟'),
(3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '饮食', 1, '午餐：藜麦沙拉+鸡胸肉'),
(3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '运动', 1, '快走25分钟'),
(3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '饮食', 0, '吃了火锅'),
(3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '运动', 0, NULL),
(3, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '饮食', 1, '早餐：鸡蛋+全麦面包+豆浆'),
(3, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '运动', 1, '跑步20分钟'),
-- 用户4（赵六）近5天数据
(4, CURDATE(), '饮食', 1, '早餐：荞麦面+鸡蛋'),
(4, CURDATE(), '服药', 1, '二甲双胍0.5g'),
(4, CURDATE(), '测血糖', 1, '空腹血糖 7.0'),
(4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '饮食', 1, '午餐：杂粮饭+芹菜炒肉'),
(4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '服药', 1, '按时服药'),
(4, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '饮食', 1, '晚餐：蔬菜沙拉+鱼肉'),
(4, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '运动', 1, '散步40分钟'),
(4, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '饮食', 1, '早餐：全麦面包+牛奶'),
(4, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '服药', 1, '按时服药'),
(4, DATE_SUB(CURDATE(), INTERVAL 4 DAY), '饮食', 1, '午餐：糙米+炖豆腐+青菜'),
(4, DATE_SUB(CURDATE(), INTERVAL 4 DAY), '运动', 0, NULL);

-- 7. 测试风险预测记录（扩充到10条）
INSERT INTO risk_prediction (user_id, age, height, weight, fasting_blood_sugar, postprandial_blood_sugar, blood_pressure_systolic, blood_pressure_diastolic, family_history, bmi, risk_level, risk_score, advice) VALUES
(1, 45, 170.00, 75.00, 6.5, 9.2, 130, 85, 1, 25.9, '中风险', 65, '建议控制饮食，增加运动量，定期监测血糖。'),
(1, 44, 170.00, 76.00, 6.8, 10.1, 135, 88, 1, 26.3, '高风险', 78, '血糖控制不佳，建议及时就医调整治疗方案。'),
(2, 52, 175.00, 80.00, 5.8, 8.5, 125, 82, 0, 26.1, '低风险', 35, '目前血糖控制较好，继续保持良好的生活习惯。'),
(3, 35, 160.00, 55.00, 5.0, 6.8, 118, 76, 1, 21.5, '低风险', 20, '各项指标良好，家族史需注意，建议每年体检。'),
(4, 60, 168.00, 72.00, 7.2, 11.0, 142, 90, 1, 25.5, '高风险', 82, '空腹和餐后血糖均偏高，血压也需控制，建议尽快就医。'),
(5, 28, 165.00, 58.00, 6.0, 9.5, 120, 78, 0, 21.3, '中风险', 55, '1型糖尿病患者需严格控制血糖，建议加强自我监测。'),
(6, 48, 172.00, 85.00, 6.0, 8.2, 138, 92, 1, 28.7, '中风险', 60, 'BMI偏高，建议减重，控制饮食，每周至少运动150分钟。'),
(7, 38, 158.00, 62.00, 5.2, 7.0, 122, 80, 0, 24.8, '低风险', 28, '指标基本正常，妊娠期需继续监测血糖。'),
(4, 60, 168.00, 73.00, 7.5, 11.5, 145, 95, 1, 25.8, '高风险', 88, '血糖持续偏高，血压升高明显，建议立即就医调整治疗方案。'),
(1, 45, 170.00, 74.00, 6.3, 8.8, 128, 82, 1, 25.6, '中风险', 58, '较上次有所改善，继续坚持饮食控制和运动。');

-- 8. 测试生活方案
INSERT INTO life_plan (user_id, plan_type, title, content) VALUES
(1, 'diet', '一周饮食方案', '{"breakfast": "全麦面包2片+鸡蛋1个+牛奶250ml", "lunch": "杂粮饭150g+瘦肉50g+蔬菜200g", "dinner": "蔬菜汤+鱼肉100g+蔬菜150g", "tips": "少油少盐，避免高糖食物，每餐七分饱"}'),
(1, 'exercise', '运动方案', '{"type": "有氧运动为主", "frequency": "每周5次", "duration": "每次30-45分钟", "activities": ["快走", "太极拳", "游泳", "骑自行车"], "notice": "运动前后测血糖，随身携带糖果"}'),
(2, 'diet', '控制血糖饮食方案', '{"breakfast": "燕麦粥+鸡蛋+蔬菜沙拉", "lunch": "糙米饭+鸡肉100g+绿叶蔬菜200g", "dinner": "清蒸鱼+凉拌蔬菜+玉米半根", "tips": "控制主食量，每餐不超过150g，晚餐少吃主食"}'),
(2, 'exercise', '中老年运动方案', '{"type": "低强度有氧", "frequency": "每周4-5次", "duration": "每次30分钟", "activities": ["太极拳", "八段锦", "散步"], "notice": "避免剧烈运动，注意膝盖保护"}'),
(3, 'diet', '减重饮食方案', '{"breakfast": "鸡蛋1个+全麦面包1片+黑咖啡", "lunch": "藜麦沙拉+鸡胸肉+圣女果", "dinner": "蔬菜汤+豆腐", "tips": "控制总热量在1200-1400千卡/天，多喝水"}'),
(4, 'diet', '降糖降压饮食方案', '{"breakfast": "荞麦面+鸡蛋+凉拌黄瓜", "lunch": "糙米饭+清炒蔬菜+鱼肉", "dinner": "玉米半根+蔬菜汤+瘦肉", "tips": "严格低盐（每日<5g），控制主食量"}'),
(5, 'diet', '1型糖尿病饮食方案', '{"breakfast": "全麦面包+鸡蛋+牛奶", "lunch": "米饭+瘦肉+蔬菜", "dinner": "杂粮饭+鱼肉+蔬菜", "tips": "学习碳水化合物计数法，配合胰岛素剂量调整"}');

-- 9. 测试智能助手对话（扩充到多轮对话）
INSERT INTO chat_message (user_id, role, content, session_id) VALUES
(1, 'user', '糖尿病可以完全治愈吗？', 'session_001'),
(1, 'ai', '目前糖尿病尚无法完全治愈，但通过合理的治疗和管理，可以将血糖控制在理想范围内，减少并发症的发生，保持正常的生活质量。', 'session_001'),
(1, 'user', '我应该多久测一次血糖？', 'session_001'),
(1, 'ai', '建议血糖控制稳定的患者每周测2-4次空腹和餐后血糖；血糖波动较大或调整药物期间，建议每天监测。具体频率请遵医嘱。', 'session_001'),
(1, 'user', '空腹血糖多少算正常？', 'session_001'),
(1, 'ai', '空腹血糖正常范围为3.9-6.1 mmol/L。糖尿病患者的目标是控制在4.4-7.0 mmol/L之间。如果空腹血糖持续高于7.0，说明血糖控制不佳，需要调整治疗方案。', 'session_001'),
(1, 'user', '我经常觉得口渴，是不是血糖太高了？', 'session_002'),
(1, 'ai', '口渴确实是高血糖的典型症状之一。当血糖升高时，身体会通过排尿排出多余的糖分，导致脱水和口渴感。建议您立即测一下血糖，如果明显偏高请及时就医调整用药。', 'session_002'),
(1, 'user', '好的，我测一下看看', 'session_002'),
(1, 'ai', '好的，测量后请记录数值。如果空腹血糖超过13.9 mmol/L或出现恶心、呕吐、呼吸有烂苹果味等酮症酸中毒症状，请立即就医。', 'session_002'),
(2, 'user', '我最近开始运动了，但怕低血糖怎么办？', 'session_003'),
(2, 'ai', '运动时预防低血糖很重要！建议：1.运动前测血糖，低于5.6时先吃些碳水；2.避免空腹运动；3.随身携带糖果或含糖饮料；4.运动后睡前再测一次；5.刚开始运动时强度不要太大。', 'session_003');
