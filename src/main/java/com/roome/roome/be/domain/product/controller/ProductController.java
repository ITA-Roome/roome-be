package com.roome.roome.be.domain.product.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.product.dto.request.UpdateProductImagesRequest;
import com.roome.roome.be.domain.product.dto.request.UpdateProductRequest;
import com.roome.roome.be.domain.product.dto.response.ProductDetailResponse;
import com.roome.roome.be.domain.product.dto.response.ProductListItemResponse;
import com.roome.roome.be.domain.product.enums.Category;
import com.roome.roome.be.domain.product.enums.Color;
import com.roome.roome.be.domain.product.service.ProductImageService;
import com.roome.roome.be.domain.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 상세 조회 성공", content = @Content)
	@Operation(
		summary = "상품 목록 조회",
		description = """
	카테고리(category)와 색상(color)으로 필터링하고, 정렬,페이지네이션을 적용하여 상품 목록을 조회합니다.
	정렬 가능: id, price, createdAt
	예시:
	- /products?category=BEDROOM_BED&color=WHITE&sort=price,asc&page=0&size=20
	- /products?color=GRAY&sort=createdAt,desc
	"""
	)
	public ResponseEntity<ApiResponse<Page<ProductListItemResponse>>> getList(
		@RequestParam(required = false) Category category,
		@RequestParam(required = false) Color color,
		@ParameterObject
		@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
		Pageable pageable
	) {
		var page = productService.getList(category, color, pageable);
		return ApiResponse.success(SuccessStatus.GET_PRODUCT_LIST, page);
	}
}