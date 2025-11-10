package com.roome.roome.be.domain.product.dto.response;

import com.roome.roome.be.domain.product.enums.TagType;

public record ProductTagInfo(
        Long id,
        String name,
        TagType type
){}

