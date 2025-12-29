package com.roome.roome.be.domain.chat.dto.response;

import com.roome.roome.be.domain.chat.enums.ChatMode;

import java.util.List;

public record ChatProductScenarioResponse(
        ChatMode chatMode,
        List<ProductSummaryResponse> products
) {

    public static ChatProductScenarioResponse from(List<ProductSummaryResponse> productSummaryResponseList) {
        return new ChatProductScenarioResponse(ChatMode.PRODUCT, productSummaryResponseList);
    }
}
