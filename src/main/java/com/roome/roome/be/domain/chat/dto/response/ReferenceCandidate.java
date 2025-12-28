package com.roome.roome.be.domain.chat.dto.response;

import com.roome.roome.be.domain.reference.dto.response.CandidateReferenceInfo;
import com.roome.roome.be.domain.reference.dto.response.ReferenceTagInfo;

import java.util.Set;

public record ReferenceCandidate(
        Long referenceId,
        String imageUrl,
        String description,
        Set<ReferenceTagInfo> tagList
) {
    public static ReferenceCandidate from(CandidateReferenceInfo info) {
        return new ReferenceCandidate(
                info.referenceId(),
                info.referenceImageUrl(),
                info.description(),
                info.tagList()
        );
    }
}

