package com.diabetes.controller;

import com.diabetes.entity.ChatMessage;
import com.diabetes.entity.dto.DifyChatDTO;
import com.diabetes.service.ChatMessageService;
import com.diabetes.utils.DifyClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ChatController 单元测试 —— 目标覆盖率 100%
 */
@WebMvcTest(ChatController.class)
@DisplayName("ChatController 聊天控制器")
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatMessageService chatMessageService;

    @MockBean
    private DifyClient difyClient;

    @Test
    @DisplayName("POST /api/chat/send 新会话 → 返回 sessionId 和 AI 回复")
    void sendNewSession() throws Exception {
        DifyChatDTO dto = new DifyChatDTO();
        dto.setUserId(100);
        dto.setQuery("糖尿病饮食建议");
        dto.setSessionId(null); // 新会话

        ChatMessage savedUserMsg = new ChatMessage();
        savedUserMsg.setId(1);

        ChatMessage savedAiMsg = new ChatMessage();
        savedAiMsg.setId(2);

        Map<String, String> aiResult = new HashMap<>();
        aiResult.put("answer", "建议控制总热量摄入...");
        aiResult.put("conversation_id", "conv-real-uuid");

        when(chatMessageService.save(eq(100), eq("user"), anyString(), anyString()))
                .thenReturn(savedUserMsg);
        when(difyClient.chat(eq("糖尿病饮食建议"), eq("100"), eq("new")))
                .thenReturn(aiResult);
        when(chatMessageService.save(eq(100), eq("ai"), anyString(), anyString()))
                .thenReturn(savedAiMsg);

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.reply").exists())
                .andExpect(jsonPath("$.data.sessionId").exists());
    }

    @Test
    @DisplayName("POST /api/chat/send 已有会话 → 继续对话")
    void sendExistingSession() throws Exception {
        DifyChatDTO dto = new DifyChatDTO();
        dto.setUserId(100);
        dto.setQuery("还有别的建议吗");
        dto.setSessionId("existing-session-id");

        Map<String, String> aiResult = new HashMap<>();
        aiResult.put("answer", "另外建议增加运动...");

        when(chatMessageService.save(eq(100), eq("user"), anyString(), anyString()))
                .thenReturn(new ChatMessage());
        when(difyClient.chat(eq("还有别的建议吗"), eq("100"), eq("existing-session-id")))
                .thenReturn(aiResult);
        when(chatMessageService.save(eq(100), eq("ai"), anyString(), anyString()))
                .thenReturn(new ChatMessage());

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /api/chat/send Dify 返回空 answer → 返回兜底文案")
    void sendEmptyAnswer() throws Exception {
        DifyChatDTO dto = new DifyChatDTO();
        dto.setUserId(100);
        dto.setQuery("测试");
        dto.setSessionId(null);

        // 不包含 "answer" key 时才触发 getOrDefault 兜底
        Map<String, String> aiResult = new HashMap<>();
        // aiResult 中没有 "answer" key，getOrDefault 返回兜底文案

        when(chatMessageService.save(eq(100), eq("user"), anyString(), anyString()))
                .thenReturn(new ChatMessage());
        when(difyClient.chat(anyString(), anyString(), anyString())).thenReturn(aiResult);
        when(chatMessageService.save(eq(100), eq("ai"), anyString(), anyString()))
                .thenReturn(new ChatMessage());

        mockMvc.perform(post("/api/chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reply").value("抱歉，暂时无法回答。"));
    }

    @Test
    @DisplayName("GET /api/chat/history/{userId}/{sessionId} → 返回聊天历史")
    void history() throws Exception {
        ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setRole("user");
        msg.setContent("你好");

        when(chatMessageService.listBySession(100, "session-001"))
                .thenReturn(List.of(msg));

        mockMvc.perform(get("/api/chat/history/100/session-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].content").value("你好"));
    }

    @Test
    @DisplayName("GET /api/chat/sessions/{userId} → 返回会话列表")
    void sessions() throws Exception {
        when(chatMessageService.listSessions(100))
                .thenReturn(List.of("session-001", "session-002"));

        mockMvc.perform(get("/api/chat/sessions/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0]").value("session-001"));
    }
}
