package com.roome.roome.be.domain.product.dto.response;

import com.roome.roome.be.domain.product.entity.ProductTag;
import com.roome.roome.be.domain.product.enums.TagType;

public record ProductTagInfo(
        Long id,
        String name,
        TagType type
){
    public static ProductTagInfo from(ProductTag productTag) {
        return new ProductTagInfo(
                productTag.getTag().getId(),
                productTag.getTag().getName(),
                productTag.getTag().getType()
        );
    }
}

