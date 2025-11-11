package com.roome.roome.be.domain.shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.shop.dto.response.ShopDetailResponse;
import com.roome.roome.be.domain.shop.dto.response.ShopListResponse;
import com.roome.roome.be.domain.shop.service.ShopService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shops")
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

	// 가게 목록 조회
	@GetMapping
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가게 상세 조회 성공", content = @Content)
	@Operation(summary = "가게 목록 조회: 페이지 최근 가게부터 보여줌(역순)")
	public ResponseEntity<ApiResponse<ShopListResponse>> getShops(
		@Parameter(description = "조회할 페이지 번호 (0부터 시작)", example = "0")
		@RequestParam(defaultValue = "0") int page,
		@Parameter(description = "페이지당 항목 개수", example = "10")
		@RequestParam(defaultValue = "10") int size,
		@Parameter(description = "가게 이름 검색어(선택사항), 포함된 검색어가 있으면 반환", example = "이케아")
		@RequestParam(required = false) String name
	) {
		ShopListResponse shopListResponse = shopService.getShopList(page, size, name);
		return ApiResponse.success(SuccessStatus.GET_SHOP_LIST_SUCCESS, shopListResponse);
	}

}
