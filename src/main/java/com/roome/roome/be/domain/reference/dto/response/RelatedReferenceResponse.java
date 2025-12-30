package com.roome.roome.be.domain.reference.dto.response;

import java.util.List;

public record RelatedReferenceResponse(
        Long referenceId,
        String thumbnailUrl,
        List<String> imageUrls,
        Integer scrapCount,
        String userName,
        Integer matchedTagCount
) {
}
