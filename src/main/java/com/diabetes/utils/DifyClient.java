package com.diabetes.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class DifyClient {

    @Value("${dify.api-url:https://api.dify.ai/v1}")
    private String apiUrl;

    @Value("${dify.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public DifyClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 调用Dify对话API，返回Map包含answer和conversation_id
     */
    public Map<String, String> chat(String query, String userId, String conversationId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("inputs", new HashMap<>());
            body.put("query", query);
            body.put("user", userId);
            // 仅当conversationId有效且不是"new"时才传递
            if (conversationId != null && !conversationId.isEmpty() && !"new".equals(conversationId)) {
                body.put("conversation_id", conversationId);
            }
            body.put("response_mode", "blocking");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl + "/chat-messages", request, Map.class);

            Map<String, String> result = new HashMap<>();
            if (response.getBody() != null) {
                result.put("answer", (String) response.getBody().get("answer"));
                String newConvId = (String) response.getBody().get("conversation_id");
                if (newConvId != null) {
                    result.put("conversation_id", newConvId);
                }
            }
            return result;
        } catch (Exception e) {
            System.err.println("【Dify调用失败】" + e.getMessage());
            e.printStackTrace();
            Map<String, String> result = new HashMap<>();
            result.put("answer", getMockResponse(query));
            result.put("conversation_id", conversationId);
            return result;
        }
    }

    /**
     * 调用Dify进行风险预测
     */
    public String predictRisk(Map<String, Object> params) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            StringBuilder query = new StringBuilder();
            query.append("根据以下数据预测糖尿病风险：");
            params.forEach((k, v) -> query.append(k).append(":").append(v).append(","));

            Map<String, Object> body = new HashMap<>();
            body.put("inputs", new HashMap<>());
            body.put("query", query.toString());
            body.put("user", "risk_prediction");
            body.put("response_mode", "blocking");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl + "/chat-messages", request, Map.class);

            if (response.getBody() != null) {
                return (String) response.getBody().get("answer");
            }
        } catch (Exception e) {
            System.err.println("【Dify风险预测失败】" + e.getMessage());
            return getMockRiskResponse(params);
        }
        return null;
    }

    /**
     * 模拟AI回答（当Dify不可用时）
     */
    private String getMockResponse(String query) {
        if (query.contains("饮食") || query.contains("吃")) {
            return "建议您控制总热量摄入，多食蔬菜和全谷物，减少高糖高脂食物。每日三餐定时定量，可少食多餐。";
        } else if (query.contains("运动")) {
            return "每周建议进行150分钟中等强度有氧运动，如快走、太极拳、游泳等。运动前后注意监测血糖，避免低血糖。";
        } else if (query.contains("血糖")) {
            return "空腹血糖理想控制在4.4-7.0mmol/L，餐后2小时血糖<10.0mmol/L。如有异常请及时就医。";
        } else if (query.contains("药")) {
            return "请严格遵医嘱用药，不可自行增减药量或停药。定期复查血糖和肝肾功能。";
        } else {
            return "您好！我是糖尿病智能助手。关于您的问题，建议您保持良好的生活习惯，定期监测血糖，遵医嘱用药。如有不适请及时就医。";
        }
    }

    /**
     * 模拟风险预测（当Dify不可用时）
     */
    private String getMockRiskResponse(Map<String, Object> params) {
        double riskScore = 0;
        double bmi = params.containsKey("bmi") ? Double.parseDouble(params.get("bmi").toString()) : 24;
        if (bmi > 28) riskScore += 30;
        else if (bmi > 24) riskScore += 15;

        double bloodSugar = params.containsKey("fastingBloodSugar") ? Double.parseDouble(params.get("fastingBloodSugar").toString()) : 5.6;
        if (bloodSugar > 7.0) riskScore += 30;
        else if (bloodSugar > 6.1) riskScore += 15;

        if (params.containsKey("familyHistory") && "1".equals(params.get("familyHistory").toString())) riskScore += 15;

        int age = params.containsKey("age") ? Integer.parseInt(params.get("age").toString()) : 30;
        if (age > 50) riskScore += 15;
        else if (age > 40) riskScore += 10;
        else if (age > 30) riskScore += 5;

        String level = riskScore < 30 ? "低风险" : (riskScore < 60 ? "中风险" : "高风险");

        return String.format(
                "{\"riskScore\": %.0f, \"riskLevel\": \"%s\", \"advice\": \"%s\"}",
                riskScore, level,
                level.equals("高风险") ? "您属于糖尿病高风险人群，建议及时就医进行全面检查。"
                        : level.equals("中风险") ? "您存在一定的糖尿病风险，建议改善生活习惯，定期监测血糖。"
                        : "您目前糖尿病风险较低，请继续保持良好的生活习惯。"
        );
    }
}
