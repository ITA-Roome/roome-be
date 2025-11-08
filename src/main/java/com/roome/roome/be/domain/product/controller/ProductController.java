package com.roome.roome.be.domain.product.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/products")
@Tag(name = "Products", description = "상품 API")
public class ProductController {

	private final ProductService productService;

	//상품 상세 조회
	@GetMapping("/{productId}")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 상세 조회 성공", content = @Content)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품이 존재하지 않음", content = @Content)
	@Operation(summary = "상품 상세 조회")
	public ResponseEntity<ApiResponse<ProductDetailResponse>> getDetail(@PathVariable Long productId) {
		var dto = productService.getDetail(productId);
		return ApiResponse.success(SuccessStatus.GET_PRODUCT_DETAIL, dto);
	}

	//상품 목록
	@GetMapping
	@Operation(
		summary = "상품 목록 조회",
		description = """
카테고리, 색상, 가게, 검색어, 가격범위로 필터링하고 정렬·페이지네이션을 적용하여 상품 목록을 조회합니다.

[필터]
- category: 상품 카테고리 (예: FURNITURE, BEDDING ...)
- color: 색상 태그. 다중 지정 가능 (예: color=WHITE&color=GREEN)
- match: 색상 매칭 방식 (any | all). 기본값 any
- shopId: 특정 가게의 상품만 조회
- keyWord: **상품명(name)** 에서만 부분 일치 검색
- minPrice, maxPrice: 가격 범위 필터

[정렬 가능 필드]
- id, price, createdAt, popularity (있다면)

[예시]
- /api/products?category=FURNITURE&color=WHITE&color=GREEN&match=any&sort=price,asc&page=0&size=20
- /api/products?shopId=12&q=의자&minPrice=20000&maxPrice=70000&color=BEIGE&color=BROWN&match=all&sort=createdAt,desc
- /api/products?sort=popularity,desc
"""
	)
	@Parameters({
		@Parameter(name = "shopId", description = "가게 ID. 지정 시 해당 가게의 상품만 조회", example = "12"),
		@Parameter(name = "category", description = "상품 카테고리"),
		@Parameter(
			name = "color",
			description = "색상 태그(다중 지정 가능). 예: color=WHITE&color=GREEN",
			array = @ArraySchema(schema = @Schema(type = "string"))
		),
		@Parameter(
			name = "match",
			description = "색상 매칭 방식(any: 하나라도 일치, all: 전부 일치)",
			schema = @Schema(allowableValues = {"any", "all"}, defaultValue = "any")
		),
		@Parameter(name = "keyWord", description = "상품명(name)에서 부분 일치 검색", example = "의자"),
		@Parameter(name = "minPrice", description = "최소 가격", example = "10000"),
		@Parameter(name = "maxPrice", description = "최대 가격", example = "50000"),
		@Parameter(name = "sort", description = "정렬 (예: price,asc | createdAt,desc"),
		@Parameter(name = "page", description = "페이지 번호", example = "0"),
		@Parameter(name = "size", description = "페이지 크기", example = "20")
	})
	public ResponseEntity<ApiResponse<Page<ProductListItemResponse>>> getProducts(
		@RequestParam(required = false) Long shopId,
		@RequestParam(required = false) Category category,
		@RequestParam(required = false, name = "color") List<String> colorNames,
		@RequestParam(required = false, defaultValue = "any") String match,
		@RequestParam(required = false) String keyWord,
		@RequestParam(required = false) Integer minPrice,
		@RequestParam(required = false) Integer maxPrice,
		@ParameterObject
		@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
	) {
		var page = productService.getList(shopId, category, colorNames, match, keyWord, minPrice, maxPrice, pageable);
		return ApiResponse.success(SuccessStatus.GET_PRODUCT_LIST, page);
	}
}