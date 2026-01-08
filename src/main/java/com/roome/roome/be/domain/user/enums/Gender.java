package com.roome.roome.be.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    FEMALE("여성"),
    MALE("남성"),
    OTHER("공개 안함");

    private final String description;
}
