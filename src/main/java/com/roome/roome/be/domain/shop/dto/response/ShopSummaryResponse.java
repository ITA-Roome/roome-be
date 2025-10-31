package com.roome.roome.be.domain.shop.dto.response;

import com.roome.roome.be.domain.shop.entity.Shop;

public record ShopSummaryResponse(
	Long id,
	String name
) {
	public static ShopSummaryResponse from(Shop shop) {
		return new ShopSummaryResponse(
			shop.getId(),
			shop.getName()
		);
	}
}
