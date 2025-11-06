package com.roome.roome.be.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MoodType {
    COZY("아늑한"),
    SIMPLE("단순한"),
    SNUG("포근한"),
    NEAT("깔끔한"),
    CHIC("세련된"),
    CUTE("귀여운");

    private final String description;
}
