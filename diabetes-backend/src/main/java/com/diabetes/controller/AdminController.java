package com.diabetes.controller;

import com.diabetes.common.R;
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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DifyClient difyClient;

    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userService.countActiveUsers());
        data.put("pendingConsultations", consultationService.countByStatus("待回复"));
        data.put("totalConsultations", consultationService.countByStatus("已回复") + consultationService.countByStatus("待回复"));

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

    @PostMapping("/execute")
    public Map<String, Object> execute(@RequestBody Map<String, String> body) {
        String sql = body.get("sql");
        Map<String, Object> result = new HashMap<>();

        if (sql == null || sql.isBlank()) {
            result.put("result", List.of(Map.of("error", "SQL语句不能为空")));
            return result;
        }

        // 清理 markdown 代码块标记和空白
        String cleanSql = sql.trim();
        if (cleanSql.startsWith("```sql")) {
            cleanSql = cleanSql.substring(6);
        } else if (cleanSql.startsWith("```")) {
            cleanSql = cleanSql.substring(3);
        }
        if (cleanSql.endsWith("```")) {
            cleanSql = cleanSql.substring(0, cleanSql.length() - 3);
        }
        cleanSql = cleanSql.trim();
        String trimmedSql = cleanSql.toUpperCase();
        if (!trimmedSql.startsWith("SELECT") && !trimmedSql.startsWith("SHOW") && !trimmedSql.startsWith("DESCRIBE")) {
            result.put("result", List.of(Map.of("error", "仅允许执行SELECT查询语句")));
            return result;
        }

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            result.put("result", rows);
        } catch (Exception e) {
            result.put("result", List.of(Map.of("error", "SQL执行失败: " + e.getMessage())));
        }
        return result;
    }

    @PostMapping("/ai/query")
    public R<?> aiQuery(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        if (question == null || question.isBlank()) {
            return R.error("查询内容不能为空");
        }
        try {
            Map<String, Object> outputs = difyClient.runAdminWorkflow(question, "admin-user");
            Object bodyObj = outputs.get("body");
            return R.success(bodyObj);
        } catch (Exception e) {
            return R.error("智能查询失败: " + e.getMessage());
        }
    }
}
