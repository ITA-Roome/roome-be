package com.roome.roome.be.domain.product.dto.response;

import com.roome.roome.be.domain.product.enums.Category;

public record RelatedProductResponse(
        Long productId,
        String name,
        Category category,
        String description,
        Integer price,
        String imageUrl
) {
}
