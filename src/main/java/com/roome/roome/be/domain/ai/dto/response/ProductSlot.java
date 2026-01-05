package com.roome.roome.be.domain.ai.dto.response;

import com.roome.roome.be.domain.product.enums.ProductType;

import java.util.List;

public record ProductSlot(
        List<ProductType> productTypes,
        List<String> productColors,
        Integer minBudget,
        Integer maxBudget
) {
}
