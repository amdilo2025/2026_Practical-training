package com.diabetes.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.diabetes.entity.RiskPrediction;
import com.diabetes.mapper.RiskPredictionMapper;
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

        // 调用Dify获取风险预测
        Map<String, Object> params = new HashMap<>();
        params.put("age", prediction.getAge());
        params.put("bmi", prediction.getBmi());
        params.put("fastingBloodSugar", prediction.getFastingBloodSugar());
        params.put("postprandialBloodSugar", prediction.getPostprandialBloodSugar());
        params.put("familyHistory", prediction.getFamilyHistory());
        params.put("bloodPressureSystolic", prediction.getBloodPressureSystolic());
        params.put("bloodPressureDiastolic", prediction.getBloodPressureDiastolic());

        String aiResponse = difyClient.predictRisk(params);
        try {
            JSONObject json = JSONUtil.parseObj(aiResponse);
            prediction.setRiskScore(json.getInt("riskScore"));
            prediction.setRiskLevel(json.getStr("riskLevel"));
            prediction.setAdvice(json.getStr("advice"));
        } catch (Exception e) {
            // 如果Dify返回格式不对，使用本地计算
            int score = calculateRiskScore(prediction);
            prediction.setRiskScore(score);
            prediction.setRiskLevel(score < 30 ? "低风险" : (score < 60 ? "中风险" : "高风险"));
            prediction.setAdvice(generateAdvice(prediction.getRiskLevel()));
        }

        riskPredictionMapper.insert(prediction);
        return prediction;
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
