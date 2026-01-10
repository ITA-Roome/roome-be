package com.roome.roome.be.domain.product.controller;

import java.util.List;

import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.reference.dto.response.RelatedReferenceResponse;
import com.roome.roome.be.domain.reference.service.ReferenceService;
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
import com.roome.roome.be.domain.product.service.ProductService;
import com.roome.roome.be.domain.search.service.SearchService;

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
	private final SearchService searchService;
	private final ReferenceService referenceService;

	//상품 상세 조회
	@GetMapping("/{productId}")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 상세 조회 성공", content = @Content)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품이 존재하지 않음", content = @Content)
	@Operation(summary = "상품 상세 조회")
	public ResponseEntity<ApiResponse<ProductDetailResponse>> getDetail(
			@PathVariable Long productId,
			@AuthenticationPrincipal Long userId
	) {
		var dto = productService.getProductDetail(productId, userId);
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
- **tags: color, material, style, feature, mood, usage (다중 지정 가능)**
- **match: 태그 매칭 방식 (any | all). 기본값 any**

[정렬 가능 필드]
- id, price, createdAt, popularity

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
				name = "usage",
				description = "사용되는 공간 및 용도(다중 지정 가능) 예: usage=BATHROOM",
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
			@RequestParam(required = false) ProductCategory category,
			@RequestParam(required = false, name = "color") List<String> colorTags,
			@RequestParam(required = false, name = "material") List<String> materialTags,
			@RequestParam(required = false, name = "style") List<String> styleTags,
			@RequestParam(required = false, name = "feature") List<String> featureTags,
			@RequestParam(required = false, name = "mood") List<String> moodTags,
			@RequestParam(required = false, name = "usage") List<String> usageTags,
			@RequestParam(required = false, defaultValue = "any") String match,
			@RequestParam(required = false) String keyWord,
			@RequestParam(required = false) Integer minPrice,
			@RequestParam(required = false) Integer maxPrice,
			@RequestParam(required = false) List<String> sort,
			@AuthenticationPrincipal Long userId,

			@ParameterObject
			@PageableDefault(size = 20) Pageable pageable
	) {
		searchService.recordSearch(keyWord, userId);
		var page = productService.getList(shopId, category, colorTags, materialTags, styleTags, featureTags, moodTags,usageTags, match, keyWord, minPrice, maxPrice, pageable, userId);
		return ApiResponse.success(SuccessStatus.GET_PRODUCT_LIST, page);
	}

	// 상품 기반 연관 레퍼런스 조회
	@GetMapping("/{productId}/related-references")
	@Operation(
			summary = "상품 연관 레퍼런스 조회",
			description = "특정 상품의 태그와 매칭되는 레퍼런스 목록을 조회합니다. 태그 매칭 개수가 많을수록 우선 표시됩니다."
	)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "연관 레퍼런스 조회 성공",
			content = @Content(schema = @Schema(implementation = RelatedReferenceResponse.class))
	)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "상품이 존재하지 않음",
			content = @Content
	)
	public ResponseEntity<ApiResponse<List<RelatedReferenceResponse>>> getRelatedReferences(
			@PathVariable Long productId,
			@Parameter(description = "조회할 레퍼런스 최대 개수", example = "10")
			@RequestParam(defaultValue = "10") int limit
	) {
		// 상품 존재 여부 확인
		productService.getProductById(productId);

		var references = referenceService.getRelatedReferences(productId, limit);
		return ApiResponse.success(SuccessStatus.GET_RELATED_REFERENCES_SUCCESS, references);
	}
}
