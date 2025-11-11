package com.roome.roome.be.domain.shop.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ShopUpdateRequest(
	String name,
	String logoObjectKey
){}
