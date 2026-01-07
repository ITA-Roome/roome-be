package com.roome.roome.be.domain.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.chat.enums.ChatTask;
import com.roome.roome.be.domain.chat.enums.MissingField;
import com.roome.roome.be.domain.product.enums.ProductType;
import com.roome.roome.be.domain.reference.enums.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record ChatSession(

        /* =========================
         * 기본 정보
         * ========================= */
        Long userId,
        ChatMode mode,
        ChatTask task,              // 현재 대화 모드
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
                ChatTask.UNDECIDED,
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

    /**
     * 도메인만 전환 (task는 유지)
     */
    public ChatSession withMode(ChatMode mode) {
        return new ChatSession(
                userId,
                mode,
                task,
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
                ChatTask.PRODUCT_COLLECTING,
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
                ChatTask.REFERENCE_COLLECTING,
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

    public ChatSession withTask(ChatTask task) {
        return new ChatSession(
                userId,
                mode,
                task,
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

    /* =========================
     * MissingField 계산
     * ========================= */

    public List<MissingField> missingProductFields() {
        List<MissingField> missing = new ArrayList<>();

        if (productTypes == null || productTypes.isEmpty()) {
            missing.add(MissingField.PRODUCT_TYPE);
        }
        if (productColors == null || productColors.isEmpty()) {
            missing.add(MissingField.PRODUCT_COLOR);
        }
        if (productMinBudget == null) {
            missing.add(MissingField.PRODUCT_MIN_BUDGET);
        }
        if (productMaxBudget == null) {
            missing.add(MissingField.PRODUCT_MAX_BUDGET);
        }

        return missing;
    }

    public List<MissingField> missingReferenceFields() {
        List<MissingField> missing = new ArrayList<>();

        if (referenceType == null) {
            missing.add(MissingField.REFERENCE_TYPE);
        }
        if (referenceSize == null) {
            missing.add(MissingField.REFERENCE_SIZE);
        }
        if (referenceMoods == null || referenceMoods.isEmpty()) {
            missing.add(MissingField.REFERENCE_MOOD);
        }
        if (referenceStyles == null || referenceStyles.isEmpty()) {
            missing.add(MissingField.REFERENCE_STYLE);
        }
        if (referenceMinBudget == null) {
            missing.add(MissingField.REFERENCE_MIN_BUDGET);
        }
        if (referenceMaxBudget == null) {
            missing.add(MissingField.REFERENCE_MAX_BUDGET);
        }

        return missing;
    }

    @JsonIgnore
    public boolean isProductReady() {
        return missingProductFields().isEmpty();
    }

    @JsonIgnore
    public boolean isReferenceReady() {
        return missingReferenceFields().isEmpty();
    }

}
