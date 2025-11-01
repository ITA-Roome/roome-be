package com.roome.roome.be.domain.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.shop.dto.request.ShopRegisterRequest;
import com.roome.roome.be.domain.shop.dto.request.ShopUpdateRequest;
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

    //가게 등록
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

	// 가게 정보 수정
	@PatchMapping("/{shopId}")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가게 수정 성공", content = @Content)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게가 존재하지 않는 경우", content = @Content)
	@Operation(summary = "가게 이름 수정 (관리자)")
	public ResponseEntity<ApiResponse<Void>> updateShop(
		@PathVariable Long shopId,
		@RequestBody ShopUpdateRequest shopUpdateRequest
	) {
		shopService.updateShop(shopId, shopUpdateRequest);
		return ApiResponse.success(SuccessStatus.UPDATE_SHOP_SUCCESS);
	}

	//가게 삭제
	@DeleteMapping("/{shopId}")
	@Operation(summary = "가게 삭제 (관리자)")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가게 삭제 성공", content = @Content)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게가 존재하지 않는 경우", content = @Content)
	public ResponseEntity<ApiResponse<Void>> deleteShop(
		@PathVariable Long shopId
	) {
		shopService.deleteShop(shopId);
		return ApiResponse.success(SuccessStatus.DELETE_SHOP_SUCCESS);
	}
}
