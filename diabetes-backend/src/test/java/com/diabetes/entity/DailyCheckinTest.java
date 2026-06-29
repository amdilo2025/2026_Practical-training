package com.diabetes.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DailyCheckin 实体单元测试 —— 目标覆盖率 100%
 */
@DisplayName("DailyCheckin 实体")
class DailyCheckinTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        DailyCheckin checkin = new DailyCheckin();

        checkin.setId(1);
        checkin.setUserId(100);
        checkin.setCheckinDate(LocalDate.of(2026, 6, 28));
        checkin.setCheckinType("血糖");
        checkin.setStatus(1);
        checkin.setContent("空腹血糖 5.2");
        checkin.setRemark("感觉良好");

        LocalDateTime now = LocalDateTime.now();
        checkin.setCreatedTime(now);

        assertEquals(1, checkin.getId());
        assertEquals(100, checkin.getUserId());
        assertEquals(LocalDate.of(2026, 6, 28), checkin.getCheckinDate());
        assertEquals("血糖", checkin.getCheckinType());
        assertEquals(1, checkin.getStatus());
        assertEquals("空腹血糖 5.2", checkin.getContent());
        assertEquals("感觉良好", checkin.getRemark());
        assertEquals(now, checkin.getCreatedTime());
    }

    @Test
    @DisplayName("默认初始值为 null")
    void defaultValues_ShouldBeNull() {
        DailyCheckin checkin = new DailyCheckin();

        assertNull(checkin.getId());
        assertNull(checkin.getUserId());
        assertNull(checkin.getCheckinDate());
        assertNull(checkin.getCheckinType());
        assertNull(checkin.getStatus());
        assertNull(checkin.getContent());
        assertNull(checkin.getRemark());
        assertNull(checkin.getCreatedTime());
    }

    @Test
    @DisplayName("status = 0 → 未完成打卡")
    void incompleteStatus_ShouldWork() {
        DailyCheckin checkin = new DailyCheckin();
        checkin.setStatus(0);
        assertEquals(0, checkin.getStatus());
    }

    @Test
    @DisplayName("remark 为空字符串 → 合法")
    void emptyRemark_ShouldWork() {
        DailyCheckin checkin = new DailyCheckin();
        checkin.setRemark("");
        assertEquals("", checkin.getRemark());
    }
}
