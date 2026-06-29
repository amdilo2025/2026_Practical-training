package com.diabetes.controller;

import com.diabetes.common.R;
import com.diabetes.mapper.HealthArticleMapper;
import com.diabetes.service.*;
import com.diabetes.utils.DifyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private DailyCheckinService dailyCheckinService;

    @Autowired
    private DifyClient difyClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HealthArticleMapper healthArticleMapper;

    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userService.countActiveUsers());
        data.put("pendingConsultations", consultationService.countByStatus("待回复"));
        data.put("totalConsultations", consultationService.countByStatus("已回复") + consultationService.countByStatus("待回复"));
        data.put("totalArticles", healthArticleMapper.selectCount(null));

        String startDate = LocalDate.now().minusDays(7).toString();
        data.put("activeDays7", 0);

        String monthStart = LocalDate.now().minusDays(30).toString();
        data.put("userTrend", userService.userTrend(monthStart));

        return R.success(data);
    }

    @GetMapping("/statistics")
    public R<Map<String, Object>> statistics() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userService.countActiveUsers());

        String monthStart = LocalDate.now().minusDays(30).toString();
        data.put("userTrend", userService.userTrend(monthStart));
        Map<String, Object> consultationStats = new HashMap<>();
        consultationStats.put("pending", consultationService.countByStatus("待回复"));
        consultationStats.put("replied", consultationService.countByStatus("已回复"));
        data.put("consultationStats", consultationStats);
        return R.success(data);
    }

    /**
     * 执行 SQL 查询（仅允许 SELECT）
     * 供 Dify 的 database workflowadmin 子工作流调用
     */
    @PostMapping("/execute")
    public R<Map<String, Object>> executeSql(@RequestBody Map<String, String> body) {
        String sql = body.get("sql");
        if (sql == null || sql.trim().isEmpty()) {
            return R.error("SQL语句不能为空");
        }

        String trimmed = sql.trim();
        String upper = trimmed.substring(0, Math.min(20, trimmed.length())).toUpperCase();

        if (!upper.startsWith("SELECT") && !upper.startsWith("SHOW") && !upper.startsWith("DESC")) {
            return R.error("仅允许执行 SELECT/SHOW/DESC 查询语句");
        }

        if (upper.contains("DROP") || upper.contains("DELETE") || upper.contains("UPDATE")
                || upper.contains("INSERT") || upper.contains("ALTER") || upper.contains("TRUNCATE")) {
            return R.error("禁止执行修改类SQL语句");
        }

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(trimmed);
            List<String> columns = rows.isEmpty() ? new ArrayList<>() : new ArrayList<>(rows.get(0).keySet());
            Map<String, Object> result = new HashMap<>();
            result.put("columns", columns);
            result.put("rows", rows);
            result.put("total", rows.size());
            return R.success(result);
        } catch (Exception e) {
            return R.error("SQL执行失败: " + e.getMessage());
        }
    }

    /**
     * 自然语言数据查询
     * 接收用户输入的自然语言，转发给 Dify 数据管理工作流执行 Text2SQL 并返回结果
     */
    @PostMapping("/data-query")
    public R<Map<String, Object>> dataQuery(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        if (query == null || query.trim().isEmpty()) {
            return R.error("查询内容不能为空");
        }

        String userId = body.getOrDefault("userId", "admin");

        try {
            Map<String, Object> result = difyClient.runDataQueryWorkflow(query, userId);
            return R.success(result);
        } catch (Exception e) {
            return R.error("数据查询失败: " + e.getMessage());
        }
    }
}
