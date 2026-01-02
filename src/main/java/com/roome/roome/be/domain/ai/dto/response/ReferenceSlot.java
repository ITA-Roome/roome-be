package com.roome.roome.be.domain.ai.dto.response;

import com.roome.roome.be.domain.reference.enums.*;

import java.util.List;

public record ReferenceSlot(
        ReferenceType spaceType,
        ReferenceSize spaceSize,
        List<ReferenceMood> moods,
        List<ReferenceStyle> styles,
        ReferenceColor colorTone,
        Integer minBudget,
        Integer maxBudget
) {
}
