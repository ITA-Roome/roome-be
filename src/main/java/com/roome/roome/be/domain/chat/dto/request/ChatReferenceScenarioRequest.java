package com.roome.roome.be.domain.chat.dto.request;

import com.roome.roome.be.domain.chat.model.ChatSession;
import com.roome.roome.be.domain.reference.enums.ReferenceColor; // Import 추가
import com.roome.roome.be.domain.reference.enums.ReferenceMood;
import com.roome.roome.be.domain.reference.enums.ReferenceSize;
import com.roome.roome.be.domain.reference.enums.ReferenceStyle;
import com.roome.roome.be.domain.reference.enums.ReferenceType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ChatReferenceScenarioRequest(
        @NotNull
        Long userId,

        ReferenceType referenceType,
        ReferenceSize referenceSize,
        List<ReferenceMood> referenceMood,
        List<ReferenceStyle> referenceStyle,

        ReferenceColor referenceColor,

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
                        chatSession.referenceColor() != null ? chatSession.referenceColor() : ReferenceColor.NO_PREFERENCE,
                        chatSession.referenceMaxBudget(),
                        chatSession.referenceMinBudget()
                );
        }
}