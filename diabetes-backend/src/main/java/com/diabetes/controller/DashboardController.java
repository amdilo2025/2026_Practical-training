package com.diabetes.controller;

import com.diabetes.common.R;
import com.diabetes.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private HealthArticleService healthArticleService;

    @GetMapping("/patient/{userId}")
    public R<Map<String, Object>> patientDashboard(@PathVariable Integer userId) {
        Map<String, Object> data = new HashMap<>();
        // 今日打卡状态
        String today = LocalDate.now().toString();
        // 最近咨询
        data.put("recentConsultations", consultationService.listByUser(userId));
        // 推荐资讯
        data.put("recommendedArticles", healthArticleService.listAll("已发布"));
        // 用户信息
        data.put("userInfo", userService.getById(userId));
        return R.success(data);
    }

    @GetMapping("/admin")
    public R<Map<String, Object>> adminDashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userService.countActiveUsers());
        data.put("pendingConsultations", consultationService.countByStatus("待回复"));
        data.put("totalArticles", healthArticleService.listAll(null).size());

        String monthStart = LocalDate.now().minusDays(30).toString();
        data.put("userTrend", userService.userTrend(monthStart));
        data.put("categoryStats", healthArticleService.countByCategory());
        return R.success(data);
    }
}
