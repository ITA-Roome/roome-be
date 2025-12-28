package com.roome.roome.be.domain.ai.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roome.roome.be.domain.ai.dto.request.AiProductRequest;

import java.util.Collections;
import java.util.stream.Collectors;

public class ProductPromptBuilder {

    private static final ObjectMapper om = new ObjectMapper();

    private static final String SYSTEM = """
            너는 인테리어 쇼핑몰의 "상품 추천 평가 AI"이다.

            절대 규칙 (어기면 안 됨)
            1. 반드시 아래에 제공된 상품 ID만 사용한다.
            2. 존재하지 않는 ID를 생성하지 않는다.
            3. JSON 배열만 출력한다. (설명, 마크다운, 문장 금지)
            4. 점수는 0~100점 사이의 정수이다.
            5. 점수가 높은 상품 3~4개만 반환한다.
            
            ※ 태그는 직접 출력하지 말고,
               '장점', '분위기', '추천 위치'를 추론해서 작성할 것.

            ────────────────────
            점수 산정 기준 (총 100점)
            ────────────────────
            1️⃣ 공간 적합도 (0~30점)
            - 사용자의 공간 유형(거실/침실/서재 등)에 적합한가?

            2️⃣ 스타일 / 분위기 일치도 (0~30점)
            - 사용자가 선택한 분위기, 스타일과 잘 어울리는가?

            3️⃣ 예산 적합도 (0~20점)
            - 가격이 예산 범위에 가까울수록 높은 점수

            4️⃣ 태그 일치도 (0~20점)
            - 상품 태그가 사용자의 선호 태그와 얼마나 일치하는가?
            - STYLE, MOOD, FEATURE 태그가 많을수록 가점

            ────────────────────
            📤 출력 형식 (반드시 이 형식)
            ────────────────────
            [
              {
                "productId": 12,
                "score": 92,
                "reason": "거실용 가구이며 미니멀 스타일과 잘 어울리고 예산 범위 내에 있음"
                "advantage": "공간을 넓어 보이게 하며 미니멀한 분위기를 연출함",
                "mood": "깔끔하고 따뜻한 분위기",
                "recommendedPlace": "거실 / 서재"
              }
            ]
            """;

    public static String build(AiProductRequest req) {
        try {
            String candidateText =
                    req.candidateList().stream()
                            .map(p -> String.format(
                                    """
                                            ID:%d
                                            이름:%s
                                            가격:%d
                                            태그:%s
                                            """,
                                    p.productId(),
                                    p.productName(),
                                    p.productPrice(),
                                    (p.tagList() == null
                                            ? Collections.emptyList()
                                            : p.tagList()
                                    ).stream()
                                            .map(t -> t.getClass().getName())
                                            .collect(Collectors.joining(", "))
                            ))
                            .collect(Collectors.joining("\n"));

            return SYSTEM
                    + "\n\n[사용자 조건]\n"
                    + om.writeValueAsString(req)
                    + "\n\n[상품 후보 목록]\n"
                    + candidateText;

        } catch (Exception e) {
            throw new IllegalStateException("AI 프롬프트 생성 실패", e);
        }
    }
}
