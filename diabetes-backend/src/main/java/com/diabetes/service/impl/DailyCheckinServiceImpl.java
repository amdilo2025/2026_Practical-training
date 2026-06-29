package com.diabetes.service.impl;

import com.diabetes.entity.DailyCheckin;
import com.diabetes.mapper.DailyCheckinMapper;
import com.diabetes.service.DailyCheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DailyCheckinServiceImpl implements DailyCheckinService {

    @Autowired
    private DailyCheckinMapper dailyCheckinMapper;

    @Override
    public List<Map<String, Object>> listByUserAndDate(Integer userId, String startDate) {
        return dailyCheckinMapper.selectByUserAndDate(userId, startDate);
    }

    @Override
    public boolean checkin(Integer userId, LocalDate date, String type, String content) {
        // 检查是否已打卡
        DailyCheckin existing = dailyCheckinMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DailyCheckin>()
                        .eq(DailyCheckin::getUserId, userId)
                        .eq(DailyCheckin::getCheckinDate, date)
                        .eq(DailyCheckin::getCheckinType, type)
        );
        if (existing != null) {
            return false; // 已打卡
        }
        DailyCheckin checkin = new DailyCheckin();
        checkin.setUserId(userId);
        checkin.setCheckinDate(date);
        checkin.setCheckinType(type);
        checkin.setStatus(1);
        checkin.setContent(content);
        dailyCheckinMapper.insert(checkin);
        return true;
    }

    @Override
    public Map<String, Object> stats(Integer userId, String startDate) {
        List<Map<String, Object>> statsList = dailyCheckinMapper.selectCheckinStats(userId, startDate);
        Map<String, Object> result = new HashMap<>();
        int totalItems = 0;
        int completedItems = 0;
        for (Map<String, Object> stat : statsList) {
            totalItems += ((Number) stat.get("total_count")).intValue();
            completedItems += ((Number) stat.get("completed_count")).intValue();
        }
        result.put("stats", statsList);
        result.put("totalItems", totalItems);
        result.put("completedItems", completedItems);
        result.put("completionRate", totalItems > 0 ? Math.round(completedItems * 100.0 / totalItems) : 0);
        result.put("activeDays", dailyCheckinMapper.countActiveDays(userId, startDate));
        return result;
    }

    @Override
    public List<Map<String, Object>> dailyTrend(Integer userId, String startDate) {
        return dailyCheckinMapper.selectDailyTrend(userId, startDate);
    }

    @Override
    public Integer countActiveDays(Integer userId, String startDate) {
        return dailyCheckinMapper.countActiveDays(userId, startDate);
    }
}
