package com.roome.roome.be.domain.reference.dto.response;


import java.util.Set;

public record CandidateReferenceInfo(
        Long referenceId,
        String imageUrl,
        String description,
        Set<ReferenceTagInfo> tagList
) {
    public static CandidateReferenceInfo from(CandidateReferenceInfo info) {
        return new CandidateReferenceInfo(
                info.referenceId(),
                info.imageUrl(),
                info.description(),
                info.tagList()
        );
    }
}
