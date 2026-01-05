package com.roome.roome.be.domain.chat.model;

import com.roome.roome.be.domain.ai.dto.response.AiIntentResult;
import com.roome.roome.be.domain.chat.enums.ChatIntentType;

public record ChatDecision(
        ChatSession session,
        AiIntentResult intent
) {
}
