package com.diabetes.controller;

import com.diabetes.entity.User;
import com.diabetes.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * DashboardController 单元测试 —— 目标覆盖率 100%
 */
@WebMvcTest(DashboardController.class)
@DisplayName("DashboardController 首页仪表盘控制器")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ConsultationService consultationService;

    @MockBean
    private HealthArticleService healthArticleService;

    @Test
    @DisplayName("GET /api/dashboard/patient/{userId} → 返回患者首页")
    void patientDashboard() throws Exception {
        User user = new User();
        user.setId(100);
        user.setName("张三");
        user.setStatus("正常");

        when(consultationService.listByUser(100)).thenReturn(List.of());
        when(healthArticleService.listAll("已发布")).thenReturn(List.of());
        when(userService.getById(100)).thenReturn(user);

        mockMvc.perform(get("/api/dashboard/patient/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userInfo.name").value("张三"));
    }

    @Test
    @DisplayName("GET /api/dashboard/admin → 返回管理端首页")
    void adminDashboard() throws Exception {
        when(userService.countActiveUsers()).thenReturn(200);
        when(consultationService.countByStatus("待回复")).thenReturn(8);
        when(healthArticleService.listAll(null)).thenReturn(List.of());
        when(userService.userTrend(anyString())).thenReturn(List.of());
        when(healthArticleService.countByCategory()).thenReturn(List.of());

        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalUsers").value(200))
                .andExpect(jsonPath("$.data.pendingConsultations").value(8));
    }
}
