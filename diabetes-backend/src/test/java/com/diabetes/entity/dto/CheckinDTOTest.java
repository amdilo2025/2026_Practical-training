package com.diabetes.entity.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CheckinDTO 单元测试 —— 目标覆盖率 100%
 */
@DisplayName("CheckinDTO")
class CheckinDTOTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        CheckinDTO dto = new CheckinDTO();

        dto.setUserId(1);
        dto.setCheckinDate("2026-06-28");
        dto.setCheckinType("血糖");
        dto.setContent("空腹血糖 5.2");

        assertEquals(1, dto.getUserId());
        assertEquals("2026-06-28", dto.getCheckinDate());
        assertEquals("血糖", dto.getCheckinType());
        assertEquals("空腹血糖 5.2", dto.getContent());
    }

    @Test
    @DisplayName("默认初始值为 null")
    void defaultValues_ShouldBeNull() {
        CheckinDTO dto = new CheckinDTO();

        assertNull(dto.getUserId());
        assertNull(dto.getCheckinDate());
        assertNull(dto.getCheckinType());
        assertNull(dto.getContent());
    }

    @Test
    @DisplayName("checkinType = '运动' → 运动打卡场景")
    void exerciseCheckin_ShouldWork() {
        CheckinDTO dto = new CheckinDTO();
        dto.setUserId(100);
        dto.setCheckinDate("2026-06-28");
        dto.setCheckinType("运动");
        dto.setContent("慢跑30分钟");

        assertEquals("运动", dto.getCheckinType());
        assertEquals("慢跑30分钟", dto.getContent());
    }

    @Test
    @DisplayName("checkinType = '用药' → 用药打卡场景")
    void medicationCheckin_ShouldWork() {
        CheckinDTO dto = new CheckinDTO();
        dto.setUserId(100);
        dto.setCheckinDate("2026-06-28");
        dto.setCheckinType("用药");
        dto.setContent("二甲双胍 500mg");

        assertEquals("用药", dto.getCheckinType());
    }
}
