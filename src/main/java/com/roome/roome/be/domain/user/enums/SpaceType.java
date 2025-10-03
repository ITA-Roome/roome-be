package com.roome.roome.be.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpaceType {
    ROOM("방"),
    ONE_ROOM("원룸"),
    LIVING_ROOM("거실"),
    KITCHEN("주방"),
    BATHROOM("화장실"),
    BEDROOM("침실");

    private final String description;
}
