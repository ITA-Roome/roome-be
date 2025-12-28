package com.roome.roome.be.domain.ai.dto.request;

import com.roome.roome.be.domain.chat.dto.request.ChatReferenceScenarioRequest;
import com.roome.roome.be.domain.chat.dto.response.ReferenceCandidate;
import com.roome.roome.be.domain.reference.dto.response.CandidateReferenceInfo;

import java.util.List;

public record AiReferenceRequest(
        String userName,
        String spaceType,
        String size,
        String mood,
        String style,
        List<ReferenceCandidate> candidates
) {

    public static AiReferenceRequest from(
            String userName,
            ChatReferenceScenarioRequest request,
            List<CandidateReferenceInfo> list
    ) {
        return new AiReferenceRequest(
                userName,
                request.referenceType().name(),
                request.referenceSize().name(),
                request.referenceMood().name(),
                request.referenceStyle().name(),
                list.stream()
                        .map(ReferenceCandidate::from)
                        .toList()
        );
    }
}

