package com.roome.roome.be.domain.chat.dto.request;

import com.roome.roome.be.domain.chat.model.ChatSession;
import com.roome.roome.be.domain.reference.enums.ReferenceMood;
import com.roome.roome.be.domain.reference.enums.ReferenceSize;
import com.roome.roome.be.domain.reference.enums.ReferenceStyle;
import com.roome.roome.be.domain.reference.enums.ReferenceType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ChatReferenceScenarioRequest(
        @NotNull
        Long userId,
        // ===== 인테리어 모드 =====
        @NotNull
        ReferenceType referenceType,
        @NotNull
        ReferenceSize referenceSize,
        @NotNull
        List<ReferenceMood> referenceMood,
        @NotNull
        List<ReferenceStyle> referenceStyle,

        @NotNull
        Integer maxBudget,
        Integer minBudget
) {
        public static ChatReferenceScenarioRequest create(ChatSession chatSession){
                return new ChatReferenceScenarioRequest(
                        chatSession.userId(),
                        chatSession.referenceType(),
                        chatSession.referenceSize(),
                        chatSession.referenceMoods(),
                        chatSession.referenceStyles(),
                        chatSession.referenceMaxBudget(),
                        chatSession.referenceMinBudget()
                );
        }
}
