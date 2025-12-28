package com.roome.roome.be.domain.ai.dto.response;

public record AiProductResponse(
        Long productId,
        int score,
        String reason,
        String advantage,
        String mood,
        String recommendedPlace
) {
}
