package com.roome.roome.be.domain.reference.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum ReferenceCategoryMapping {

    /* =========================
       LIGHTING (조명)
    ========================= */
    // 전구는 모든 공간, 모든 평수에 필요함
    LIGHTING_LED_BULB(
            EnumSet.allOf(ReferenceType.class),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 일반 조명(천장등)은 주요 생활 공간에 추천
    LIGHTING_GENERAL(
            EnumSet.of(ReferenceType.LIVING_ROOM, ReferenceType.BEDROOM, ReferenceType.KITCHEN, ReferenceType.STUDY, ReferenceType.KIDS_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 무드등/장식 조명
    LIGHTING_DECORATIVE(
            EnumSet.of(ReferenceType.LIVING_ROOM, ReferenceType.BEDROOM, ReferenceType.STUDY),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 야외/베란다 조명 (ETC: 베란다/테라스 등)
    LIGHTING_OUTDOOR(
            EnumSet.of(ReferenceType.ETC, ReferenceType.LIVING_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 욕실 조명
    LIGHTING_BATHROOM(
            EnumSet.of(ReferenceType.BATHROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 레일/시스템 조명 (보통 거실, 주방, 작업실)
    LIGHTING_SYSTEM(
            EnumSet.of(ReferenceType.LIVING_ROOM, ReferenceType.KITCHEN, ReferenceType.STUDY),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 스마트 조명 (작업실, 침실, 거실)
    LIGHTING_SMART(
            EnumSet.of(ReferenceType.STUDY, ReferenceType.BEDROOM, ReferenceType.LIVING_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    /* =========================
        DINING / KITCHEN (주방 & 다이닝)
    ========================= */
    // 식탁 의자는 주방뿐 아니라 서재나 거실 보조 의자로도 쓰임
    DININGROOM_CHAIR(
            EnumSet.of(ReferenceType.KITCHEN, ReferenceType.LIVING_ROOM, ReferenceType.STUDY),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 사이드 테이블은 거실, 침실, 베란다 등 다양하게 쓰임
    DININGROOM_COFFEE_SIDE_TABLE(
            EnumSet.of(ReferenceType.LIVING_ROOM, ReferenceType.BEDROOM, ReferenceType.ETC),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 유아용 의자
    DININGROOM_KIDS_CHAIR(
            EnumSet.of(ReferenceType.KITCHEN, ReferenceType.LIVING_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 식탁 (좁은 원룸에는 2인용이 들어갈 수 있으므로 전체 사이즈 허용하되, 추천 로직에서 조절 가능)
    DININGROOM_TABLE(
            EnumSet.of(ReferenceType.KITCHEN, ReferenceType.LIVING_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 스텝 스툴 (주방 상부장, 서재 높은 책장, 욕실 등)
    DININGROOM_STEP_STOOL(
            EnumSet.of(ReferenceType.KITCHEN, ReferenceType.STUDY, ReferenceType.BATHROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 아일랜드 식탁 의자
    DININGROOM_BAR_TABLE_CHAIR(
            EnumSet.of(ReferenceType.KITCHEN, ReferenceType.LIVING_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 유아용 테이블
    DININGROOM_KIDS_TABLE(
            EnumSet.of(ReferenceType.KIDS_ROOM, ReferenceType.LIVING_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 그릇장/주방 수납장
    DININGROOM_FURNITURE(
            EnumSet.of(ReferenceType.KITCHEN, ReferenceType.LIVING_ROOM),
            EnumSet.of(ReferenceSize.MEDIUM, ReferenceSize.LARGE) // 너무 좁은 곳엔 비추천
    ),

    // 스툴 (화장대 의자, 현관 의자 등 다용도)
    DININGROOM_STOOL(
            EnumSet.of(ReferenceType.LIVING_ROOM, ReferenceType.BEDROOM, ReferenceType.ETC),
            EnumSet.allOf(ReferenceSize.class)
    ),

    /* =========================
       STUDY / OFFICE (서재)
    ========================= */
    // 사무용 의자
    OFFICE_CHAIR(
            EnumSet.of(ReferenceType.STUDY, ReferenceType.BEDROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 책상 (침실에 두는 경우도 포함)
    OFFICE_DESK(
            EnumSet.of(ReferenceType.STUDY, ReferenceType.BEDROOM, ReferenceType.LIVING_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    /* =========================
       LIVING ROOM (거실)
    ========================= */
    // 암체어/1인 소파 (침실이나 서재에도 둠)
    LIVINGROOM_ARMCHAIR(
            EnumSet.of(ReferenceType.LIVING_ROOM, ReferenceType.BEDROOM, ReferenceType.STUDY),
            EnumSet.of(ReferenceSize.MEDIUM, ReferenceSize.LARGE, ReferenceSize.UNKNOWN) // 좁은 원룸엔 꽉 찰 수 있음
    ),

    // 액자/포스터 (모든 공간 필수 인테리어 템)
    HOME_DECOR_FRAME_POSTER(
            EnumSet.allOf(ReferenceType.class),
            EnumSet.allOf(ReferenceSize.class)
    ),

    /* =========================
       TEXTILE (패브릭)
    ========================= */
    // 아기용 텍스타일
    TEXTILE_RUG_BABY_TEXTILE(
            EnumSet.of(ReferenceType.BEDROOM, ReferenceType.KIDS_ROOM, ReferenceType.LIVING_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 침구 (침실, 아이방)
    TEXTILE_RUG_BEDDING(
            EnumSet.of(ReferenceType.BEDROOM, ReferenceType.KIDS_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 키즈 텍스타일
    TEXTILE_RUG_KIDS_TEXTILE(
            EnumSet.of(ReferenceType.KIDS_ROOM, ReferenceType.BEDROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 러그 (욕실/주방 제외한 거실 공간)
    TEXTILE_RUG_RUG(
            EnumSet.of(ReferenceType.LIVING_ROOM, ReferenceType.BEDROOM, ReferenceType.STUDY, ReferenceType.KIDS_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 욕실 매트/타월
    TEXTILE_RUG_BATH_TEXTILE(
            EnumSet.of(ReferenceType.BATHROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 쿠션 커버 (소파나 침대 위)
    TEXTILE_RUG_CUSHION_COVER(
            EnumSet.of(ReferenceType.LIVING_ROOM, ReferenceType.BEDROOM, ReferenceType.STUDY),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 주방 장갑/앞치마 등
    TEXTILE_RUG_KITCHEN_TEXTILE(
            EnumSet.of(ReferenceType.KITCHEN),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 담요 (거실 소파, 침실, 서재, 차박 등)
    TEXTILE_RUG_BLANKET_THROW(
            EnumSet.of(ReferenceType.LIVING_ROOM, ReferenceType.BEDROOM, ReferenceType.STUDY, ReferenceType.ETC),
            EnumSet.allOf(ReferenceSize.class)
    ),

    /* =========================
       CURTAIN (커튼/블라인드)
    ========================= */
    // 커튼 (거실, 침실, 아이방)
    CURTAIN_BLIND_CURTAIN(
            EnumSet.of(ReferenceType.LIVING_ROOM, ReferenceType.BEDROOM, ReferenceType.KIDS_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    // 블라인드 (서재, 베란다, 주방 창문, 침실)
    CURTAIN_BLIND_BLIND(
            EnumSet.of(ReferenceType.STUDY, ReferenceType.ETC, ReferenceType.KITCHEN, ReferenceType.BEDROOM),
            EnumSet.allOf(ReferenceSize.class)
    ),

    /* =========================
       KITCHENWARE (식기류)
    ========================= */
    // 식탁보/매트
    KITCHENWARE_TABLE_TEXTILE(
            EnumSet.of(ReferenceType.KITCHEN, ReferenceType.LIVING_ROOM),
            EnumSet.allOf(ReferenceSize.class)
    );


    // =================================================================
    //  FIELD & METHODS
    // =================================================================

    private final Set<ReferenceType> targetSpaces;
    private final Set<ReferenceSize> targetSizes;

    /**
     * 사용자의 입력(ReferenceType, ReferenceSize)과 카테고리가 매칭되는지 확인
     * @param inputSpace 사용자가 선택한 공간
     * @param inputSize 사용자가 선택한 평수 (UNKNOWN일 경우 사이즈 무관하게 통과)
     * @return 매칭 여부
     */
    public boolean isMatch(ReferenceType inputSpace, ReferenceSize inputSize) {
        // 1. 공간 매칭 여부 확인
        boolean isSpaceMatched = targetSpaces.contains(inputSpace);

        // 2. 평수 매칭 여부 확인
        // 사용자가 '잘 모르겠어요(UNKNOWN)'를 선택했거나, 카테고리가 해당 평수를 지원하면 통과
        boolean isSizeMatched = (inputSize == ReferenceSize.UNKNOWN) || targetSizes.contains(inputSize);

        return isSpaceMatched && isSizeMatched;
    }
}