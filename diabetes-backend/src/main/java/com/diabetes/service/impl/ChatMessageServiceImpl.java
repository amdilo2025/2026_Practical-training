package com.diabetes.service.impl;

import com.diabetes.entity.ChatMessage;
import com.diabetes.mapper.ChatMessageMapper;
import com.diabetes.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    public List<ChatMessage> listBySession(Integer userId, String sessionId) {
        return chatMessageMapper.findBySession(userId, sessionId);
    }

    @Override
    public ChatMessage save(Integer userId, String role, String content, String sessionId) {
        ChatMessage msg = new ChatMessage();
        msg.setUserId(userId);
        msg.setRole(role);
        msg.setContent(content);
        msg.setSessionId(sessionId);
        chatMessageMapper.insert(msg);
        return msg;
    }

    @Override
    public List<String> listSessions(Integer userId) {
        return chatMessageMapper.findSessionsByUser(userId);
    }
}
