package com.roome.roome.be.domain.ai.dto.request;

import com.roome.roome.be.domain.chat.dto.request.ChatReferenceScenarioRequest;
import com.roome.roome.be.domain.reference.dto.response.CandidateReferenceInfo;
import com.roome.roome.be.domain.reference.enums.ReferenceMood;
import com.roome.roome.be.domain.reference.enums.ReferenceStyle;

import java.util.List;

public record AiReferenceRequest(
        String userName,
        String spaceType,
        String size,
        List<ReferenceMood> mood,
        List<ReferenceStyle> style,
        List<CandidateReferenceInfo> candidates
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
                request.referenceMood(),
                request.referenceStyle(),
                list.stream()
                        .map(CandidateReferenceInfo::from)
                        .toList()
        );
    }
}

