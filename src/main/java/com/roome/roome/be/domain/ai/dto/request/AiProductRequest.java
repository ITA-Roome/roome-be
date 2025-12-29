package com.roome.roome.be.domain.ai.dto.request;

import com.roome.roome.be.domain.chat.dto.request.ChatProductScenarioRequest;
import com.roome.roome.be.domain.product.dto.response.CandidateProductInfo;
import com.roome.roome.be.domain.product.enums.ProductCategory;

import java.util.List;

public record AiProductRequest(
        List<String> productTypes,
        List<String> preferredColors,
        Integer minBudget,
        Integer maxBudget,
        List<CandidateProductInfo> candidateList
) {
    public static AiProductRequest from(
            ChatProductScenarioRequest request,
            List<CandidateProductInfo> candidates,
            List<ProductCategory> categories
    ) {
        return new AiProductRequest(
                categories.stream().map(Enum::name).toList(),
                request.preferredColors(),
                request.minBudget(),
                request.maxBudget(),
                candidates
        );
    }
}

