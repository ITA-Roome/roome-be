package com.roome.roome.be.domain.chat.dto.response;

import java.util.List;

public record ChatResponse(
        String message,
        List<String> buttons,
        String sessionId
) {
    public static ChatResponse of(String message, List<String> buttons, String sessionId) {
        return new ChatResponse(message, buttons, sessionId);
    }
}

