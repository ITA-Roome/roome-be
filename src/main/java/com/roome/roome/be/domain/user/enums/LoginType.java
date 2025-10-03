package com.roome.roome.be.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {
    EMAIL("EMAIL"),
    SOCIAL("SOCIAL");

    private final String type;
}
