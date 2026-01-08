package com.roome.roome.be.domain.reference.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReferenceColor {

    BRIGHT_TONE("밝은 톤"),
    DARK_TONE("어두운 톤"),
    WARM_TONE("따뜻한 톤"),
    GRAY_TONE("그레이 톤"),
    CALM_TONE("차분한 톤"),
    NEUTRAL("뉴트럴"),
    COLORFUL("컬러풀/비비드"),
    NO_PREFERENCE("상관없음");

    private final String description;
}