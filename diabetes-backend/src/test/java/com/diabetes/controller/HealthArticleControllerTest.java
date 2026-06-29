package com.diabetes.controller;

import com.diabetes.entity.HealthArticle;
import com.diabetes.service.HealthArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
 * HealthArticleController 单元测试 —— 目标覆盖率 100%
 */
@WebMvcTest(HealthArticleController.class)
@DisplayName("HealthArticleController 健康资讯控制器")
class HealthArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HealthArticleService healthArticleService;

    private HealthArticle testArticle;

    @BeforeEach
    void setUp() {
        testArticle = new HealthArticle();
        testArticle.setId(1);
        testArticle.setTitle("糖尿病饮食指南");
        testArticle.setCategory("饮食");
        testArticle.setStatus("已发布");
        testArticle.setViews(100);
    }

    @Test
    @DisplayName("GET /api/article/list → 返回文章列表")
    void listArticles() throws Exception {
        when(healthArticleService.listAll(null)).thenReturn(List.of(testArticle));

        mockMvc.perform(get("/api/article/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("糖尿病饮食指南"));
    }

    @Test
    @DisplayName("GET /api/article/listPublished → 只返回已发布文章")
    void listPublished() throws Exception {
        when(healthArticleService.listAll("已发布")).thenReturn(List.of(testArticle));

        mockMvc.perform(get("/api/article/listPublished"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/article/get/{id} → 返回文章并增加浏览量")
    void getById() throws Exception {
        when(healthArticleService.getById(1)).thenReturn(testArticle);
        when(healthArticleService.incrementViews(1)).thenReturn(1);

        mockMvc.perform(get("/api/article/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("糖尿病饮食指南"));
    }

    @Test
    @DisplayName("GET /api/article/get/{id} 不存在 → 返回错误")
    void getByIdNotFound() throws Exception {
        when(healthArticleService.getById(999)).thenReturn(null);

        mockMvc.perform(get("/api/article/get/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("文章不存在"));
    }

    @Test
    @DisplayName("GET /api/article/category/{category} → 按分类返回")
    void findByCategory() throws Exception {
        when(healthArticleService.findByCategory("饮食")).thenReturn(List.of(testArticle));

        mockMvc.perform(get("/api/article/category/饮食"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].category").value("饮食"));
    }

    @Test
    @DisplayName("GET /api/article/countByCategory → 返回分类统计")
    void countByCategory() throws Exception {
        when(healthArticleService.countByCategory()).thenReturn(List.of());

        mockMvc.perform(get("/api/article/countByCategory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /api/article/add → 发布成功")
    void addArticle() throws Exception {
        when(healthArticleService.add(any(HealthArticle.class))).thenReturn(1);

        mockMvc.perform(post("/api/article/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testArticle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("PUT /api/article/update → 更新成功")
    void updateArticle() throws Exception {
        when(healthArticleService.update(any(HealthArticle.class))).thenReturn(1);

        mockMvc.perform(put("/api/article/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testArticle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("DELETE /api/article/delete/{id} → 删除成功")
    void deleteArticle() throws Exception {
        when(healthArticleService.delete(1)).thenReturn(1);

        mockMvc.perform(delete("/api/article/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
