package com.roome.roome.be.domain.chat.dto.response;

public record ProductSummaryResponse(
        String productNumber,
        String name,
        Integer price,
        String imageUrl,
        String productUrl
) {
}
