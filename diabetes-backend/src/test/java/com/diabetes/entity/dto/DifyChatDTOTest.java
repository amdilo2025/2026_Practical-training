package com.diabetes.entity.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DifyChatDTO 单元测试 —— 目标覆盖率 100%
 */
@DisplayName("DifyChatDTO")
class DifyChatDTOTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        DifyChatDTO dto = new DifyChatDTO();

        dto.setUserId(1);
        dto.setQuery("糖尿病应该怎么饮食控制？");
        dto.setSessionId("abc-123-def");

        assertEquals(1, dto.getUserId());
        assertEquals("糖尿病应该怎么饮食控制？", dto.getQuery());
        assertEquals("abc-123-def", dto.getSessionId());
    }

    @Test
    @DisplayName("默认初始值为 null")
    void defaultValues_ShouldBeNull() {
        DifyChatDTO dto = new DifyChatDTO();

        assertNull(dto.getUserId());
        assertNull(dto.getQuery());
        assertNull(dto.getSessionId());
    }

    @Test
    @DisplayName("sessionId 为 null → 新会话场景")
    void nullSessionId_RepresentsNewSession() {
        DifyChatDTO dto = new DifyChatDTO();
        dto.setUserId(1);
        dto.setQuery("你好");
        // sessionId 不设置，保持 null

        assertNull(dto.getSessionId());
        assertEquals(1, dto.getUserId());
        assertEquals("你好", dto.getQuery());
    }

    @Test
    @DisplayName("空字符串 sessionId → 边界场景")
    void emptySessionId_ShouldWork() {
        DifyChatDTO dto = new DifyChatDTO();
        dto.setSessionId("");

        assertEquals("", dto.getSessionId());
    }
}
