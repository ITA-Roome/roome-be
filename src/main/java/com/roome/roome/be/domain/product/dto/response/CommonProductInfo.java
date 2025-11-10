package com.roome.roome.be.domain.product.dto.response;

import com.roome.roome.be.domain.product.entity.Tag;
import com.roome.roome.be.domain.product.enums.Category;
import com.roome.roome.be.domain.product.enums.Color;

import java.time.LocalDateTime;
import java.util.List;

public record CommonProductInfo(
        Long id,
        String name,
        Category category,
        Color color,
        Integer price,
        String description,
        String productUrl,
        String thumbnailKey,
        List<String> imageList,
        List<Tag> tagList,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
