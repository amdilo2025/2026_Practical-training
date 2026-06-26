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

    @Value("${dify.risk-api-key:}")
    private String riskApiKey;

    @Value("${dify.doctor-api-key:}")
    private String doctorApiKey;

    private final RestTemplate restTemplate;

    public DifyClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 智能助手对话（原有逻辑，使用 dify.api-key）
     */
    public Map<String, String> chat(String query, String userId, String conversationId) {
        return chatWith(query, userId, conversationId, new HashMap<>(), apiKey);
    }

    /**
     * 通用对话接口：可传入 inputs（开场变量）和指定应用的 API key。赵晓峰医生使用。
     * 返回 Map 包含 answer 和 conversation_id
     */
    public Map<String, String> chatWith(String query, String userId, String conversationId,
                                        Map<String, Object> inputs, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            Map<String, Object> body = new HashMap<>();
            body.put("inputs", inputs == null ? new HashMap<>() : inputs);
            body.put("query", query);
            body.put("user", userId);
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
            System.err.println("【Dify对话调用失败】" + e.getMessage());
            Map<String, String> result = new HashMap<>();
            result.put("answer", getMockResponse(query));
            result.put("conversation_id", conversationId);
            return result;
        }
    }

    /**
     * 调用风险预测 workflow（使用 dify.risk-api-key）
     * 返回 workflow 的 outputs（含 result、disease）
     */
    public Map<String, Object> runRiskWorkflow(Map<String, Object> inputs, String userId) {
        return runWorkflow(inputs, userId, riskApiKey);
    }

    /**
     * 通用 workflow 执行：POST /workflows/run，blocking 模式，返回 data.outputs
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> runWorkflow(Map<String, Object> inputs, String userId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        Map<String, Object> body = new HashMap<>();
        body.put("inputs", inputs == null ? new HashMap<>() : inputs);
        body.put("user", userId);
        body.put("response_mode", "blocking");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                apiUrl + "/workflows/run", request, Map.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Dify workflow 无响应");
        }
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        if (data == null) {
            throw new RuntimeException("Dify workflow 响应缺少 data");
        }
        Object outputs = data.get("outputs");
        return outputs instanceof Map ? (Map<String, Object>) outputs : new HashMap<>();
    }

    public String getDoctorApiKey() {
        return doctorApiKey;
    }

    /**
     * 模拟AI回答（当Dify不可用时）
     */
    private String getMockResponse(String query) {
        if (query == null) return "您好，我是赵晓峰医生助手，请问有什么可以帮您？";
        if (query.contains("饮食") || query.contains("吃")) {
            return "建议您控制总热量摄入，多食蔬菜和全谷物，减少高糖高脂食物。每日三餐定时定量，可少食多餐。";
        } else if (query.contains("运动")) {
            return "每周建议进行150分钟中等强度有氧运动，如快走、太极拳、游泳等。运动前后注意监测血糖，避免低血糖。";
        } else if (query.contains("血糖")) {
            return "空腹血糖理想控制在4.4-7.0mmol/L，餐后2小时血糖<10.0mmol/L。如有异常请及时就医。";
        } else if (query.contains("药")) {
            return "请严格遵医嘱用药，不可自行增减药量或停药。定期复查血糖和肝肾功能。";
        } else {
            return "您好！我是内科主任赵晓峰。关于您的问题，建议保持良好的生活习惯，定期监测血糖，遵医嘱用药。如有不适请及时就医。";
        }
    }
}
