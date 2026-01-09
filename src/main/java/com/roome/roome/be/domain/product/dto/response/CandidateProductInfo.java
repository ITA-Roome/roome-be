package com.roome.roome.be.domain.product.dto.response;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.ProductImage;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public record CandidateProductInfo(
        Long productId,
        String productName,
        Integer productPrice,
        String description,
        Set<String> productImageList, // [이미지 리스트]
        Set<ProductTagInfo> tagList
) {
    public static CandidateProductInfo from(Product product) {
        return new CandidateProductInfo(
                product.getId(),
                product.getName(),
                product.getPrice(),

                product.getDescription(),

                product.getProductImageList() == null
                        ? Collections.emptySet()
                        : product.getProductImageList().stream()
                        .map(ProductImage::getObjectKey)
                        .collect(Collectors.toSet()),

                product.getProductTagList() == null
                        ? Collections.emptySet()
                        : product.getProductTagList().stream()
                        .map(ProductTagInfo::from)
                        .collect(Collectors.toSet())
        );
    }
}