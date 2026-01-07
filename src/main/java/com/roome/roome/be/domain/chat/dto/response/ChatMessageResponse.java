package com.roome.roome.be.domain.chat.dto.response;

import com.roome.roome.be.domain.chat.enums.ChatResponseType;

import java.util.List;

public record ChatMessageResponse(
        String sessionId,
        ChatResponseType type,   // QUESTION / RESULT
        String message,          // 봇 메시지
        List<String> options,    // 버튼 (2~4개)
        Object data              // 결과 payload (products or references)
) {

    public static ChatMessageResponse question(String sessionId, String message, List<String> options) {
        return new ChatMessageResponse(sessionId, ChatResponseType.QUESTION, message, options, null);
    }

    public static ChatMessageResponse result(String sessionId, String message, List<String> options, Object data) {
        return new ChatMessageResponse(sessionId, ChatResponseType.RESULT, message, options, data);
    }
}
