package com.diabetes.controller;

import com.diabetes.common.R;
import com.diabetes.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private DailyCheckinService dailyCheckinService;

    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userService.countActiveUsers());
        data.put("pendingConsultations", consultationService.countByStatus("待回复"));
        data.put("totalConsultations", consultationService.countByStatus("已回复") + consultationService.countByStatus("待回复"));

        // 近7天打卡活跃用户数
        String startDate = LocalDate.now().minusDays(7).toString();
        data.put("activeDays7", 0); // 简化处理

        // 用户增长趋势（近30天）
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
}
