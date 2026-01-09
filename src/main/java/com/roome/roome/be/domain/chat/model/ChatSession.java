package com.roome.roome.be.domain.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.chat.enums.ChatTask;
import com.roome.roome.be.domain.chat.enums.MissingField;
import com.roome.roome.be.domain.product.enums.ProductCategory;
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
        ChatTask task,
        Instant updatedAt,

        /* =========================
         * PRODUCT (제품 추천)
         * ========================= */
        ProductType productType,                 // [수정] 대분류 (단일 값)
        List<ProductCategory> productCategories, // [수정] 소분류 (리스트)
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
     * 생성 (Factory Method)
     * ========================= */
    public static ChatSession create(Long userId) {
        return new ChatSession(
                userId,
                ChatMode.UNDECIDED,
                ChatTask.UNDECIDED,
                Instant.now(),

                // Product 초기값
                null,           // productType
                List.of(),      // productCategories
                List.of(),      // productColors
                null,           // minBudget
                null,           // maxBudget

                // Reference 초기값
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
     * 상태 갱신 (불변 객체이므로 새 객체 반환)
     * ========================= */

    /**
     * 모드 변경 (기존 데이터 유지)
     */
    public ChatSession withMode(ChatMode mode) {
        return new ChatSession(
                userId,
                mode,
                task,
                Instant.now(),

                // Product 필드 유지
                productType,
                productCategories,
                productColors,
                productMinBudget,
                productMaxBudget,

                // Reference 필드 유지
                referenceType,
                referenceSize,
                referenceMoods,
                referenceStyles,
                referenceColor,
                referenceMinBudget,
                referenceMaxBudget
        );
    }

    /**
     * Product 정보 업데이트 (수정된 필드 반영)
     */
    public ChatSession withProductInfo(
            ProductType productType,                // [변경]
            List<ProductCategory> productCategories,// [변경]
            List<String> productColors,
            Integer minBudget,
            Integer maxBudget
    ) {
        return new ChatSession(
                userId,
                ChatMode.PRODUCT,
                ChatTask.PRODUCT_COLLECTING,
                Instant.now(),

                productType,        // 업데이트
                productCategories,  // 업데이트
                productColors,
                minBudget,
                maxBudget,

                // Reference 유지
                referenceType,
                referenceSize,
                referenceMoods,
                referenceStyles,
                referenceColor,
                referenceMinBudget,
                referenceMaxBudget
        );
    }

    /**
     * Reference 정보 업데이트
     */
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

                // Product 유지
                productType,
                productCategories,
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

                productType,
                productCategories,
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
     * 추천 가능 여부 판단
     * ========================= */

    public boolean canRecommendProduct() {
        // 대분류(Type)가 있고, 예산 범위 중 하나라도 있으면 추천 가능
        // (소분류 Category는 비어있으면 '전체'로 간주하므로 필수가 아님)
        return productType != null
                && (productMinBudget != null || productMaxBudget != null);
    }

    public boolean canRecommendReference() {
        return referenceType != null
                && referenceMoods != null && !referenceMoods.isEmpty()
                && (referenceMinBudget != null || referenceMaxBudget != null);
    }

    /* =========================
     * 누락 필드 확인 (질문 순서 제어)
     * ========================= */

    public List<MissingField> missingProductFields() {
        List<MissingField> missing = new ArrayList<>();

        // 1. 대분류 먼저 확인
        if (productType == null) {
            missing.add(MissingField.PRODUCT_TYPE);
        }
        // 2. 대분류가 있다면 -> 소분류 확인 (선택 안했으면 물어봄)
        else if (productCategories == null || productCategories.isEmpty()) {
            missing.add(MissingField.PRODUCT_DETAIL_CATEGORY);
        }

        // 3. 예산 확인
        if (productMinBudget == null && productMaxBudget == null) {
            missing.add(MissingField.PRODUCT_BUDGET);
        }

        // 4. 컬러 확인
        if (productColors == null || productColors.isEmpty()) {
            missing.add(MissingField.PRODUCT_COLOR_MOOD);
        }

        return missing;
    }

    public List<MissingField> missingReferenceFields() {
        List<MissingField> missing = new ArrayList<>();

        if (referenceType == null) missing.add(MissingField.REFERENCE_TYPE);
        if (referenceSize == null) missing.add(MissingField.REFERENCE_SIZE);
        if (referenceMoods == null || referenceMoods.isEmpty()) missing.add(MissingField.REFERENCE_MOOD);
        if (referenceStyles == null || referenceStyles.isEmpty()) missing.add(MissingField.REFERENCE_STYLE);
        if (referenceColor == null) missing.add(MissingField.REFERENCE_COLOR);

        if (referenceMinBudget == null && referenceMaxBudget == null) {
            missing.add(MissingField.REFERENCE_BUDGET);
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