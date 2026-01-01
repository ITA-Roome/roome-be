package com.roome.roome.be.domain.product.dto.response;

public record RelatedProductResponse(
        Long productId,
        String name,
        String category,
        String description,
        Integer price,
        String imageUrl
) {
}
