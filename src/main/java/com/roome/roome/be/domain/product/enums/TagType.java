package com.roome.roome.be.domain.product.enums;

public enum TagType {
    COLOR,
    MATERIAL,
    FEATURE,
    PRODUCT_TYPE,
    SIZE,
    USAGE,
    STYLE,
    MOOD,

    REFERENCE_COLOR,
    REFERENCE_MATERIAL,
    REFERENCE_FEATURE,
    REFERENCE_PRODUCT_TYPE, // 또는 REFERENCE_TYPE 등 실제 DB 값 확인 필요
    REFERENCE_SIZE,
    REFERENCE_USAGE,       // 만약 DB에 REFERENCE_TYPE으로 저장된다면 매핑 로직에서 수정 필요
    REFERENCE_STYLE,
    REFERENCE_MOOD
}
