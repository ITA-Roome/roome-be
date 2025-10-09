package com.roome.roome.be.common.jwt.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtTokenType {

    ACCESS("access"),
    REFRESH("refresh");

    private final String type;
}
