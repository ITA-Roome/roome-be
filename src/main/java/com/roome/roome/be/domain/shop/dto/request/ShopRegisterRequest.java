package com.roome.roome.be.domain.shop.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ShopRegisterRequest (
	@NotBlank(message = "가게 이름은 필수입니다.")
	String name,

	@NotBlank(message = "로고 objectKey는 필수입니다.")
	String logoObjectKey

) { }