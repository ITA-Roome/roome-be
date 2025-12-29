package com.roome.roome.be.domain.chat.dto.response;

import com.roome.roome.be.domain.ai.dto.response.AiProductResponse;
import com.roome.roome.be.domain.product.dto.response.CandidateProductInfo;


public record ProductSummaryResponse(
        Long productId,
        String name,
        Integer price,
        String imageUrl,
        String reason,
        String advantage,
        String mood,
        String recommendedPlace

) {

    public static ProductSummaryResponse from(
            CandidateProductInfo candidate,
            AiProductResponse ai
    ) {
        return new ProductSummaryResponse(
                candidate.productId(),
                candidate.productName(),
                candidate.productPrice(),
                candidate.productImageList().stream().findFirst().orElse(null),
                ai.reason(),
                ai.advantage(),
                ai.mood(),
                ai.recommendedPlace()
        );
    }
}
