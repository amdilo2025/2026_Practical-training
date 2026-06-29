package com.diabetes.controller;

import com.diabetes.entity.dto.CheckinDTO;
import com.diabetes.service.DailyCheckinService;
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
 * DailyCheckinController 单元测试 —— 目标覆盖率 100%
 */
@WebMvcTest(DailyCheckinController.class)
@DisplayName("DailyCheckinController 打卡控制器")
class DailyCheckinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DailyCheckinService dailyCheckinService;

    @Test
    @DisplayName("GET /api/checkin/list/{userId} → 返回打卡列表")
    void listCheckins() throws Exception {
        List<Map<String, Object>> list = List.of(
                Map.of("checkin_date", "2026-06-28", "checkin_type", "血糖")
        );
        when(dailyCheckinService.listByUserAndDate(eq(100), anyString())).thenReturn(list);

        mockMvc.perform(get("/api/checkin/list/100").param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /api/checkin/do 首次打卡 → 打卡成功")
    void doCheckinSuccess() throws Exception {
        CheckinDTO dto = new CheckinDTO();
        dto.setUserId(100);
        dto.setCheckinDate("2026-06-28");
        dto.setCheckinType("血糖");
        dto.setContent("空腹血糖5.2");

        when(dailyCheckinService.checkin(eq(100), any(), eq("血糖"), eq("空腹血糖5.2")))
                .thenReturn(true);

        mockMvc.perform(post("/api/checkin/do")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /api/checkin/do 重复打卡 → 打卡失败")
    void doCheckinDuplicate() throws Exception {
        CheckinDTO dto = new CheckinDTO();
        dto.setUserId(100);
        dto.setCheckinDate("2026-06-28");
        dto.setCheckinType("血糖");
        dto.setContent("重复打卡");

        when(dailyCheckinService.checkin(eq(100), any(), eq("血糖"), eq("重复打卡")))
                .thenReturn(false);

        mockMvc.perform(post("/api/checkin/do")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("今日已打卡"));
    }

    @Test
    @DisplayName("GET /api/checkin/stats/{userId} → 返回打卡统计")
    void checkinStats() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalItems", 30);
        stats.put("completedItems", 25);
        stats.put("completionRate", 83L);

        when(dailyCheckinService.stats(eq(100), anyString())).thenReturn(stats);

        mockMvc.perform(get("/api/checkin/stats/100").param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalItems").value(30));
    }

    @Test
    @DisplayName("GET /api/checkin/trend/{userId} → 返回每日趋势")
    void checkinTrend() throws Exception {
        when(dailyCheckinService.dailyTrend(eq(100), anyString())).thenReturn(List.of());

        mockMvc.perform(get("/api/checkin/trend/100").param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
