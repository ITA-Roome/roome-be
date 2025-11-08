package com.roome.roome.be.domain.shop.dto.request;

public record ShopUpdateRequest(
	String name,
	ShopRegisterRequest.ShopLogoRequest logo
){}
