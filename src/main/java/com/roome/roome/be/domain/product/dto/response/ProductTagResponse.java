package com.roome.roome.be.domain.product.dto.response;

import com.roome.roome.be.domain.product.enums.TagType;

public record ProductTagResponse(
	Long id,
	TagType tagType,
	String name
) {}