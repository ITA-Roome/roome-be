package com.roome.roome.be.domain.reference.dto.response;


public record RelatedReferenceResponse(
        Long referenceId,
        String thumbnailUrl,
        Integer scrapCount,
        String userName,
        Integer matchedTagCount
) {
}
