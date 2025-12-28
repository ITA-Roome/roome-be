package com.roome.roome.be.domain.chat.dto.response;

import com.roome.roome.be.domain.ai.dto.response.AiReferenceResponse;

import java.util.List;

public record ChatReferenceScenarioResponse(
        String title,
        List<Long> referenceIdList,
        List<String> imageUrlList,
        String moodDescription,
        List<String> moodKeywords,
        String summary
) {
    public static ChatReferenceScenarioResponse from(AiReferenceResponse ai) {
        return new ChatReferenceScenarioResponse(
                ai.title(),
                ai.referenceIdList(),
                ai.imageUrlList(),
                ai.moodDescription(),
                ai.moodKeywords(),
                ai.summary()
        );
    }

    public static ChatReferenceScenarioResponse empty() {
        return new ChatReferenceScenarioResponse(
                "인테리어 무드 추천",
                List.of(),
                List.of(),
                "조건에 맞는 레퍼런스를 찾지 못했습니다.",
                List.of(),
                "조건을 조금 넓혀 다시 시도해 주세요."
        );
    }
}

