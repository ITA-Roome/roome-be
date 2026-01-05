package com.roome.roome.be.domain.reference.dto.response;

import java.util.List;

public record ReferenceDetailResponse(
        Long referenceId,
        String name,
        String description,
        List<String> imageUrls,
        List<ReferenceItemProductInfo> referenceItems,
        Integer scrapCount,
        Integer likeCount,
        Boolean isScrapped,
        Boolean isLiked,
        String userName,
        Long userId,
        String userProfileUrl,
        String referenceUrl
) {}
