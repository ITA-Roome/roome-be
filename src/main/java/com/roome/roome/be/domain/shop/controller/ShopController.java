package com.roome.roome.be.domain.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.shop.dto.request.ShopRegisterRequest;
import com.roome.roome.be.domain.shop.dto.response.ShopDetailResponse;
import com.roome.roome.be.domain.shop.dto.response.ShopRegisterResponse;
import com.roome.roome.be.domain.shop.service.ShopService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
@Tag(name = "Shop", description = "일반 조회 가게 API")
public class ShopController {

	private final ShopService shopService;

    //가게 상세 조회
	@GetMapping("/{shopId}")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가게 상세 조회 성공", content = @Content)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content)
	@Operation(summary = "가게 상세 조회")
	public ResponseEntity<ApiResponse<ShopDetailResponse>> getShop(
		@PathVariable Long shopId
	) {
		return ApiResponse.success(
			SuccessStatus.GET_SHOP_DETAIL_SUCCESS,
			shopService.getShopDetail(shopId)
		);
	}


}
