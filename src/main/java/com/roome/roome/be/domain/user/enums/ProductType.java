package com.roome.roome.be.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductType {
    BLANKET("이불"),
    CUSHION("쿠션"),
    CURTAIN("커튼"),
    INTERIOR_ITEM("인테리어 소품"),
    RUG("러그"),
    LIGHT("조명"),
    TABLE_CLOTH("식탁보"),
    POSTER("포스터");

    private final String description;
}
