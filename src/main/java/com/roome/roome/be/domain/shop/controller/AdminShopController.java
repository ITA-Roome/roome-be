package com.roome.roome.be.domain.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.shop.dto.request.ShopRegisterRequest;
import com.roome.roome.be.domain.shop.dto.response.ShopRegisterResponse;
import com.roome.roome.be.domain.shop.service.ShopService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/shop")
@Tag(name = "Shop", description = "가게 API")
public class AdminShopController {

	private final ShopService shopService;

	@PostMapping
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가게 등록 성공", content = @Content)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content)
	@Operation(summary = "가게 등록 (관리자)")
	public ResponseEntity<ApiResponse<ShopRegisterResponse>> registerShop(
		@Valid @RequestBody ShopRegisterRequest shopRegisterRequest
	) {
		ShopRegisterResponse shopRegisterResponse = shopService.registerShop(shopRegisterRequest);
		return ApiResponse.success(SuccessStatus.REGISTER_SHOP_SUCCESS, shopRegisterResponse);
	}
}
