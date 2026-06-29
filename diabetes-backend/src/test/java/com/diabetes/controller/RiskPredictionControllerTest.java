package com.diabetes.controller;

import com.diabetes.entity.RiskPrediction;
import com.diabetes.service.RiskPredictionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RiskPredictionController 单元测试 —— 目标覆盖率 100%
 */
@WebMvcTest(RiskPredictionController.class)
@DisplayName("RiskPredictionController 风险预测控制器")
class RiskPredictionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RiskPredictionService riskPredictionService;

    @Test
    @DisplayName("POST /api/risk/predict → 返回预测结果")
    void predict() throws Exception {
        RiskPrediction prediction = new RiskPrediction();
        prediction.setUserId(100);
        prediction.setAge(55);
        prediction.setHeight(170.0);
        prediction.setWeight(80.0);

        RiskPrediction result = new RiskPrediction();
        result.setId(1);
        result.setRiskLevel("中风险");
        result.setRiskScore(45);
        result.setAdvice("建议改善生活习惯");

        when(riskPredictionService.predict(any(RiskPrediction.class))).thenReturn(result);

        mockMvc.perform(post("/api/risk/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prediction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.riskLevel").value("中风险"));
    }

    @Test
    @DisplayName("GET /api/risk/list/{userId} → 返回预测历史")
    void listByUser() throws Exception {
        RiskPrediction prediction = new RiskPrediction();
        prediction.setId(1);
        prediction.setRiskLevel("低风险");

        when(riskPredictionService.listByUser(100)).thenReturn(List.of(prediction));

        mockMvc.perform(get("/api/risk/list/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].riskLevel").value("低风险"));
    }

    @Test
    @DisplayName("GET /api/risk/trend/{userId} → 返回风险趋势")
    void trend() throws Exception {
        when(riskPredictionService.riskTrend(100)).thenReturn(List.of());

        mockMvc.perform(get("/api/risk/trend/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/risk/get/{id} → 返回预测详情")
    void getById() throws Exception {
        RiskPrediction prediction = new RiskPrediction();
        prediction.setId(1);
        prediction.setRiskLevel("高风险");
        prediction.setRiskScore(85);

        when(riskPredictionService.getById(1)).thenReturn(prediction);

        mockMvc.perform(get("/api/risk/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.riskLevel").value("高风险"));
    }
}
