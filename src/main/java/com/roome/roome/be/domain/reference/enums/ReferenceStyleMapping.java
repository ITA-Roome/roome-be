package com.roome.roome.be.domain.reference.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum ReferenceStyleMapping {

    // 한글 설명 -> ReferenceStyle(스타일) 매핑
    MINIMAL("미니멀", Set.of(ReferenceStyle.MINIMALIST)), // 이제 다른 Mood(NEAT 등) 안 섞음
    NATURAL("내추럴", Set.of(ReferenceStyle.NATURAL)),
    MODERN_STYLE("모던", Set.of(ReferenceStyle.MODERN)),
    NORDIC_STYLE("북유럽", Set.of(ReferenceStyle.NORDIC)),
    VINTAGE("빈티지", Set.of(ReferenceStyle.VINTAGE)),
    UNKNOWN("잘 모르겠어요", Set.of(ReferenceStyle.MODERN, ReferenceStyle.MINIMALIST)); // 스타일 기본값

    private final String description;
    private final Set<ReferenceStyle> styles; // *오직 Style 타입만 가짐

    public static Set<ReferenceStyle> findStylesByDescription(String input) {
        return Arrays.stream(values())
                .filter(s -> s.description.equals(input))
                .findFirst()
                .map(ReferenceStyleMapping::getStyles)
                .orElse(UNKNOWN.getStyles());
    }
}
