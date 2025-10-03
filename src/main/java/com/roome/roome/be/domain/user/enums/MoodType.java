package com.roome.roome.be.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MoodType {
    COZY("아늑한"),
    KITSCH("키치한"),
    NEAT("깔끔한"),
    NORDIC("북유럽풍의"),
    MODERN("모던한"),
    UNIQUE("개성있는"),
    WARM("따뜻한"),
    COOL("시원한");

    private final String description;
}
