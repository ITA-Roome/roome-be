package com.roome.roome.be.domain.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {

//	// BEDROOM
//	BEDROOM_BED("침대"),
//	BEDROOM_MATTRESS("매트리스"),
//	BEDROOM_BEDDING("침구"),
//	BEDROOM_BEDSIDE_TABLE("침대협탁"),
//	BEDROOM_BED_STORAGE("침대 수납"),
//	BEDROOM_HEADBOARD("침대헤드"),
//	BEDROOM_MATTRESS_BASE("매트리스 베이스"),
//	BEDROOM_BED_SLATS("침대갈빗살"),
//	BEDROOM_BED_LEG("침대다리"),
//	BEDROOM_BED_WITH_MATTRESS("매트리스 포함 침대"),
//	BEDROOM_HEADBOARD_COVER("침대헤드커버"),
//	BEDROOM_SET("침실가구세트"),
//
//	// LIVINGROOM
//	LIVINGROOM_SOFA("소파"),
//	LIVINGROOM_ARMCHAIR("암체어/카우치"),
//	LIVINGROOM_SOFA_BED("소파베드"),
//	LIVINGROOM_SOFA_LEG("소파/암체어용 다리"),
//	LIVINGROOM_FOOTSTOOL("풋스툴/발받침대"),
//	LIVINGROOM_CHAISE_LONGUE("긴의자/카우치"),
//	LIVINGROOM_SOFA_COVER("소파/암체어용 커버"),
//	LIVINGROOM_SOFA_CUSHION("소파 쿠션"),
//
//	// DININGROOM
//	DININGROOM_FURNITURE("다이닝 가구"),
//	DININGROOM_TABLE("테이블"),
//	DININGROOM_CHAIR("의자"),
//	DININGROOM_COFFEE_SIDE_TABLE("커피테이블/보조테이블"),
//	DININGROOM_BAR_TABLE_CHAIR("바테이블/의자"),
//	DININGROOM_STOOL("스툴"),
//	DININGROOM_CAFE_FURNITURE("카페가구"),
//	DININGROOM_BENCH("벤치"),
//	DININGROOM_STEP_STOOL("스텝스툴/사다리"),
//	DININGROOM_KIDS_TABLE("어린이 테이블"),
//	DININGROOM_KIDS_CHAIR("어린이 의자"),
//	DININGROOM_BABY_CHAIR("영유아 의자"),
//	DININGROOM_VANITY_STOOL("화장대 의자/스툴"),
//
//	// OFFICE
//	OFFICE_DESK("책상/컴퓨터책상"),
//	OFFICE_CHAIR("의자/사무실의자"),
//	OFFICE_DESK_CHAIR_SET("책상/의자세트"),
//	OFFICE_MEETING_TABLE("미팅/회의용 테이블"),
//	OFFICE_GAMING_FURNITURE("게이밍 가구"),
//	OFFICE_MEETING_CHAIR("회의실 의자"),
//	OFFICE_SET("오피스 책상/의자세트"),
//
//	// KITCHEN_FURNITURE
//	KITCHEN_FURNITURE_SYSTEM("주방 시스템"),
//	KITCHEN_FURNITURE_ORGANIZER("주방정리용품"),
//	KITCHEN_FURNITURE_PANTRY("주방 팬트리"),
//	KITCHEN_FURNITURE_WALL_STORAGE("주방 벽수납"),
//	KITCHEN_FURNITURE_COUNTER("주방 조리대"),
//	KITCHEN_FURNITURE_APPLIANCE("주방 가전"),
//	KITCHEN_FURNITURE_ISLAND_CART("주방 카트/아일랜드"),
//	KITCHEN_FURNITURE_HANDLE_KNOB("손잡이"),
//	KITCHEN_FURNITURE_LIGHTING("주방 조명"),
//	KITCHEN_FURNITURE_SINK_FAUCET("주방 싱크대/수전"),
//	KITCHEN_FURNITURE_WALL_PANEL("주방 벽패널"),
//	KITCHEN_FURNITURE_MINI_KITCHEN("미니 주방"),
//	KITCHEN_FURNITURE_DOOR_DRAWER_FRONT("주방 도어/서랍앞판"),
//	KITCHEN_FURNITURE_CABINET("주방 캐비넷"),
//	KITCHEN_FURNITURE_METOD_SHELF_DRAWER("METOD 메토드 선반/서랍"),
//
//	// KITCHENWARE
//	KITCHENWARE_FOOD_STORAGE("식품보관 / 냉장고정리"),
//	KITCHENWARE_PLATES_BOWLS("접시 / 그릇"),
//	KITCHENWARE_POTS_PANS_BAKEWARE("냄비 / 팬 / 오븐용기"),
//	KITCHENWARE_DISH_RACK_CLEANING("식기건조대 / 세척용품"),
//	KITCHENWARE_CUTTING_BOARD_KNIVES("도마 / 주방칼"),
//	KITCHENWARE_TOOLS("주방도구"),
//	KITCHENWARE_CUTLERY("수저 / 커트러리"),
//	KITCHENWARE_CUPS_BOTTLES_STRAWS("유리컵 / 병 / 빨대"),
//	KITCHENWARE_MUGS_COFFEE_TEA("머그컵 / 커피 / 티용품"),
//	KITCHENWARE_TRAY_SERVING_BOWL("쟁반 / 서빙볼"),
//	KITCHENWARE_TABLE_TEXTILE("테이블 텍스타일"),
//	KITCHENWARE_BAKE_TOOLS("제빵도구"),
//	KITCHENWARE_KITCHEN_TEXTILE("주방 텍스타일"),
//	KITCHENWARE_NAPKIN_HOLDER("냅킨 / 냅킨홀더"),
//	KITCHENWARE_KIDS_TABLEWARE("어린이 식기"),
//	KITCHENWARE_OUTDOOR_PICNIC("야외활동 / 피크닉"),
//
//	// LIGHTING
//	LIGHTING_GENERAL("일반조명"),
//	LIGHTING_SYSTEM("시스템 조명"),
//	LIGHTING_SMART("스마트 조명"),
//	LIGHTING_LED_BULB("LED전구"),
//	LIGHTING_DECORATIVE("장식 조명"),
//	LIGHTING_OUTDOOR("야외용 조명"),
//	LIGHTING_BATHROOM("욕실용 조명"),
//
//	// TEXTILE_RUG
//	TEXTILE_RUG_BEDDING("침구"),
//	TEXTILE_RUG_BATH_TEXTILE("욕실 텍스타일"),
//	TEXTILE_RUG_CUSHION_COVER("쿠션/쿠션커버"),
//	TEXTILE_RUG_SEAT_PAD("의자방석/패드"),
//	TEXTILE_RUG_RUG("러그"),
//	TEXTILE_RUG_BLANKET_THROW("담요/스로우"),
//	TEXTILE_RUG_TABLE_TEXTILE("테이블 텍스타일"),
//	TEXTILE_RUG_KITCHEN_TEXTILE("주방 텍스타일"),
//	TEXTILE_RUG_OUTDOOR_CUSHION("야외용 쿠션"),
//	TEXTILE_RUG_KIDS_TEXTILE("어린이 텍스타일"),
//	TEXTILE_RUG_BABY_TEXTILE("영유아 텍스타일"),
//	TEXTILE_RUG_BATHROBE_PONCHO("목욕가운/판쵸우의"),
//	TEXTILE_RUG_LUMBAR_SUPPORT("허리 서포트"),
//	TEXTILE_RUG_FABRIC_SEWING("패브릭/재봉용품"),
//
//	// CURTAIN_BLIND
//	CURTAIN_BLIND_CURTAIN("커튼"),
//	CURTAIN_BLIND_BLIND("블라인드"),
//	CURTAIN_BLIND_ROD_RAIL_PARTS("커튼봉/레일/설치부품"),
//
//	// HOME_DECOR
//	HOME_DECOR_FRAME_POSTER("액자/그림/포스터"),
//	HOME_DECOR_MIRROR("거울"),
//	HOME_DECOR_CLOCK("시계"),
//	HOME_DECOR_VASE_OBJECT("화병/오브제"),
//	HOME_DECOR_CANDLE_HOLDER("향초/캔들/홀더"),
//	HOME_DECOR_POTPOURRI_FRAGRANCE("포푸리/홈프레그런스"),
//	HOME_DECOR_PLANT_FAKE("식물/조화"),
//	HOME_DECOR_PLANTER("화분"),
//	HOME_DECOR_DECORATIVE_ITEM("장식용품"),
//	HOME_DECOR_MEMO_BOARD_ORGANIZER("메모판/정리함"),
//	HOME_DECOR_STORAGE_BASKET("수납함/바구니"),
//	HOME_DECOR_GIFT_WRAP_BAG("선물 포장지/종이백"),
//	HOME_DECOR_CHRISTMAS_DECOR("크리스마스 장식"),
//
//	// KIDS
//	KIDS_GENERAL("어린이"),
//	KIDS_BABY("영유아"),
//
//	// BATHROOM
//	BATHROOM_SYSTEM("욕실 시스템"),
//	BATHROOM_VANITY_BASE("욕실 세면대하부장"),
//	BATHROOM_TALL_SHELF_UNIT("욕실 키큰장/선반유닛"),
//	BATHROOM_WALL_STORAGE("욕실 벽수납장"),
//	BATHROOM_STORAGE_BOX("욕실 수납 정리함"),
//	BATHROOM_STOOL_BENCH("욕실 스툴/벤치"),
//	BATHROOM_CART("욕실카트"),
//	BATHROOM_SHELF_TOWEL_RAIL("욕실 선반/수건레일"),
//	BATHROOM_MIRROR("욕실 거울"),
//	BATHROOM_ACCESSORY("욕실 용품"),
//	BATHROOM_LAUNDRY_SUPPLIES("욕실 세탁용품"),
//	BATHROOM_TEXTILE("욕실 텍스타일"),
//	BATHROOM_LIGHTING("욕실용 조명"),
//	BATHROOM_COUNTERTOP("세면대 상판"),
//	BATHROOM_SINK("욕실 세면기"),
//	BATHROOM_FAUCET("욕실 수전"),
//	BATHROOM_SHOWER("욕실 샤워기");
//
//	private final String description;

    BATHROOM_ACCESSORY,
    HOME_DECOR_DECORATIVE,

    // ======================
    // Furniture
    // ======================
    DESK,
    HEIGHT_ADJUSTABLE_DESK,
    MEETING_TABLE,
    OUTDOOR_TABLE,
    TABLE,
    TABLE_FRAME,
    TABLE_TOP,
    CHAIR,
    BAR_STOOL,
    STOOL,
    STEP_STOOL,

    // ======================
    // Lighting
    // ======================
    CEILING_LAMP,
    PENDANT_LAMP,
    FLOOR_LAMP,
    TABLE_LAMP,
    WALL_LAMP,
    WORK_LAMP,
    READING_LAMP,
    DECORATIVE_LIGHT,
    SPOTLIGHT,
    SYSTEM_LIGHTING,
    LED_BULB,

    // ======================
    // Fabric & Decor
    // ======================
    RUG,
    FLAT_WOVEN_RUG,
    LONG_PILE_RUG,
    SHORT_PILE_RUG,
    DOOR_MAT,
    CUSHION,
    CUSHION_COVER,
    ART_PRINT,
    CANVAS_PRINT,
    FRAME,
    TABLE_CLOTH,
    TABLE_MAT,
    TABLE_RUNNER,

    // ======================
    // Bedding & Bath
    // ======================
    BEDSPREAD,
    BLANKET,
    DUVET_COVER,
    FITTED_SHEET,
    MATTRESS_COVER,
    PILLOW_CASE,
    TOWEL,
    BATH_TOWEL,
    HAND_TOWEL,
    BATH_MAT,

    // ======================
    // Window
    // ======================
    CURTAIN,
    SHEER_CURTAIN,
    BLACKOUT_CURTAIN,
    BLACKOUT_BLIND,
    PLEATED_BLIND,
    ROLLER_BLIND,
    ROMAN_BLIND,
    VENETIAN_BLIND
}