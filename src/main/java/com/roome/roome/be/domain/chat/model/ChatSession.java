package com.roome.roome.be.domain.chat.model;

import com.roome.roome.be.domain.chat.enums.ChatMode;

import java.time.LocalDateTime;

public record ChatSession(
        Long userId,
        ChatMode chatMode,
        Object request,
        Object response,
        LocalDateTime createdAt
) {
    public static ChatSession from(
            Long userId,
            ChatMode chatMode,
            Object request,
            Object response
    ) {
        return new ChatSession(
                userId,
                chatMode,
                request,
                response,
                LocalDateTime.now()
        );
    }
}
