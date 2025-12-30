package com.roome.roome.be.domain.shop.dto.response;

import com.roome.roome.be.domain.shop.entity.Shop;

public record ShopSummaryResponse(
	Long id,
	String name,
	String logoUrl,
	String description
) {
	public static ShopSummaryResponse from(Shop shop, String logoUrl) {
		return new ShopSummaryResponse(
			shop.getId(),
			shop.getName(),
			logoUrl,
			shop.getDescription()
		);
	}
}
