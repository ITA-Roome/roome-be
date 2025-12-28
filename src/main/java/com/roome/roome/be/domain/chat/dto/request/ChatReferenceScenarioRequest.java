package com.roome.roome.be.domain.chat.dto.request;

import com.roome.roome.be.domain.reference.enums.ReferenceMood;
import com.roome.roome.be.domain.reference.enums.ReferenceSize;
import com.roome.roome.be.domain.reference.enums.ReferenceStyle;
import com.roome.roome.be.domain.reference.enums.ReferenceType;
import jakarta.validation.constraints.NotNull;

public record ChatReferenceScenarioRequest(
        // ===== 인테리어 모드 =====
        @NotNull
        ReferenceType referenceType,
        @NotNull
        ReferenceSize referenceSize,
        @NotNull
        ReferenceMood referenceMood,
        @NotNull
        ReferenceStyle referenceStyle,

        @NotNull
        Integer maxBudget,
        @NotNull
        Integer minBudget
) {
}
