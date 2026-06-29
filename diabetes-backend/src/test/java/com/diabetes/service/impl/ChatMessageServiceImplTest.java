package com.diabetes.service.impl;

import com.diabetes.entity.ChatMessage;
import com.diabetes.mapper.ChatMessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ChatMessageServiceImpl 单元测试 —— 目标覆盖率 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChatMessageServiceImpl 聊天消息服务")
class ChatMessageServiceImplTest {

    @Mock
    private ChatMessageMapper chatMessageMapper;

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    private ChatMessage testMessage;

    @BeforeEach
    void setUp() {
        testMessage = new ChatMessage();
        testMessage.setId(1);
        testMessage.setUserId(100);
        testMessage.setRole("user");
        testMessage.setContent("你好");
        testMessage.setSessionId("session-001");
    }

    @Test
    @DisplayName("listBySession → 返回该会话消息列表")
    void listBySession_ShouldReturnMessages() {
        when(chatMessageMapper.findBySession(100, "session-001"))
                .thenReturn(List.of(testMessage));

        List<ChatMessage> result = chatMessageService.listBySession(100, "session-001");

        assertEquals(1, result.size());
        assertEquals("user", result.get(0).getRole());
        verify(chatMessageMapper).findBySession(100, "session-001");
    }

    @Test
    @DisplayName("listBySession 无消息 → 返回空列表")
    void listBySession_Empty_ShouldReturnEmptyList() {
        when(chatMessageMapper.findBySession(100, "session-empty"))
                .thenReturn(List.of());

        List<ChatMessage> result = chatMessageService.listBySession(100, "session-empty");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("save → 保存消息并返回带 ID 的 ChatMessage")
    void save_ShouldInsertAndReturn() {
        when(chatMessageMapper.insert(any(ChatMessage.class))).thenReturn(1);

        ChatMessage result = chatMessageService.save(100, "ai", "糖尿病建议...", "session-002");

        assertNotNull(result);
        assertEquals(100, result.getUserId());
        assertEquals("ai", result.getRole());
        assertEquals("糖尿病建议...", result.getContent());
        assertEquals("session-002", result.getSessionId());
        verify(chatMessageMapper).insert(any(ChatMessage.class));
    }

    @Test
    @DisplayName("listSessions → 返回用户会话列表")
    void listSessions_ShouldReturnSessionIds() {
        when(chatMessageMapper.findSessionsByUser(100))
                .thenReturn(List.of("session-001", "session-002"));

        List<String> result = chatMessageService.listSessions(100);

        assertEquals(2, result.size());
        assertTrue(result.contains("session-001"));
        assertTrue(result.contains("session-002"));
        verify(chatMessageMapper).findSessionsByUser(100);
    }

    @Test
    @DisplayName("listSessions 无会话 → 返回空列表")
    void listSessions_Empty_ShouldReturnEmptyList() {
        when(chatMessageMapper.findSessionsByUser(999))
                .thenReturn(List.of());

        List<String> result = chatMessageService.listSessions(999);
        assertTrue(result.isEmpty());
    }
}
