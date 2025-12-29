package com.roome.roome.be.domain.ai.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roome.roome.be.domain.ai.dto.request.AiReferenceRequest;
import com.roome.roome.be.domain.reference.dto.response.CandidateReferenceInfo;
import com.roome.roome.be.domain.reference.dto.response.ReferenceTagInfo;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReferencePromptBuilder {

    private static final ObjectMapper om = new ObjectMapper();

    private static final String SYSTEM = """
            너는 인테리어 추천 서비스의 '레퍼런스 평가 AI'이다.

            아래에 제공된 레퍼런스 후보들은
            사용자의 공간, 분위기, 스타일, 색상 조건을 기준으로
            이미 1차 필터링된 상태이다.

            너의 역할은 다음과 같다.

            ────────────────────
            [점수 산정 기준] (총 100점)
            ────────────────────
            1. 공간 적합도 (0~40점)
            - 사용자가 선택한 공간 유형(거실/침실/서재 등)에 잘 어울리는가?

            2. 분위기 / 스타일 일치도 (0~40점)
            - 사용자가 선택한 분위기 및 스타일과 조화로운가?

            3. 색감 일치도 (0~20점)
            - 사용자가 선택한 색상과 레퍼런스 색감이 잘 맞는가?

            ────────────────────
            [출력 규칙]
            ────────────────────
            1. 각 레퍼런스를 0~100점으로 평가한다.
            2. 점수가 높은 레퍼런스 3개만 선택한다.
            3. 반드시 referenceId를 그대로 사용한다.
            4. imageUrl은 제공된 값만 사용한다.
            5. 선택 이유를 간단히 작성한다.
            6. 선택된 레퍼런스를 종합하여 하나의 분위기 요약을 작성한다.
            7. 반드시 JSON 객체 형식으로만 출력한다.
            8. JSON 외 텍스트 출력 금지.

            ────────────────────
            [출력 형식]
            ────────────────────
            {
              "title": "%s님을 위한 인테리어 무드 추천",
              "referenceIdList": [1, 2, 3],
              "imageUrlList": ["url1", "url2", "url3"],
              "moodDescription": "...",
              "moodKeywords": ["...", "..."],
              "stylingTips": ["...", "..."],
              "summary": "..."
            }
            """;

    public static String build(AiReferenceRequest request) {
        try {
            String candidateText =
                    Optional.ofNullable(request.candidates())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(ReferencePromptBuilder::formatCandidate)
                            .collect(Collectors.joining("\n"));

            return SYSTEM.formatted(request.userName())
                    + "\n\n[사용자 조건]\n"
                    + om.writeValueAsString(request)
                    + "\n\n[레퍼런스 후보 목록]\n"
                    + candidateText;

        } catch (Exception e) {
            throw new IllegalStateException("Reference 프롬프트 생성 실패", e);
        }
    }

    private static String formatCandidate(CandidateReferenceInfo r) {
        String tags = Optional.ofNullable(r.tagList())
                .orElse(Collections.emptySet())
                .stream()
                .map(ReferenceTagInfo::name)
                .collect(Collectors.joining(", "));

        return String.format(
                """
                        referenceId:%d
                        imageUrl:%s
                        설명:%s
                        태그:%s
                        """,
                r.referenceId(),
                r.imageUrl(),
                r.description(),
                tags
        );
    }
}
