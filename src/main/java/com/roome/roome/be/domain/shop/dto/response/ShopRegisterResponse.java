package com.roome.roome.be.domain.shop.dto.response;

import com.roome.roome.be.domain.shop.entity.Shop;

public record ShopRegisterResponse(
	Long shopId,
	String name
) {
	public static ShopRegisterResponse from(Shop shop) {
		return new ShopRegisterResponse(shop.getId(), shop.getName());
	}
}