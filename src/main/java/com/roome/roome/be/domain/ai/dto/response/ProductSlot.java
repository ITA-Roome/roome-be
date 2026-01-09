package com.roome.roome.be.domain.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.product.enums.ProductType;

import java.util.List;

public record ProductSlot(
        ProductType productType,
        @JsonAlias("productCategory")
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        List<ProductCategory> productCategories,
        List<String> productColors,
        Integer minBudget,
        Integer maxBudget
) {
}