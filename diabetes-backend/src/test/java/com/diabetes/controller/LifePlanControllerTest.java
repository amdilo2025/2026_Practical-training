package com.diabetes.controller;

import com.diabetes.entity.LifePlan;
import com.diabetes.service.LifePlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * LifePlanController 单元测试 —— 目标覆盖率 100%
 */
@WebMvcTest(LifePlanController.class)
@DisplayName("LifePlanController 生活方案控制器")
class LifePlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LifePlanService lifePlanService;

    @Test
    @DisplayName("GET /api/plan/list/{userId} → 返回方案列表")
    void listByUser() throws Exception {
        LifePlan plan = new LifePlan();
        plan.setId(1);
        plan.setPlanType("饮食");
        plan.setTitle("糖尿病饮食计划");

        when(lifePlanService.listByUser(100)).thenReturn(List.of(plan));

        mockMvc.perform(get("/api/plan/list/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].planType").value("饮食"));
    }

    @Test
    @DisplayName("GET /api/plan/latest/{userId}/{planType} → 返回最新方案")
    void getLatest() throws Exception {
        LifePlan plan = new LifePlan();
        plan.setId(1);
        plan.setPlanType("饮食");

        when(lifePlanService.getLatestByType(100, "饮食")).thenReturn(plan);

        mockMvc.perform(get("/api/plan/latest/100/饮食"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/plan/latest → 暂无方案")
    void getLatestNotFound() throws Exception {
        when(lifePlanService.getLatestByType(100, "饮食")).thenReturn(null);

        mockMvc.perform(get("/api/plan/latest/100/饮食"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("暂无方案"));
    }

    @Test
    @DisplayName("POST /api/plan/add → 方案生成成功")
    void addPlan() throws Exception {
        when(lifePlanService.add(any(LifePlan.class))).thenReturn(1);

        LifePlan newPlan = new LifePlan();
        newPlan.setUserId(100);
        newPlan.setPlanType("饮食");

        mockMvc.perform(post("/api/plan/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPlan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("DELETE /api/plan/delete/{id} → 删除成功")
    void deletePlan() throws Exception {
        when(lifePlanService.delete(1)).thenReturn(1);

        mockMvc.perform(delete("/api/plan/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
