package com.roome.roome.be.domain.product.dto.request;

import java.util.List;

public record CommitProductImagesRequest(
	Long sessionId,
	List<Item> items,
	Integer thumbnailOrder
) {
	public record Item(String objectKey, Integer order) {}
}
