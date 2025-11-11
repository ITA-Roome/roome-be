package com.roome.roome.be.domain.product.controller;

import java.util.List;

import com.roome.roome.be.domain.product.dto.response.ProductToggleLikeResponse;
import com.roome.roome.be.domain.product.dto.response.ProductToggleScrapResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.product.dto.response.ProductDetailResponse;
import com.roome.roome.be.domain.product.dto.response.ProductListItemResponse;
import com.roome.roome.be.domain.product.enums.Category;
import com.roome.roome.be.domain.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Products", description = "상품 API")
public class ProductController {

	private final ProductService productService;

	//상품 상세 조회
	@GetMapping("/{productId}")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 상세 조회 성공", content = @Content)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품이 존재하지 않음", content = @Content)
	@Operation(summary = "상품 상세 조회")
	public ResponseEntity<ApiResponse<ProductDetailResponse>> getDetail(
			@PathVariable Long productId,
			@AuthenticationPrincipal Long userId
	) {
		var dto = productService.getDetail(productId,userId);
		return ApiResponse.success(SuccessStatus.GET_PRODUCT_DETAIL, dto);
	}

	//상품 목록
	@GetMapping
	@Operation(
			summary = "상품 목록 조회",
			description = """
상품의 카테고리, 태그(색상, 소재 등), 가게, 검색어, 가격범위로 필터링하고 정렬·페이지네이션을 적용하여 상품 목록을 조회합니다.

[필터]
- category: 상품 카테고리 (예: FURNITURE, BEDDING ...)
- shopId: 특정 가게의 상품만 조회
- keyWord: **상품명(name)** 에서만 부분 일치 검색
- minPrice, maxPrice: 가격 범위 필터
- **tags: color, material, style, feature, mood (다중 지정 가능)**
- **match: 태그 매칭 방식 (any | all). 기본값 any**

[정렬 가능 필드]
- id, price, createdAt, popularity (있다면)

[예시]
- **(신규)** /api/products?category=DININGROOM_CHAIR&color=WHITE&material=WOOD&match=all
- **(수정)** /api/products?shopId=12&keyWord=의자&minPrice=20000&maxPrice=70000
- /api/products?sort=popularity,desc
"""
	)
	@Parameters({
			@Parameter(name = "shopId", description = "가게 ID. 지정 시 해당 가게의 상품만 조회", example = "1"),
			@Parameter(name = "category", description = "상품 카테고리"),
			@Parameter(name = "keyWord", description = "상품명(name)에서 부분 일치 검색", example = "테이블"),
			@Parameter(name = "minPrice", description = "최소 가격", example = "10000"),
			@Parameter(name = "maxPrice", description = "최대 가격", example = "100000"),

			@Parameter(
					name = "color",
					description = "색상 태그(다중 지정 가능). 예: color=WHITE&color=GREEN",
					array = @ArraySchema(schema = @Schema(type = "string"))
			),
			@Parameter(
					name = "material",
					description = "소재 태그(다중 지정 가능) 예: material=WOOD" ,
					array = @ArraySchema(schema = @Schema(type = "string"))
			),
			@Parameter(
					name = "style",
					description = "스타일 태그(다중 지정 가능) 예: style=MODERN",
					array = @ArraySchema(schema = @Schema(type = "string"))
			),
			@Parameter(
					name = "feature",
					description = "기능 태그(다중 지정 가능) 예: feature=WATERPROOF",
					array = @ArraySchema(schema = @Schema(type = "string"))
			),
			@Parameter(
					name = "mood",
					description = "무드 태그(다중 지정 가능) 예: mood=DECORATIVE",
					array = @ArraySchema(schema = @Schema(type = "string"))
			),
			@Parameter(
					name = "match",
					description = "태그 매칭 방식(any: 하나라도 일치, all: 전부 일치)",
					schema = @Schema(allowableValues = {"any", "all"}, defaultValue = "any")
			),
			@Parameter(name = "sort", description = "정렬 (예: price,asc | createdAt,desc"),
			@Parameter(name = "page", description = "페이지 번호", example = "0"),
			@Parameter(name = "size", description = "페이지 크기", example = "20")
	})
	public ResponseEntity<ApiResponse<Page<ProductListItemResponse>>> getProducts(
		@RequestParam(required = false) Long shopId,
		@RequestParam(required = false) Category category,
		@RequestParam(required = false, name = "color") List<String> colorTags,
        @RequestParam(required = false, name = "material") List<String> materialTags,
		@RequestParam(required = false, name = "style") List<String> styleTags,
		@RequestParam(required = false, name = "feature") List<String> featureTags,
		@RequestParam(required = false, name = "mood") List<String> moodTags,
		@RequestParam(required = false, defaultValue = "any") String match,
		@RequestParam(required = false) String keyWord,
		@RequestParam(required = false) Integer minPrice,
		@RequestParam(required = false) Integer maxPrice,
		@ParameterObject
		@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
	) {
		var page = productService.getList(shopId, category, colorTags, materialTags, styleTags, featureTags, moodTags, match, keyWord, minPrice, maxPrice, pageable);
		return ApiResponse.success(SuccessStatus.GET_PRODUCT_LIST, page);
	}

	// 상품 좋아요 기능 구현
	@PostMapping("{productId}/like")
	@Operation(
			summary = "상품 좋아요 토글",
			description = "이미 좋아요가 눌려 있으면 취소하고, 눌려 있지 않으면 좋아요를 추가합니다."
	)
	@Parameters({
			@Parameter(name = "productId", description = "좋아요를 누를 상품의 ID", example = "123"),
	})
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 토글 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductToggleLikeResponse.class)))
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않거나 상품이 존재하지 않음", content = @Content(mediaType = "application/json"))
	public ResponseEntity<ApiResponse<ProductToggleLikeResponse>> toggleProductLike(
			@PathVariable("productId") Long productId,
			@AuthenticationPrincipal Long userId
	) {
		ProductToggleLikeResponse response = productService.toggleProductLike(productId, userId);
		return ApiResponse.success(SuccessStatus.CREATE_PRODUCT_LIKE,response);
	}

	// 상품 스크랩 기능 구현
	@PostMapping("{productId}/scrap")
	@Operation(
			summary = "상품 스크랩 토글",
			description = "이미 스크랩이 되어 있으면 취소하고, 되어 있지 않으면 스크랩에 추가합니다."
	)
	@Parameters({
			@Parameter(name = "productId", description = "스크랩할 상품의 ID", example = "123"),
	})
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스크랩 토글 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductToggleLikeResponse.class)))
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않거나 상품이 존재하지 않음", content = @Content(mediaType = "application/json"))
	public ResponseEntity<ApiResponse<ProductToggleScrapResponse>> toggleProductScrap(
			@PathVariable("productId") Long productId,
			@AuthenticationPrincipal Long userId
	) {
		ProductToggleScrapResponse response = productService.toggleProductScrap(productId, userId);
		return ApiResponse.success(SuccessStatus.CREATE_PRODUCT_LIKE,response);
	}
}
