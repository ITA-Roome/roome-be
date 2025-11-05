package com.roome.roome.be.domain.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.product.dto.request.RegisterProductRequest;
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
public class ProductAdminController {

	private final ProductService productService;

	@PostMapping("/register")
	@Operation(summary = "상품 ,이미지, 태그를 등록")
	public ResponseEntity<ApiResponse<Long>> registerProduct(
		@Valid @RequestBody RegisterProductRequest registerProductRequest
	) {
		Long productId = productService.register(registerProductRequest);
		return ApiResponse.success(SuccessStatus.PRODUCT_REGISTER_SUCCESS, productId);
	}
}