package com.diabetes.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ChatMessage 实体单元测试 —— 目标覆盖率 100%
 */
@DisplayName("ChatMessage 实体")
class ChatMessageTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        ChatMessage message = new ChatMessage();

        message.setId(1);
        message.setUserId(100);
        message.setRole("user");
        message.setContent("糖尿病应该怎么控制饮食？");
        message.setSessionId("session-uuid-123");
        LocalDateTime now = LocalDateTime.now();
        message.setCreatedTime(now);

        assertEquals(1, message.getId());
        assertEquals(100, message.getUserId());
        assertEquals("user", message.getRole());
        assertEquals("糖尿病应该怎么控制饮食？", message.getContent());
        assertEquals("session-uuid-123", message.getSessionId());
        assertEquals(now, message.getCreatedTime());
    }

    @Test
    @DisplayName("默认初始值为 null")
    void defaultValues_ShouldBeNull() {
        ChatMessage message = new ChatMessage();

        assertNull(message.getId());
        assertNull(message.getUserId());
        assertNull(message.getRole());
        assertNull(message.getContent());
        assertNull(message.getSessionId());
        assertNull(message.getCreatedTime());
    }

    @Test
    @DisplayName("role = 'ai' → AI 回复消息")
    void aiRole_ShouldWork() {
        ChatMessage message = new ChatMessage();
        message.setRole("ai");
        message.setContent("建议您控制总热量摄入...");
        assertEquals("ai", message.getRole());
    }

    @Test
    @DisplayName("不同 sessionId → 区分不同会话")
    void differentSessionIds_ShouldBeDistinguishable() {
        ChatMessage msg1 = new ChatMessage();
        msg1.setSessionId("session-a");

        ChatMessage msg2 = new ChatMessage();
        msg2.setSessionId("session-b");

        assertNotEquals(msg1.getSessionId(), msg2.getSessionId());
    }
}
