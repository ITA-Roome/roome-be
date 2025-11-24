package com.roome.roome.be.domain.product.dto.response;

import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.domain.product.entity.ProductImage;

public record ProductImageResponse(
	String objectKey,
	String url,
	int sortOrder
) {
	public static ProductImageResponse from(ProductImage productImage, ImageUrlBuilder imageUrlBuilder) {
		return new ProductImageResponse(
				productImage.getObjectKey(),
				imageUrlBuilder.build(productImage.getObjectKey()),
				productImage.getSortOrder()
		);
	}
}

