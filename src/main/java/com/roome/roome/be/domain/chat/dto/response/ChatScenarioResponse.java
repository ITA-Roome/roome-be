package com.roome.roome.be.domain.chat.dto.response;

import com.roome.roome.be.domain.ai.dto.response.AiProductResponse;
import com.roome.roome.be.domain.chat.enums.ChatMode;

import java.util.List;

public record ChatScenarioResponse(
        ChatMode chatMode,
        List<AiProductResponse> products
) {
    public static ChatScenarioResponse from(List<AiProductResponse> list) {
        return new ChatScenarioResponse(ChatMode.PRODUCT, list);
    }
}
