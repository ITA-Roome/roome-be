package com.roome.roome.be.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {
    EMAIL("EMAIL"),
    GOOGLE("GOOGLE"),
    KAKAO("KAKAO");

    private final String type;
}
