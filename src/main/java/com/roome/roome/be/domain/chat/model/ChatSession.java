package com.roome.roome.be.domain.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.roome.roome.be.domain.chat.dto.response.ChatProductScenarioResponse;
import com.roome.roome.be.domain.chat.dto.response.ChatReferenceScenarioResponse;
import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.chat.enums.ChatTask;
import com.roome.roome.be.domain.chat.enums.MissingField;
import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.product.enums.ProductType;
import com.roome.roome.be.domain.reference.enums.*;
import lombok.Builder; // [필수] 추가됨

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
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
        ProductType productType,
        List<ProductCategory> productCategories,
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
        Integer referenceMaxBudget,

        /* =========================
         * [추가됨] 결과 저장용 필드
         * ========================= */
        ChatProductScenarioResponse lastProductResult,
        ChatReferenceScenarioResponse lastReferenceResult

) {

    /* =========================
     * 생성 (Factory Method)
     * ========================= */
    public static ChatSession create(Long userId) {
        return ChatSession.builder()
                .userId(userId)
                .mode(ChatMode.UNDECIDED)
                .task(ChatTask.UNDECIDED)
                .updatedAt(Instant.now())
                // 리스트는 빈 리스트로 초기화
                .productCategories(List.of())
                .productColors(List.of())
                .referenceMoods(List.of())
                .referenceStyles(List.of())
                .build();
    }

    /* =========================
     * 상태 갱신 (toBuilder 이용)
     * ========================= */

    /**
     * 모드 변경
     */
    public ChatSession withMode(ChatMode mode) {
        // 기존 값은 그대로 두고(복사), mode랑 time만 바꿉니다.
        return this.toBuilder()
                .mode(mode)
                .updatedAt(Instant.now())
                .build();
    }

    /**
     * Product 정보 업데이트
     */
    public ChatSession withProductInfo(
            ProductType productType,
            List<ProductCategory> productCategories,
            List<String> productColors,
            Integer minBudget,
            Integer maxBudget
    ) {
        return this.toBuilder()
                .mode(ChatMode.PRODUCT)
                .task(ChatTask.PRODUCT_COLLECTING)
                .updatedAt(Instant.now())
                .productType(productType)
                .productCategories(productCategories)
                .productColors(productColors)
                .productMinBudget(minBudget)
                .productMaxBudget(maxBudget)
                // 만약 조건이 바뀌면 기존 추천 결과는 지우는 게 좋다면:
                // .lastProductResult(null)
                .build();
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
        return this.toBuilder()
                .mode(ChatMode.REFERENCE)
                .task(ChatTask.REFERENCE_COLLECTING)
                .updatedAt(Instant.now())
                .referenceType(type)
                .referenceSize(size)
                .referenceMoods(moods)
                .referenceStyles(styles)
                .referenceColor(color)
                .referenceMinBudget(minBudget)
                .referenceMaxBudget(maxBudget)
                .build();
    }

    public ChatSession withTask(ChatTask task) {
        return this.toBuilder()
                .task(task)
                .updatedAt(Instant.now())
                .build();
    }

    /* =========================
     * 추천 가능 여부 판단 (기존 로직 유지)
     * ========================= */
    public boolean canRecommendProduct() {
        return productType != null
                && (productMinBudget != null || productMaxBudget != null);
    }

    public boolean canRecommendReference() {
        return referenceType != null
                && referenceMoods != null && !referenceMoods.isEmpty()
                && (referenceMinBudget != null || referenceMaxBudget != null);
    }

    /* =========================
     * 누락 필드 확인 (기존 로직 유지)
     * ========================= */
    public List<MissingField> missingProductFields() {
        List<MissingField> missing = new ArrayList<>();
        if (productType == null) missing.add(MissingField.PRODUCT_TYPE);
        else if (productCategories == null || productCategories.isEmpty()) {
            missing.add(MissingField.PRODUCT_DETAIL_CATEGORY);
        }
        if (productMinBudget == null && productMaxBudget == null) {
            missing.add(MissingField.PRODUCT_BUDGET);
        }
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