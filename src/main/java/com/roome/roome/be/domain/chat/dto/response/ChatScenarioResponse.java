package com.roome.roome.be.domain.chat.dto.response;

import java.util.List;

public record ChatScenarioResponse(
        String conversationKey,
        String message,
        RecommendationCardResponse result,
        List<String> actions

) {
}
