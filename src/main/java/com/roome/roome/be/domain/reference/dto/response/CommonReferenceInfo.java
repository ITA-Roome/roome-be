package com.roome.roome.be.domain.reference.dto.response;

import java.util.List;

public record CommonReferenceInfo(
        Long referenceId,
        String nickname,
        Long userId,
        List<String> imageUrlList,
        Integer scrapCount,
        Integer likeCount,
        boolean isScrapped,
        boolean isLiked
) {
}
