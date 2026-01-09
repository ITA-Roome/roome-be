package com.roome.roome.be.domain.chat.dto.request;

import com.roome.roome.be.domain.chat.model.ChatSession;
import com.roome.roome.be.domain.product.enums.ProductType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ChatProductScenarioRequest(
        @NotNull
        Long userId,

        @Size(max = 3)
        List<String> preferredColors,

        @NotNull
        Integer maxBudget,
        Integer minBudget,

        // ===== 제품 모드 =====
        @Size(max = 3)
        List<ProductType> productTypes
) {
        public static ChatProductScenarioRequest create(ChatSession chatSession){
                return new ChatProductScenarioRequest(
                        chatSession.userId(),
                        chatSession.productColors(),
                        chatSession.productMaxBudget(),
                        chatSession.productMinBudget(),

                        chatSession.productType() != null
                                ? List.of(chatSession.productType())
                                : null
                );
        }
}
