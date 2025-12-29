package com.roome.roome.be.domain.reference.dto.response;

import com.roome.roome.be.domain.product.dto.response.ProductTagInfo;

import java.util.Set;

public record ReferenceItemProductInfo(
        Long productId,
        String productName,
        Integer productPrice,
        String productUrl,
        String thumbnailUrl,
        Set<ProductTagInfo> tagList
) {}
