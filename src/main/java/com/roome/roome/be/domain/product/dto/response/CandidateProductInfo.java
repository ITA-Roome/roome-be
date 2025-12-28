package com.roome.roome.be.domain.product.dto.response;

import com.roome.roome.be.domain.product.entity.Tag;

import java.util.Set;

public record CandidateProductInfo(
        Long productId,
        String productName,
        Integer productPrice,
        String description,
        Set<String> productImageList,
        Set<Tag> tagList,
        String summary
) {
}
