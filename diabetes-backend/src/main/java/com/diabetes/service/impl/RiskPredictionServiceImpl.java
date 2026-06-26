package com.diabetes.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.diabetes.entity.RiskPrediction;
import com.diabetes.entity.User;
import com.diabetes.mapper.RiskPredictionMapper;
import com.diabetes.mapper.UserMapper;
import com.diabetes.service.RiskPredictionService;
import com.diabetes.utils.DifyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RiskPredictionServiceImpl implements RiskPredictionService {

    @Autowired
    private RiskPredictionMapper riskPredictionMapper;

    @Autowired
    private DifyClient difyClient;

    @Autowired
    private UserMapper userMapper;

    @Override
    public RiskPrediction getById(Integer id) {
        return riskPredictionMapper.selectById(id);
    }

    @Override
    public List<RiskPrediction> listByUser(Integer userId) {
        return riskPredictionMapper.findByUserId(userId);
    }

    @Override
    public RiskPrediction predict(RiskPrediction prediction) {
        // 计算BMI
        if (prediction.getHeight() != null && prediction.getWeight() != null && prediction.getHeight() > 0) {
            double bmi = prediction.getWeight() / ((prediction.getHeight() / 100) * (prediction.getHeight() / 100));
            prediction.setBmi(Math.round(bmi * 10.0) / 10.0);
        }

        // 从用户档案补全 workflow 所需的 sex / disease 等字段，组装结构化 inputs
        User user = userMapper.selectById(prediction.getUserId());
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("userId", user.getId());
        inputs.put("age", prediction.getAge());
        inputs.put("sex", (user.getGender() != null && !user.getGender().isEmpty()) ? user.getGender() : "男");
        inputs.put("height", prediction.getHeight());
        inputs.put("weight", prediction.getWeight());
        inputs.put("familyHistory", (prediction.getFamilyHistory() != null && prediction.getFamilyHistory() == 1) ? "有" : "无");
        inputs.put("disease", (user.getDiabetesType() != null && !user.getDiabetesType().isEmpty()) ? "是" : "否");
        inputs.put("fastingBloodSugar", prediction.getFastingBloodSugar());
        inputs.put("postprandialBloodSugar", prediction.getPostprandialBloodSugar());
        inputs.put("systolicPressure", prediction.getBloodPressureSystolic());
        inputs.put("diastolicPressure", prediction.getBloodPressureDiastolic());

        try {
            // 调用风险预测 workflow，解析输出 【等级】|评分:XX|建议:...
            Map<String, Object> outputs = difyClient.runRiskWorkflow(inputs, String.valueOf(user.getId()));
            String result = outputs.get("result") == null ? "" : outputs.get("result").toString();
            parseRiskResult(result, prediction);
        } catch (Exception e) {
            // workflow 调用或解析失败，使用本地算法兜底
            System.err.println("【风险预测workflow调用失败，使用本地兜底】" + e.getMessage());
            int score = calculateRiskScore(prediction);
            prediction.setRiskScore(score);
            prediction.setRiskLevel(score < 30 ? "低风险" : (score < 60 ? "中风险" : "高风险"));
            prediction.setAdvice(generateAdvice(prediction.getRiskLevel()));
        }

        riskPredictionMapper.insert(prediction);
        return prediction;
    }

    /** 解析 workflow 输出文本：【低/中/高风险】|评分:数字|建议:内容 */
    private void parseRiskResult(String result, RiskPrediction p) {
        // deepseek 可能输出 <think>思考过程</think>，先剔除，避免误匹配草稿
        String clean = result == null ? "" : result.replaceAll("(?s)<think>.*?</think>", "").trim();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(
                "【(低|中|高)风险】\\|评分:(\\d+)\\|建议:(.+)").matcher(clean);
        if (!m.find()) {
            throw new RuntimeException("workflow输出格式无法解析: " + result);
        }
        p.setRiskLevel(m.group(1) + "风险");
        p.setRiskScore(Integer.parseInt(m.group(2)));
        p.setAdvice(m.group(3).trim());
    }

    @Override
    public List<Map<String, Object>> riskTrend(Integer userId) {
        return riskPredictionMapper.selectRiskTrend(userId);
    }

    private int calculateRiskScore(RiskPrediction p) {
        int score = 0;
        if (p.getBmi() != null) {
            if (p.getBmi() > 28) score += 30;
            else if (p.getBmi() > 24) score += 15;
        }
        if (p.getFastingBloodSugar() != null) {
            if (p.getFastingBloodSugar() > 7.0) score += 30;
            else if (p.getFastingBloodSugar() > 6.1) score += 15;
        }
        if (p.getFamilyHistory() != null && p.getFamilyHistory() == 1) score += 15;
        if (p.getAge() != null) {
            if (p.getAge() > 50) score += 15;
            else if (p.getAge() > 40) score += 10;
            else if (p.getAge() > 30) score += 5;
        }
        return Math.min(score, 100);
    }

    private String generateAdvice(String level) {
        if ("高风险".equals(level)) {
            return "您属于糖尿病高风险人群，建议及时就医进行全面检查，严格控制饮食和血糖，遵医嘱进行药物治疗。";
        } else if ("中风险".equals(level)) {
            return "您存在一定的糖尿病风险，建议改善生活习惯，控制饮食，增加运动，定期监测血糖。";
        } else {
            return "您目前糖尿病风险较低，请继续保持良好的生活习惯，定期体检。";
        }
    }
}
