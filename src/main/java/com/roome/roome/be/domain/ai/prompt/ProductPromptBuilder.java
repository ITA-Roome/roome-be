package com.roome.roome.be.domain.ai.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roome.roome.be.domain.ai.dto.request.AiProductRequest;

public class ProductPromptBuilder {

    private static final ObjectMapper om = new ObjectMapper();

    private static final String SYSTEM = """
            너는 인테리어 쇼핑몰의 추천 AI이다.

            규칙:
            - 사용자의 공간, 스타일, 예산에 맞는 제품을 고른다
            - 너무 비슷한 제품은 제외한다
            - 가장 적합한 제품 3~4개만 선택한다
            - 반드시 JSON 배열로만 출력한다

            출력 형식:
            [
              {
                "productId": 1,
                "reason": "추천 이유"
              }
            ]
            """;

    public static String build(AiProductRequest req) {
        try {
            return SYSTEM + "\n\n[사용자 요청]\n" +
                    om.writeValueAsString(req);
        } catch (Exception e) {
            throw new IllegalStateException("AI 프롬프트 생성 실패", e);
        }
    }
}
