package com.roome.roome.be.domain.product.dto.response;

import com.roome.roome.be.domain.product.entity.Tag;
import com.roome.roome.be.domain.product.enums.ProductCategory;

import java.time.LocalDateTime;
import java.util.Set;

public record CommonProductInfo(
        Long id,
        String name,
        ProductCategory category,
        Integer price,
        String description,
        String productUrl,
        String thumbnailKey,
        Set<String> imageList,
        Set<Tag> tagList,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
