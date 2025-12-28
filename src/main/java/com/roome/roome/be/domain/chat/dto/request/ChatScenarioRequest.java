package com.roome.roome.be.domain.chat.dto.request;

import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.product.enums.ProductType;
import com.roome.roome.be.domain.reference.enums.ReferenceMood;
import com.roome.roome.be.domain.reference.enums.ReferenceSize;
import com.roome.roome.be.domain.reference.enums.ReferenceStyle;
import com.roome.roome.be.domain.reference.enums.ReferenceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ChatScenarioRequest(
        @NotNull(message = "chatMode는 필수입니다.")
        ChatMode chatMode,

        // ===== 인테리어 모드 =====
        ReferenceType referenceType,
        ReferenceSize referenceSize,
        ReferenceMood referenceMood,
        ReferenceStyle referenceStyle,

        @Size(max = 3)
        List<String> preferredColors,

        @NotNull
        Integer maxBudget,
        Integer minBudget,

        // ===== 제품 모드 =====
        @Size(max = 3)
        List<ProductType> productTypes
) {
}
