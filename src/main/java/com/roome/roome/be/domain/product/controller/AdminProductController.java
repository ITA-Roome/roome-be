package com.roome.roome.be.domain.product.controller;

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
import com.roome.roome.be.domain.product.dto.request.RegisterProductRequest;
import com.roome.roome.be.domain.product.dto.request.UpdateProductImagesRequest;
import com.roome.roome.be.domain.product.dto.request.UpdateProductRequest;
import com.roome.roome.be.domain.product.service.ProductImageService;
import com.roome.roome.be.domain.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/products")
@Tag(name = "Products(Admin)", description = "관리자 상품 API")
@RequiredArgsConstructor
public class AdminProductController {

	private final ProductService productService;
	private final ProductImageService productImageService;

	@PostMapping("/register")
	@Operation(summary = "상품 ,이미지, 태그를 등록")
	public ResponseEntity<ApiResponse<Long>> registerProduct(
		@Valid @RequestBody RegisterProductRequest registerProductRequest
	) {
		Long productId = productService.register(registerProductRequest);
		return ApiResponse.success(SuccessStatus.PRODUCT_REGISTER_SUCCESS, productId);
	}

	// 상품 수정
	@PatchMapping("/admin/products/{productId}")
	@Operation(summary = "상품 수정(통합)", description = "기본정보/태그/이미지를 한 번에 부분 수정합니다. images가 전달되면 전체 교체됩니다.")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 수정 성공", content = @Content)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품/샵이 존재하지 않음", content = @Content)
	public ResponseEntity<ApiResponse<Void>> updateProduct(
		@PathVariable Long productId,
		@Valid @RequestBody UpdateProductRequest updateProductRequest
	) {
		productService.updateProduct(productId, updateProductRequest);
		return ApiResponse.success(SuccessStatus.UPDATE_PRODUCT_SUCCESS);
	}

	//상품 이미지 교체
	@PatchMapping("/admin/products/{productId}/images")
	@Operation(
		summary = "상품 이미지 전체 교체",
		description = """
    전달한 items를 '최종 상태'로 간주하여 기존 이미지를 전부 갈아끼웁니다.
    - 추가: 새 objectKey를 items에 포함
    - 삭제: 기존에 있던 키를 items에서 제외
    - 순서변경: sortOrder로 원하는 순서로 보냄
    - 썸네일: thumbnailOrder 지정(없으면 0번)
    """
	)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 교체 성공", content = @Content)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품 없음", content = @Content)
	public ResponseEntity<ApiResponse<Void>> replaceImages(
		@PathVariable Long productId,
		@Valid @RequestBody UpdateProductImagesRequest request
	) {
		productImageService.replaceImages(productId, request);
		return ApiResponse.success(SuccessStatus.UPDATE_PRODUCT_IMAGES_SUCCESS);
	}

	//상품 삭제
	@DeleteMapping("/admin/products/{productId}")
	@Operation(
		summary = "상품 삭제(관리자)",
		description = "연관된 이미지/태그를 정리한 뒤 상품을 삭제합니다."
	)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 삭제 성공", content = @Content)
	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품 없음", content = @Content)
	public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId) {
		productService.deleteProduct(productId);
		return ApiResponse.success(SuccessStatus.DELETE_PRODUCT_SUCCESS);
	}
}