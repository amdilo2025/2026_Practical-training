package com.diabetes.controller;

import com.diabetes.entity.Consultation;
import com.diabetes.service.ConsultationService;
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
 * ConsultationController 单元测试 —— 目标覆盖率 100%
 */
@WebMvcTest(ConsultationController.class)
@DisplayName("ConsultationController 咨询控制器")
class ConsultationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConsultationService consultationService;

    @Test
    @DisplayName("GET /api/consultation/listByUser/{userId} → 返回用户咨询列表")
    void listByUser() throws Exception {
        when(consultationService.listByUser(100)).thenReturn(List.of());

        mockMvc.perform(get("/api/consultation/listByUser/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/consultation/get/{id} → 返回咨询详情")
    void getById() throws Exception {
        Consultation consultation = new Consultation();
        consultation.setId(1);
        consultation.setContent("咨询内容");

        when(consultationService.getById(1)).thenReturn(consultation);

        mockMvc.perform(get("/api/consultation/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("咨询内容"));
    }

    @Test
    @DisplayName("POST /api/consultation/add → 提交咨询成功")
    void addConsultation() throws Exception {
        when(consultationService.add(any(Consultation.class))).thenReturn(1);

        Consultation consultation = new Consultation();
        consultation.setUserId(100);
        consultation.setDoctorId(10);
        consultation.setContent("血糖偏高怎么办");

        mockMvc.perform(post("/api/consultation/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consultation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("PUT /api/consultation/reply → 回复咨询成功")
    void replyConsultation() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1);
        params.put("reply", "建议控制饮食");

        when(consultationService.reply(eq(1), eq("建议控制饮食"))).thenReturn(1);

        mockMvc.perform(put("/api/consultation/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/consultation/listAll → 返回所有咨询")
    void listAll() throws Exception {
        when(consultationService.listAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/consultation/listAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
