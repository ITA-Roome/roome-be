package com.roome.roome.be.domain.chat.model;

import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.product.enums.ProductType;
import com.roome.roome.be.domain.reference.enums.*;

import java.time.Instant;
import java.util.List;

public record ChatSession(

        /* =========================
         * 기본 정보
         * ========================= */
        String sessionId,
        Long userId,
        ChatMode mode,                 // 현재 대화 모드
        Instant updatedAt,

        /* =========================
         * PRODUCT (제품 추천)
         * ========================= */
        List<ProductType> productTypes,
        Integer productMinBudget,
        Integer productMaxBudget,

        /* =========================
         * REFERENCE (인테리어 추천)
         * ========================= */
        ReferenceType referenceType,
        ReferenceSize referenceSize,
        List<ReferenceMood> referenceMoods,
        List<ReferenceStyle> referenceStyles,
        ReferenceColor referenceColor,
        Integer referenceMinBudget,
        Integer referenceMaxBudget
) {

        /* =========================
         * 생성
         * ========================= */
        public static ChatSession create(String sessionId, Long userId) {
                return new ChatSession(
                        sessionId,
                        userId,
                        ChatMode.UNDECIDED,
                        Instant.now(),

                        List.of(),
                        null,
                        null,

                        null,
                        null,
                        List.of(),
                        List.of(),
                        null,
                        null,
                        null
                );
        }

        /* =========================
         * 상태 갱신 (불변)
         * ========================= */

        public ChatSession withMode(ChatMode mode) {
                return new ChatSession(
                        sessionId, userId,
                        mode, Instant.now(),

                        productTypes,
                        productMinBudget,
                        productMaxBudget,

                        referenceType,
                        referenceSize,
                        referenceMoods,
                        referenceStyles,
                        referenceColor,
                        referenceMinBudget,
                        referenceMaxBudget
                );
        }

        public ChatSession withProductInfo(
                List<ProductType> productTypes,
                Integer minBudget,
                Integer maxBudget
        ) {
                return new ChatSession(
                        sessionId, userId,
                        ChatMode.PRODUCT, Instant.now(),

                        productTypes,
                        minBudget,
                        maxBudget,

                        referenceType,
                        referenceSize,
                        referenceMoods,
                        referenceStyles,
                        referenceColor,
                        referenceMinBudget,
                        referenceMaxBudget
                );
        }

        public ChatSession withReferenceInfo(
                ReferenceType type,
                ReferenceSize size,
                List<ReferenceMood> moods,
                List<ReferenceStyle> styles,
                ReferenceColor color,
                Integer minBudget,
                Integer maxBudget
        ) {
                return new ChatSession(
                        sessionId, userId,
                        ChatMode.REFERENCE, Instant.now(),

                        productTypes,
                        productMinBudget,
                        productMaxBudget,

                        type,
                        size,
                        moods,
                        styles,
                        color,
                        minBudget,
                        maxBudget
                );
        }

        /* =========================
         * 추천 가능 여부
         * ========================= */

        public boolean canRecommendProduct() {
                return productTypes != null && !productTypes.isEmpty()
                        && (productMinBudget != null || productMaxBudget != null);
        }

        public boolean canRecommendReference() {
                return referenceType != null
                        && referenceMoods != null && !referenceMoods.isEmpty()
                        && (referenceMinBudget != null || referenceMaxBudget != null);
        }
}
