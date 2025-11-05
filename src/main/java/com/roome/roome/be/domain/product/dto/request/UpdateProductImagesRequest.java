package com.roome.roome.be.domain.product.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProductImagesRequest(

	Long sessionId,
	@NotNull
	@Size(max = 30)
	List<Item> items,
	Integer thumbnailOrder
) {
	public record Item(
		@NotNull String objectKey,
		@NotNull Integer order
	) {}
}