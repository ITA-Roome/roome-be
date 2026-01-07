package com.roome.roome.be.domain.chat.dto.request;

import com.roome.roome.be.domain.chat.enums.ChatInputType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageRequest(
        String sessionId,                 // null 허용(첫 진입)
        @NotNull ChatInputType inputType, // BUTTON | TEXT
        @NotBlank String message
) {
}
