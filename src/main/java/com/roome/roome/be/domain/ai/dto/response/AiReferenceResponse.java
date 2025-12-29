package com.roome.roome.be.domain.ai.dto.response;

import java.util.List;

public record AiReferenceResponse(
        String title,
        List<String> imageUrlList,
        List<Long> referenceIdList,
        String moodDescription,
        List<String> moodKeywords,
        List<String> stylingTips,
        String summary
) {
}
