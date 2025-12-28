package com.roome.roome.be.domain.ai.dto.request;

import com.roome.roome.be.domain.chat.dto.request.ChatScenarioRequest;
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
            ChatScenarioRequest req,
            List<CandidateProductInfo> candidates,
            List<ProductCategory> categories
    ) {
        return new AiProductRequest(
                categories.stream().map(Enum::name).toList(),
                req.preferredColors(),
                req.minBudget(),
                req.maxBudget(),
                candidates
        );
    }
}

