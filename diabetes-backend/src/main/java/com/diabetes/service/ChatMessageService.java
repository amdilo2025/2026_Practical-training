package com.diabetes.service;

import com.diabetes.entity.ChatMessage;
import java.util.List;

public interface ChatMessageService {
    List<ChatMessage> listBySession(Integer userId, String sessionId);
    ChatMessage save(Integer userId, String role, String content, String sessionId);
    List<String> listSessions(Integer userId);
}
