package com.roome.roome.be.domain.product.dto.response;

import com.roome.roome.be.domain.product.entity.Tag;
import com.roome.roome.be.domain.product.enums.TagType;

public record ProductTagResponse(
	Long id,
	TagType tagType,
	String name
) {
	public static ProductTagResponse from(Tag tag) {
		return new ProductTagResponse(
				tag.getId(),
				tag.getType(),
				tag.getName()
		);
	}
}