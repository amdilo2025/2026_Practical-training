package com.diabetes.controller;

import com.diabetes.entity.Doctor;
import com.diabetes.service.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * DoctorController 单元测试 —— 目标覆盖率 100%
 */
@WebMvcTest(DoctorController.class)
@DisplayName("DoctorController 医生控制器")
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorService doctorService;

    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        testDoctor = new Doctor();
        testDoctor.setId(1);
        testDoctor.setName("赵晓峰");
        testDoctor.setDepartment("内分泌科");
        testDoctor.setTitle("主任医师");
        testDoctor.setIsAi(1);
    }

    @Test
    @DisplayName("GET /api/doctor/list → 返回医生列表")
    void listDoctors() throws Exception {
        when(doctorService.listAll()).thenReturn(List.of(testDoctor));

        mockMvc.perform(get("/api/doctor/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("赵晓峰"));
    }

    @Test
    @DisplayName("GET /api/doctor/get/{id} → 返回医生")
    void getDoctorById() throws Exception {
        when(doctorService.getById(1)).thenReturn(testDoctor);

        mockMvc.perform(get("/api/doctor/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("赵晓峰"));
    }

    @Test
    @DisplayName("GET /api/doctor/get/{id} 不存在 → 返回错误")
    void getDoctorByIdNotFound() throws Exception {
        when(doctorService.getById(999)).thenReturn(null);

        mockMvc.perform(get("/api/doctor/get/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("医师不存在"));
    }

    @Test
    @DisplayName("POST /api/doctor/add → 添加成功")
    void addDoctor() throws Exception {
        when(doctorService.add(any(Doctor.class))).thenReturn(1);

        mockMvc.perform(post("/api/doctor/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDoctor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("PUT /api/doctor/update → 更新成功")
    void updateDoctor() throws Exception {
        when(doctorService.update(any(Doctor.class))).thenReturn(1);

        mockMvc.perform(put("/api/doctor/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDoctor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("DELETE /api/doctor/delete/{id} → 删除成功")
    void deleteDoctor() throws Exception {
        when(doctorService.delete(1)).thenReturn(1);

        mockMvc.perform(delete("/api/doctor/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
