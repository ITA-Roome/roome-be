package com.roome.roome.be.domain.product.dto.response;

import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.domain.product.entity.Product;

import lombok.Builder;

@Builder
public record ProductListItemResponse(
	Long id,
	String name,
	Integer price,
	String category,
	String productUrl,
	String thumbnailUrl,
	Long shopId,
	String shopName
) {
	public static ProductListItemResponse from(Product product, ImageUrlBuilder imageUrlBuilder) {
		String category = product.getCategory() != null ? product.getCategory().name() : null;
		String productUrl = product.getProductUrl() != null ? product.getProductUrl() : null;

		String thumbnailUrl = product.getThumbnailKey() != null
			? imageUrlBuilder.build(product.getThumbnailKey())
			: null;

		Long shopId = (product.getShop() != null) ? product.getShop().getId() : null;
		String shopName = (product.getShop() != null) ? product.getShop().getName() : null;

		return new ProductListItemResponse(
			product.getId(),
			product.getName(),
			product.getPrice(),
			category,
			productUrl,
			thumbnailUrl,
			shopId,
			shopName
		);
	}
}
