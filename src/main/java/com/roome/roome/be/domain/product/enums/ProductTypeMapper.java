package com.roome.roome.be.domain.product.enums;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductTypeMapper {

    private static final Map<ProductType, List<ProductCategory>> MAPPING = Map.of(

            // ======================
            // Furniture
            // ======================
            ProductType.FURNITURE, List.of(
                    ProductCategory.DESK,
                    ProductCategory.HEIGHT_ADJUSTABLE_DESK,
                    ProductCategory.MEETING_TABLE,
                    ProductCategory.OUTDOOR_TABLE,
                    ProductCategory.TABLE,
                    ProductCategory.TABLE_FRAME,
                    ProductCategory.TABLE_TOP,
                    ProductCategory.CHAIR,
                    ProductCategory.BAR_STOOL,
                    ProductCategory.STOOL,
                    ProductCategory.STEP_STOOL
            ),

            // ======================
            // Lighting
            // ======================
            ProductType.LIGHTING, List.of(
                    ProductCategory.CEILING_LAMP,
                    ProductCategory.PENDANT_LAMP,
                    ProductCategory.FLOOR_LAMP,
                    ProductCategory.TABLE_LAMP,
                    ProductCategory.WALL_LAMP,
                    ProductCategory.WORK_LAMP,
                    ProductCategory.READING_LAMP,
                    ProductCategory.DECORATIVE_LIGHT,
                    ProductCategory.SPOTLIGHT,
                    ProductCategory.SYSTEM_LIGHTING,
                    ProductCategory.LED_BULB
            ),

            // ======================
            // Fabric & Decor
            // ======================
            ProductType.FABRIC_DECOR, List.of(
                    ProductCategory.RUG,
                    ProductCategory.FLAT_WOVEN_RUG,
                    ProductCategory.LONG_PILE_RUG,
                    ProductCategory.SHORT_PILE_RUG,
                    ProductCategory.DOOR_MAT,
                    ProductCategory.CUSHION,
                    ProductCategory.CUSHION_COVER,
                    ProductCategory.ART_PRINT,
                    ProductCategory.CANVAS_PRINT,
                    ProductCategory.FRAME,
                    ProductCategory.TABLE_CLOTH,
                    ProductCategory.TABLE_MAT,
                    ProductCategory.TABLE_RUNNER
            ),

            // ======================
            // Bedding & Bath
            // ======================
            ProductType.BEDDING_BATH, List.of(
                    ProductCategory.BEDSPREAD,
                    ProductCategory.BLANKET,
                    ProductCategory.DUVET_COVER,
                    ProductCategory.FITTED_SHEET,
                    ProductCategory.MATTRESS_COVER,
                    ProductCategory.PILLOW_CASE,
                    ProductCategory.TOWEL,
                    ProductCategory.BATH_TOWEL,
                    ProductCategory.HAND_TOWEL,
                    ProductCategory.BATH_MAT
            ),

            // ======================
            // Window
            // ======================
            ProductType.WINDOW, List.of(
                    ProductCategory.CURTAIN,
                    ProductCategory.SHEER_CURTAIN,
                    ProductCategory.BLACKOUT_CURTAIN,
                    ProductCategory.BLACKOUT_BLIND,
                    ProductCategory.PLEATED_BLIND,
                    ProductCategory.ROLLER_BLIND,
                    ProductCategory.ROMAN_BLIND,
                    ProductCategory.VENETIAN_BLIND
            )
    );

    public static List<ProductCategory> getProductCategoryList(List<ProductType> productTypeList) {
        if (productTypeList == null || productTypeList.isEmpty()) {
            return List.of();
        }

        return productTypeList.stream()
                .flatMap(productType -> MAPPING.getOrDefault(productType, List.of()).stream())
                .distinct()
                .toList();
    }

}
