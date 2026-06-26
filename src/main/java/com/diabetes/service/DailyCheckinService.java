package com.diabetes.service;

import com.diabetes.entity.DailyCheckin;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DailyCheckinService {
    List<Map<String, Object>> listByUserAndDate(Integer userId, String startDate);
    boolean checkin(Integer userId, LocalDate date, String type, String content);
    Map<String, Object> stats(Integer userId, String startDate);
    List<Map<String, Object>> dailyTrend(Integer userId, String startDate);
    Integer countActiveDays(Integer userId, String startDate);
}
