package com.diabetes.controller;

import com.diabetes.common.R;
import com.diabetes.entity.ChatMessage;
import com.diabetes.entity.dto.DifyChatDTO;
import com.diabetes.service.ChatMessageService;
import com.diabetes.utils.DifyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private DifyClient difyClient;

    @PostMapping("/send")
    public R<Map<String, Object>> send(@RequestBody DifyChatDTO dto) {
        String sessionId = dto.getSessionId();
        boolean isNew = (sessionId == null || sessionId.isEmpty());
        if (isNew) {
            sessionId = UUID.randomUUID().toString();
        }

        // 保存用户消息
        chatMessageService.save(dto.getUserId(), "user", dto.getQuery(), sessionId);

        // 调用Dify获取AI回复（新对话传"new"，已有对话传conversation_id）
        String convId = isNew ? "new" : sessionId;
        Map<String, String> aiResult = difyClient.chat(dto.getQuery(), dto.getUserId().toString(), convId);

        String aiResponse = aiResult.getOrDefault("answer", "抱歉，暂时无法回答。");
        // 保存Dify返回的真实conversation_id（覆盖本地sessionId）
        String realConvId = aiResult.get("conversation_id");
        if (realConvId != null && !realConvId.isEmpty()) {
            sessionId = realConvId;
        }

        // 保存AI回复
        chatMessageService.save(dto.getUserId(), "ai", aiResponse, sessionId);

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("reply", aiResponse);
        return R.success(result);
    }

    @GetMapping("/history/{userId}/{sessionId}")
    public R<List<ChatMessage>> history(@PathVariable Integer userId,
                                        @PathVariable String sessionId) {
        return R.success(chatMessageService.listBySession(userId, sessionId));
    }

    @GetMapping("/sessions/{userId}")
    public R<List<String>> sessions(@PathVariable Integer userId) {
        return R.success(chatMessageService.listSessions(userId));
    }
}
