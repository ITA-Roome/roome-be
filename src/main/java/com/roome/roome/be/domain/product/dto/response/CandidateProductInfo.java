package com.roome.roome.be.domain.product.dto.response;


import java.util.Set;

public record CandidateProductInfo(
        Long productId,
        String productName,
        Integer productPrice,
        String description,
        Set<String> productImageList,
        Set<ProductTagInfo> tagList
) {
}
