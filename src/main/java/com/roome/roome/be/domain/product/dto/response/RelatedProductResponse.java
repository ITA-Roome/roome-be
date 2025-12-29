package com.roome.roome.be.domain.product.dto.response;

import com.roome.roome.be.domain.product.enums.ProductCategory;

public record RelatedProductResponse(
        Long productId,
        String name,
        ProductCategory category,
        String description,
        Integer price,
        String imageUrl
) {
}
