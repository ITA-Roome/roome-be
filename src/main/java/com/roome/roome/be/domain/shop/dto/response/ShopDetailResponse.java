package com.roome.roome.be.domain.shop.dto.response;

import com.roome.roome.be.domain.shop.entity.Shop;

public record ShopDetailResponse(
	Long id,
	String name,
	String logoUrl,
	String description
) {
	public static ShopDetailResponse from(Shop shop, String logoUrl) {
		return new ShopDetailResponse(
			shop.getId(),
			shop.getName(),
			logoUrl,
			shop.getDescription()
		);
	}
}