package com.roome.roome.be.domain.shop.dto.response;

import com.roome.roome.be.domain.shop.entity.Shop;

public record ShopRegisterResponse(
	Long shopId,
	String name,
	String logoUrl,
	String description
) {
	public static ShopRegisterResponse from(Shop shop, String logoUrl) {
		return new ShopRegisterResponse(shop.getId(), shop.getName(), logoUrl, shop.getDescription());
	}
}