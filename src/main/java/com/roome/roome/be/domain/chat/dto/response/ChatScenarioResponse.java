package com.roome.roome.be.domain.chat.dto.response;

import com.roome.roome.be.domain.chat.enums.ChatMode;

import java.util.List;

public record ChatScenarioResponse(
        ChatMode chatMode,
        List<ProductSummaryResponse> products
) {

    public static ChatScenarioResponse from(List<ProductSummaryResponse> productSummaryResponseList) {
        return new ChatScenarioResponse(ChatMode.PRODUCT, productSummaryResponseList);
    }
}
