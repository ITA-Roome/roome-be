package com.roome.roome.be.domain.product.dto.response;

public record ProductImageResponse(
	String objectKey,
	String url,
	int sortOrder
) {}

