package com.roome.roome.be.domain.shop.dto.response;

import com.roome.roome.be.domain.shop.entity.Shop;

public record ShopDetailResponse(
	Long id,
	String name
) {
	public static ShopDetailResponse from(Shop shop) {
		return new ShopDetailResponse(
			shop.getId(),
			shop.getName()
		);
	}
}