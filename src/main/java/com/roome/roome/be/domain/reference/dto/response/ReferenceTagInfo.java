package com.roome.roome.be.domain.reference.dto.response;

import com.roome.roome.be.domain.product.enums.TagType;

public record ReferenceTagInfo(
        Long id,
        String name,
        TagType type
) {
}
