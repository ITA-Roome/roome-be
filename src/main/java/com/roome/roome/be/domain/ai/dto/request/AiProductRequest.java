package com.roome.roome.be.domain.ai.dto.request;

import com.roome.roome.be.domain.chat.dto.request.ChatScenarioRequest;
import com.roome.roome.be.domain.product.dto.response.CandidateProductInfo;

import java.util.List;

public record AiProductRequest(
        String space,
        String style,
        List<String> colors,
        Integer minBudget,
        Integer maxBudget,
        List<CandidateProductInfo> candidateList
) {
    public static AiProductRequest from(ChatScenarioRequest request, List<CandidateProductInfo> candidateList) {
        return new AiProductRequest(
                request.referenceType() != null ? request.referenceType().name() : null,
                request.referenceStyle() != null ? request.referenceStyle().name() : null,
                request.preferredColors(),
                request.minBudget(),
                request.maxBudget(),
                candidateList
        );
    }
}
