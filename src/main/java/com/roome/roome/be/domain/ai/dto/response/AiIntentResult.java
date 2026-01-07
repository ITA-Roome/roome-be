package com.roome.roome.be.domain.ai.dto.response;

import com.roome.roome.be.domain.chat.enums.ChatIntentType;

public record AiIntentResult(
        ChatIntentType intent,
        double confidence,

        ReferenceSlot reference,   // 인테리어용
        ProductSlot product,       // 상품용

        boolean recommendRequest,
        boolean resetRequest
) {
}
