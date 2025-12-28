package com.roome.roome.be.domain.reference.dto.response;


import java.util.Set;

public record CandidateReferenceInfo(
        Long referenceId,
        String description,
        String referenceImageUrl,
        Set<ReferenceTagInfo> tagList
) {
}
