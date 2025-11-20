package com.roome.roome.be.domain.admin.controller;

import com.roome.roome.be.common.response.PageResponse;
import com.roome.roome.be.common.s3.dto.request.PresignedUrlRequest;
import com.roome.roome.be.common.s3.dto.response.PresignedUrlBatchResponse;
import com.roome.roome.be.common.s3.enums.StorageScope;
import com.roome.roome.be.common.s3.service.S3Service;
import com.roome.roome.be.domain.admin.dto.request.AdminInquiryAnswerRequest;
import com.roome.roome.be.domain.admin.dto.request.AdminInquirySearchConditionRequest;
import com.roome.roome.be.domain.admin.service.AdminService;
import com.roome.roome.be.domain.inquiry.dto.response.AdminInquiryResponse;
import com.roome.roome.be.domain.inquiry.enums.InquiryStatus;
import com.roome.roome.be.domain.inquiry.enums.InquiryType;
import com.roome.roome.be.domain.shop.dto.request.ShopRegisterRequest;
import com.roome.roome.be.domain.shop.dto.request.ShopUpdateRequest;
import com.roome.roome.be.domain.shop.dto.response.ShopRegisterResponse;
import com.roome.roome.be.domain.shop.service.ShopService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자 API")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ProductService productService;
    private final ProductImageService productImageService;
    private final ShopService shopService;
    private final S3Service s3Service;

    @PostMapping("/products/register")
    @Operation(summary = "상품 ,이미지, 태그를 등록")
    public ResponseEntity<ApiResponse<Long>> registerProduct(
            @Valid @RequestBody RegisterProductRequest registerProductRequest
    ) {
        Long productId = productService.register(registerProductRequest);
        return ApiResponse.success(SuccessStatus.PRODUCT_REGISTER_SUCCESS, productId);
    }

    // 상품 수정
    @PatchMapping("/products/{productId}")
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
    @PatchMapping("/products/{productId}/images")
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
            @Valid @RequestBody UpdateProductImagesRequest updateProductImagesRequest
    ) {
        productImageService.replaceImages(productId, updateProductImagesRequest);
        return ApiResponse.success(SuccessStatus.UPDATE_PRODUCT_IMAGES_SUCCESS);
    }

    //상품 삭제
    @DeleteMapping("/products/{productId}")
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

    //가게 등록
    @PostMapping("/shops")
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
    @PatchMapping("/shops/{shopId}")
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
    @DeleteMapping("/shops/{shopId}")
    @Operation(summary = "가게 삭제 (관리자)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가게 삭제 성공", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게가 존재하지 않는 경우", content = @Content)
    public ResponseEntity<ApiResponse<Void>> deleteShop(
            @PathVariable Long shopId
    ) {
        shopService.deleteShop(shopId);
        return ApiResponse.success(SuccessStatus.DELETE_SHOP_SUCCESS);
    }

    //상품 등록 전 사진 임시저장을 위한 api
    @PostMapping("/s3/uploads/{sessionId}/images/presigned")
    @Operation(summary = "세션 업로드용 Presigned URL(여러장 가능), sessionId는 프론트에서 임의 랜덤으로 만들어주시면 됩니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 파일 형식이거나 용량 제한(5MB)을 초과한 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한이 없는 경우", content = @Content)
    public ResponseEntity<ApiResponse<List<PresignedUrlBatchResponse>>> generatePresignedUrls(
            @PathVariable Long sessionId,
            @RequestBody List<PresignedUrlRequest> files
    ) {
        var responses = s3Service.generatePresignedPutUrls(StorageScope.PRODUCT_DETAIL, sessionId, files);
        return ApiResponse.success(SuccessStatus.S3_PRESIGNED_ISSUE_SUCCESS, responses);
    }

    // 관리자 전용 문의 답변 작성
    @PostMapping("/inquiries/{inquiryId}/answer")
    @Operation(summary = "문의 답변 작성",description = "관리자 전용 문의 답변 작성")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문의 답변 작성 성공", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한이 없는 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "문의 내역이 존재하지 않는 경우", content = @Content)
    public ResponseEntity<ApiResponse<Void>> registerInquiryAnswer(
            @AuthenticationPrincipal Long userId,
            @PathVariable("inquiryId") Long inquiryId,
            @Valid @RequestBody AdminInquiryAnswerRequest request
    ){
        adminService.registerAdminInquiryAnswer(userId, inquiryId, request);
        return ApiResponse.success(SuccessStatus.REGISTER_INQUIRY_ANSWER_SUCCESS);
    }

    // 관리자 전용 문의 내역 전체 조회
    @GetMapping("/inquiries")
    @Operation(summary = "문의하기 내역 전체 조회", description = "관리자 전용 문의 내역 전체 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문의 내역 전체 조회 성공", content = @Content(schema = @Schema(implementation = AdminInquiryResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한이 없는 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 답변을 등록한 경우", content = @Content)
    public ResponseEntity<ApiResponse<PageResponse<AdminInquiryResponse>>> getInquiryList(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false)InquiryStatus status,
            @RequestParam(required = false)InquiryType type,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ){

        Page<AdminInquiryResponse> response = adminService.getAdminInquiryList(userId, AdminInquirySearchConditionRequest.of(keyword, status,type, page, size));
        return ApiResponse.success(SuccessStatus.GET_INQUIRY_LIST_SUCCESS, PageResponse.from(response));
    }
}