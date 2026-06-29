package com.diabetes.service.impl;

import com.diabetes.entity.DailyCheckin;
import com.diabetes.mapper.DailyCheckinMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DailyCheckinServiceImpl 单元测试 —— 目标覆盖率 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DailyCheckinServiceImpl 打卡服务")
class DailyCheckinServiceImplTest {

    @Mock
    private DailyCheckinMapper dailyCheckinMapper;

    @InjectMocks
    private DailyCheckinServiceImpl dailyCheckinService;

    private static final Integer TEST_USER_ID = 100;
    private static final String TEST_START_DATE = "2026-06-01";

    @Test
    @DisplayName("listByUserAndDate → 返回打卡列表")
    void listByUserAndDate_ShouldReturnCheckins() {
        List<Map<String, Object>> checkins = List.of(
                Map.of("checkin_date", "2026-06-28", "checkin_type", "血糖")
        );
        when(dailyCheckinMapper.selectByUserAndDate(TEST_USER_ID, TEST_START_DATE))
                .thenReturn(checkins);

        List<Map<String, Object>> result = dailyCheckinService.listByUserAndDate(TEST_USER_ID, TEST_START_DATE);

        assertEquals(1, result.size());
        assertEquals("血糖", result.get(0).get("checkin_type"));
    }

    @Test
    @DisplayName("listByUserAndDate 无数据 → 返回空列表")
    void listByUserAndDate_Empty_ShouldReturnEmptyList() {
        when(dailyCheckinMapper.selectByUserAndDate(TEST_USER_ID, TEST_START_DATE))
                .thenReturn(List.of());

        List<Map<String, Object>> result = dailyCheckinService.listByUserAndDate(TEST_USER_ID, TEST_START_DATE);
        assertTrue(result.isEmpty());
    }

    // ==================== checkin ====================

    @Nested
    @DisplayName("checkin 打卡操作")
    class CheckinTests {

        @Test
        @DisplayName("checkin 首次打卡 → 插入记录并返回 true")
        void checkin_FirstTime_ShouldInsertAndReturnTrue() {
            when(dailyCheckinMapper.selectOne(any())).thenReturn(null);
            when(dailyCheckinMapper.insert(any(DailyCheckin.class))).thenReturn(1);

            boolean result = dailyCheckinService.checkin(TEST_USER_ID,
                    LocalDate.of(2026, 6, 28), "血糖", "空腹血糖5.2");

            assertTrue(result);
            verify(dailyCheckinMapper).insert(any(DailyCheckin.class));
        }

        @Test
        @DisplayName("checkin 重复打卡 → 返回 false")
        void checkin_Duplicate_ShouldReturnFalse() {
            DailyCheckin existing = new DailyCheckin();
            existing.setId(1);
            when(dailyCheckinMapper.selectOne(any())).thenReturn(existing);

            boolean result = dailyCheckinService.checkin(TEST_USER_ID,
                    LocalDate.of(2026, 6, 28), "血糖", "空腹血糖5.2");

            assertFalse(result);
            verify(dailyCheckinMapper, never()).insert(any(DailyCheckin.class));
        }

        @Test
        @DisplayName("checkin 不同类型同一天 → 视为不同打卡项")
        void checkin_DifferentTypeSameDay_ShouldInsert() {
            // 第一次查询（血糖）不存在
            when(dailyCheckinMapper.selectOne(any())).thenReturn(null);
            when(dailyCheckinMapper.insert(any(DailyCheckin.class))).thenReturn(1);

            boolean result = dailyCheckinService.checkin(TEST_USER_ID,
                    LocalDate.of(2026, 6, 28), "运动", "慢跑30分钟");

            assertTrue(result);
        }
    }

    // ==================== stats ====================

    @Nested
    @DisplayName("stats 打卡统计")
    class StatsTests {

        @Test
        @DisplayName("stats 有数据 → 计算 totalItems, completedItems, completionRate")
        void stats_WithData_ShouldCalculateCorrectly() {
            List<Map<String, Object>> statsList = new ArrayList<>();
            Map<String, Object> type1 = new HashMap<>();
            type1.put("checkin_type", "血糖");
            type1.put("total_count", 10L);
            type1.put("completed_count", 8L);
            statsList.add(type1);

            Map<String, Object> type2 = new HashMap<>();
            type2.put("checkin_type", "运动");
            type2.put("total_count", 5L);
            type2.put("completed_count", 3L);
            statsList.add(type2);

            when(dailyCheckinMapper.selectCheckinStats(TEST_USER_ID, TEST_START_DATE))
                    .thenReturn(statsList);
            when(dailyCheckinMapper.countActiveDays(TEST_USER_ID, TEST_START_DATE))
                    .thenReturn(15);

            Map<String, Object> result = dailyCheckinService.stats(TEST_USER_ID, TEST_START_DATE);

            assertEquals(15, result.get("totalItems"));
            assertEquals(11, result.get("completedItems"));
            assertEquals(73L, result.get("completionRate"));  // 11/15*100=73
            assertEquals(15, result.get("activeDays"));
        }

        @Test
        @DisplayName("stats totalItems=0 → completionRate=0 避免除零")
        void stats_NoItems_ShouldReturnZeroRate() {
            when(dailyCheckinMapper.selectCheckinStats(TEST_USER_ID, TEST_START_DATE))
                    .thenReturn(List.of());
            when(dailyCheckinMapper.countActiveDays(TEST_USER_ID, TEST_START_DATE))
                    .thenReturn(0);

            Map<String, Object> result = dailyCheckinService.stats(TEST_USER_ID, TEST_START_DATE);

            assertEquals(0, result.get("totalItems"));
            assertEquals(0, result.get("completedItems"));
            assertEquals(0L, result.get("completionRate"));
        }
    }

    // ==================== dailyTrend ====================

    @Test
    @DisplayName("dailyTrend → 返回每日趋势数据")
    void dailyTrend_ShouldReturnTrendData() {
        List<Map<String, Object>> trend = List.of(
                Map.of("checkin_date", "2026-06-28", "items", 3L, "completed", 3L)
        );
        when(dailyCheckinMapper.selectDailyTrend(TEST_USER_ID, TEST_START_DATE))
                .thenReturn(trend);

        List<Map<String, Object>> result = dailyCheckinService.dailyTrend(TEST_USER_ID, TEST_START_DATE);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).get("items"));
    }

    // ==================== countActiveDays ====================

    @Test
    @DisplayName("countActiveDays → 返回活跃天数")
    void countActiveDays_ShouldReturnCount() {
        when(dailyCheckinMapper.countActiveDays(TEST_USER_ID, TEST_START_DATE))
                .thenReturn(20);

        Integer activeDays = dailyCheckinService.countActiveDays(TEST_USER_ID, TEST_START_DATE);
        assertEquals(20, activeDays);
    }
}
