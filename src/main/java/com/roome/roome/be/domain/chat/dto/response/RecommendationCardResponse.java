package com.roome.roome.be.domain.chat.dto.response;

import java.util.List;

public record RecommendationCardResponse(
        String title,
        List<String> bulletPoints,
        List<ProductSummaryResponse> products
) {
}
