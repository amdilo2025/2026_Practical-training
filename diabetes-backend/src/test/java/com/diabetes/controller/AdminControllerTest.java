package com.diabetes.controller;

import com.diabetes.service.ConsultationService;
import com.diabetes.service.UserService;
import com.diabetes.utils.DifyClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AdminController 单元测试 —— 目标覆盖率 100%
 */
@WebMvcTest(AdminController.class)
@DisplayName("AdminController 管理员控制器")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private ConsultationService consultationService;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private DifyClient difyClient;

    // ==================== dashboard ====================

    @Test
    @DisplayName("GET /api/admin/dashboard → 返回仪表盘数据")
    void dashboard() throws Exception {
        when(userService.countActiveUsers()).thenReturn(150);
        when(consultationService.countByStatus("待回复")).thenReturn(5);
        when(consultationService.countByStatus("已回复")).thenReturn(45);
        when(userService.userTrend(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalUsers").value(150))
                .andExpect(jsonPath("$.data.pendingConsultations").value(5))
                .andExpect(jsonPath("$.data.totalConsultations").value(50));
    }

    // ==================== statistics ====================

    @Test
    @DisplayName("GET /api/admin/statistics → 返回统计数据")
    void statistics() throws Exception {
        when(userService.countActiveUsers()).thenReturn(150);
        when(userService.userTrend(anyString())).thenReturn(List.of());
        when(consultationService.countByStatus("待回复")).thenReturn(5);
        when(consultationService.countByStatus("已回复")).thenReturn(45);

        mockMvc.perform(get("/api/admin/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalUsers").value(150))
                .andExpect(jsonPath("$.data.consultationStats.pending").value(5));
    }

    // ==================== execute SQL ====================

    @Nested
    @DisplayName("POST /api/admin/execute SQL 执行")
    class SqlExecuteTests {

        @Test
        @DisplayName("SQL 为 null → 返回错误")
        void nullSql() throws Exception {
            Map<String, String> body = new HashMap<>();
            body.put("sql", null);

            mockMvc.perform(post("/api/admin/execute")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0].error").value("SQL语句不能为空"));
        }

        @Test
        @DisplayName("SQL 空白 → 返回错误")
        void blankSql() throws Exception {
            Map<String, String> body = new HashMap<>();
            body.put("sql", "   ");

            mockMvc.perform(post("/api/admin/execute")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0].error").value("SQL语句不能为空"));
        }

        @Test
        @DisplayName("非 SELECT/SHOW/DESCRIBE → 返回仅允许 SELECT")
        void nonSelectSql() throws Exception {
            Map<String, String> body = new HashMap<>();
            body.put("sql", "DELETE FROM user");

            mockMvc.perform(post("/api/admin/execute")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0].error").value("仅允许执行SELECT查询语句"));
        }

        @Test
        @DisplayName("合法的 SELECT → 返回查询结果")
        void selectSql() throws Exception {
            Map<String, String> body = new HashMap<>();
            body.put("sql", "SELECT COUNT(*) as cnt FROM user");

            List<Map<String, Object>> rows = List.of(Map.of("cnt", 150L));
            when(jdbcTemplate.queryForList(eq("SELECT COUNT(*) as cnt FROM user")))
                    .thenReturn(rows);

            mockMvc.perform(post("/api/admin/execute")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0].cnt").value(150));
        }

        @Test
        @DisplayName("SQL 带 markdown 代码块标记 → 自动清理后执行")
        void sqlWithMarkdownBlock() throws Exception {
            Map<String, String> body = new HashMap<>();
            body.put("sql", "```sql\nSELECT 1 as val\n```");

            // 修复 Bug: controller 现在正确使用 cleanSql，故 mock 清理后的 SQL
            List<Map<String, Object>> rows = List.of(Map.of("val", 1));
            when(jdbcTemplate.queryForList(eq("SELECT 1 as val"))).thenReturn(rows);

            mockMvc.perform(post("/api/admin/execute")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0].val").value(1));
        }

        @Test
        @DisplayName("SHOW 语句 → 允许执行")
        void showSql() throws Exception {
            Map<String, String> body = new HashMap<>();
            body.put("sql", "SHOW TABLES");

            when(jdbcTemplate.queryForList(eq("SHOW TABLES"))).thenReturn(List.of());

            mockMvc.perform(post("/api/admin/execute")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk());
        }
    }

    // ==================== AI 查询 ====================

    @Nested
    @DisplayName("POST /api/admin/ai/query")
    class AiQueryTests {

        @Test
        @DisplayName("question 为 null → 返回错误")
        void nullQuestion() throws Exception {
            Map<String, String> body = new HashMap<>();
            body.put("question", null);

            mockMvc.perform(post("/api/admin/ai/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("查询内容不能为空"));
        }

        @Test
        @DisplayName("正常查询 → 返回 AI 结果")
        void aiQuerySuccess() throws Exception {
            Map<String, String> body = new HashMap<>();
            body.put("question", "最近一周新增用户数");

            Map<String, Object> outputs = new HashMap<>();
            outputs.put("body", "最近一周新增用户：25人");

            when(difyClient.runAdminWorkflow(eq("最近一周新增用户数"), eq("admin-user")))
                    .thenReturn(outputs);

            mockMvc.perform(post("/api/admin/ai/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("AI 查询异常 → 返回错误")
        void aiQueryException() throws Exception {
            Map<String, String> body = new HashMap<>();
            body.put("question", "查询");

            when(difyClient.runAdminWorkflow(anyString(), anyString()))
                    .thenThrow(new RuntimeException("API 不可用"));

            mockMvc.perform(post("/api/admin/ai/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("智能查询失败: API 不可用"));
        }
    }
}
