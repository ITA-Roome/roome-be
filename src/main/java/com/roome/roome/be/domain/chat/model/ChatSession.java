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
        Long userId,
        ChatMode mode,                 // 현재 대화 모드
        Instant updatedAt,

        /* =========================
         * PRODUCT (제품 추천)
         * ========================= */
        List<ProductType> productTypes,
        List<String> productColors,
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
        public static ChatSession create(Long userId) {
                return new ChatSession(
                        userId,
                        ChatMode.UNDECIDED,
                        Instant.now(),

                        List.of(),
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
                        userId,
                        mode,
                        Instant.now(),

                        productTypes,
                        productColors,
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
                List<String> productColors,
                Integer minBudget,
                Integer maxBudget
        ) {
                return new ChatSession(
                        userId,
                        ChatMode.PRODUCT,
                        Instant.now(),

                        productTypes,
                        productColors,
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
                        userId,
                        ChatMode.REFERENCE,
                        Instant.now(),

                        productTypes,
                        productColors,
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
