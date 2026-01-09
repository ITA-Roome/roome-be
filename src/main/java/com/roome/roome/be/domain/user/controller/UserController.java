package com.roome.roome.be.domain.user.controller;

import com.roome.roome.be.domain.reference.service.ReferenceService;
import com.roome.roome.be.domain.user.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.response.PageResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.inquiry.enums.InquiryStatus;
import com.roome.roome.be.domain.inquiry.enums.InquiryType;
import com.roome.roome.be.domain.inquiry.service.InquiryService;
import com.roome.roome.be.domain.product.dto.response.ProductToggleLikeResponse;
import com.roome.roome.be.domain.product.dto.response.ProductToggleScrapResponse;
import com.roome.roome.be.domain.reference.dto.response.ReferenceToggleLikeResponse;
import com.roome.roome.be.domain.reference.dto.response.ReferenceToggleScrapResponse;
import com.roome.roome.be.domain.user.dto.request.UpdateUserProfileRequest;
import com.roome.roome.be.domain.user.dto.request.UserInquirySearchCondition;
import com.roome.roome.be.domain.user.dto.request.UserOnboardingRequest;
import com.roome.roome.be.domain.user.service.UserLikeService;
import com.roome.roome.be.domain.user.service.UserOnboardingService;
import com.roome.roome.be.domain.user.service.UserScrapService;
import com.roome.roome.be.domain.user.service.UserService;
import com.roome.roome.be.domain.user.service.UserViewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User", description = "유저 API")
public class UserController {

    private final InquiryService inquiryService;
    private final UserLikeService userLikeService;
    private final UserOnboardingService userOnboardingService;
    private final UserService userService;
    private final UserScrapService userScrapService;
    private final UserViewService userViewService;
    private final ReferenceService referenceService;

    @GetMapping("/profile")
    @Operation(summary = "유저 프로필 조회", description = "유저 계정 정보 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저 프로필 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않음", content = @Content)
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
            @AuthenticationPrincipal Long userId
    ) {
        UserProfileResponse response = userService.getUserProfile(userId);
        return ApiResponse.success(SuccessStatus.GET_USER_PROFILE_SUCCESS, response);
    }

    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저 프로필 수정", description = "유저 닉네임 또는 프로필 이미지 수정")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저 프로필 수정 성공", content = @Content(mediaType = "application/json", schema = @Schema()))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않음", content = @Content)
    public ResponseEntity<ApiResponse<Void>> updateUserProfile(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        userService.updateUserProfile(userId,request);
        return ApiResponse.success(SuccessStatus.UPDATE_USER_PROFILE_SUCCESS);
    }

    @PostMapping("/onboarding")
    @Operation(summary = "유저 온보딩 정보 저장")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "온보딩 정보 저장 성공", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않음", content = @Content)
    public ResponseEntity<ApiResponse<Void>> saveUserOnboarding(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserOnboardingRequest userOnboardingRequest
    ) {
        userOnboardingService.saveUserOnboarding(userId, userOnboardingRequest);
        return ApiResponse.success(SuccessStatus.SAVE_USER_ONBOARDING_SUCCESS);
    }

    @GetMapping("/onboarding/existence")
    @Operation(summary = "유저 온보딩 존재 여부 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "온보딩 존재 여부 조회 성공", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않음", content = @Content)
    public ResponseEntity<ApiResponse<UserOnboardingExistResponse>> checkOnboardingExistence(
            @AuthenticationPrincipal Long userId
    ) {
        UserOnboardingExistResponse response = userOnboardingService.checkExistence(userId);
        return ApiResponse.success(SuccessStatus.CHECK_USER_ONBOARDING_EXISTENCE_SUCCESS, response);
    }

    // 상품 좋아요 기능 구현
    @PostMapping("/likes/product/{productId}")
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
        ProductToggleLikeResponse response = userLikeService.toggleProductLike(productId, userId);
        return ApiResponse.success(SuccessStatus.CREATE_PRODUCT_LIKE,response);
    }

    @GetMapping("/likes/product")
    @Operation(summary = "유저가 좋아요를 누른 상품 리스트 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 상품 리스트 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserLikeProductListResponse.class)))
    public ResponseEntity<ApiResponse<UserLikeProductListResponse>> getUserLikedProductList(
            @AuthenticationPrincipal Long userId
    ) {
        UserLikeProductListResponse response = userLikeService.getUserLikedProductList(userId);
        return ApiResponse.success(SuccessStatus.GET_USER_LIKE_PRODUCT_LIST_SUCCESS, response);
    }

    // 레퍼런스 좋아요 기능 구현
    @PostMapping("/likes/reference/{referenceId}")
    @Operation(
        summary = "레퍼런스 좋아요 토글",
        description = "이미 좋아요가 눌려 있으면 취소하고, 눌려 있지 않으면 좋아요를 추가합니다."
    )
    @Parameters({
        @Parameter(name = "referenceId", description = "좋아요를 누를 레퍼런스의 ID", example = "123"),
    })
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 토글 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReferenceToggleLikeResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않거나 레퍼런스가 존재하지 않음", content = @Content(mediaType = "application/json"))
    public ResponseEntity<ApiResponse<ReferenceToggleLikeResponse>> toggleReferenceLike(
        @PathVariable("referenceId") Long referenceId,
        @AuthenticationPrincipal Long userId
    ) {
        ReferenceToggleLikeResponse response = userLikeService.toggleReferenceLike(referenceId, userId);
        return ApiResponse.success(SuccessStatus.CREATE_REFERENCE_LIKE,response);
    }

    @GetMapping("/likes/reference")
    @Operation(summary = "유저가 좋아요를 누른 레퍼런스 리스트 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 레퍼런스 리스트 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserLikeReferenceListResponse.class)))
    public ResponseEntity<ApiResponse<UserLikeReferenceListResponse>> getUserLikedReferenceList(
        @AuthenticationPrincipal Long userId
    ) {
        UserLikeReferenceListResponse response = userLikeService.getUserLikedReferenceList(userId);
        return ApiResponse.success(SuccessStatus.GET_USER_LIKE_REFERENCE_LIST_SUCCESS, response);
    }

    // 상품 스크랩 기능 구현
    @PostMapping("/scraps/product/{productId}")
    @Operation(
            summary = "상품 스크랩 토글",
            description = "이미 스크랩이 되어 있으면 취소하고, 되어 있지 않으면 스크랩에 추가합니다."
    )
    @Parameters({
            @Parameter(name = "productId", description = "스크랩할 상품의 ID", example = "123"),
    })
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스크랩 토글 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductToggleScrapResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않거나 상품이 존재하지 않음", content = @Content(mediaType = "application/json"))
    public ResponseEntity<ApiResponse<ProductToggleScrapResponse>> toggleProductScrap(
            @PathVariable("productId") Long productId,
            @AuthenticationPrincipal Long userId
    ) {
        ProductToggleScrapResponse response = userScrapService.toggleProductScrap(productId, userId);
        return ApiResponse.success(SuccessStatus.CREATE_PRODUCT_LIKE,response);
    }

    @PostMapping("/scraps/reference/{referenceId}")
    @Operation(
            summary = "레퍼런스 스크랩 토글 ",
            description = "이미 스크랩이 되어 있으면 취소하고, 되어 있지 않으면 스크랩에 추가합니다."
    )
    @Parameters({
            @Parameter(name = "referenceId", description = "스크랩할 Reference ID", example = "1")
    })
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스크랩 토글 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReferenceToggleScrapResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않거나 레퍼런스가 존재하지 않음", content = @Content(mediaType = "application/json"))
    public ResponseEntity<ApiResponse<ReferenceToggleScrapResponse>> toggleReferenceScrap(
            @PathVariable("referenceId") Long referenceId,
            @AuthenticationPrincipal Long userId
    ) {
        ReferenceToggleScrapResponse response = userScrapService.toggleReferenceScrap(referenceId, userId);
        return ApiResponse.success(SuccessStatus.CREATE_REFERENCE_SCRAP,response);
    }

    @GetMapping("/scraps/product")
    @Operation(summary = "유저가 스크랩한 상품 리스트 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스크랩 상품 내역 리스트 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserScrappedProductListResponse.class)))
    public ResponseEntity<ApiResponse<UserScrappedProductListResponse>> getUserScrappedProductList(
            @AuthenticationPrincipal Long userId
    ) {
        UserScrappedProductListResponse response = userScrapService.getUserScrappedProductList(userId);
        return ApiResponse.success(SuccessStatus.GET_USER_SCRAP_PRODUCT_LIST_SUCCESS, response);
    }

    @GetMapping("/scraps/reference")
    @Operation(summary = "유저가 스크랩한 레퍼런스 리스트 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스크랩 레퍼런스 내역 리스트 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserScrappedReferenceListResponse.class)))
    public ResponseEntity<ApiResponse<UserScrappedReferenceListResponse>> getUserScrappedReferenceList(
            @AuthenticationPrincipal Long userId
    ) {
        UserScrappedReferenceListResponse response = userScrapService.getUserScrappedReferenceList(userId);
        return ApiResponse.success(SuccessStatus.GET_USER_SCRAP_REFERENCE_LIST_SUCCESS, response);
    }

    @GetMapping("/recent-views")
    @Operation(summary = "유저가 최근 본 상품 리스트 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "최근 본 상품 리스트 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRecentViewedProductListResponse.class)))
    public ResponseEntity<ApiResponse<UserRecentViewedProductListResponse>> getUserRecentViewedProductList(
            @AuthenticationPrincipal Long userId
    ) {
        UserRecentViewedProductListResponse response = userViewService.getUserRecentViewedProductList(userId);
        return ApiResponse.success(SuccessStatus.GET_USER_LIKE_PRODUCT_LIST_SUCCESS, response);
    }

    @GetMapping("/inquiries")
    @Operation(summary = "문의하기 내역 전체 조회", description = "유저 전용 문의 내역 전체 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "문의 내역 전체 조회 성공", content = @Content(schema = @Schema(implementation = UserInquiryResponse.class)))
    public ResponseEntity<ApiResponse<PageResponse<UserInquiryResponse>>> getInquiryList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) InquiryStatus status,
            @RequestParam(required = false) InquiryType type,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ){

        Page<UserInquiryResponse> response = inquiryService.getUserInquiryList(new UserInquirySearchCondition(keyword,status,type), PageRequest.of(page, size));
        return ApiResponse.success(SuccessStatus.GET_INQUIRY_LIST_SUCCESS, PageResponse.from(response));
    }

    @GetMapping("/references")
    @Operation(summary = "내가 업로드한 레퍼런스 리스트 조회", description = "유저가 업로드한 레퍼런스 리스트 내역 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저 업로드 리스트 내역  조회 성공", content = @Content(schema = @Schema(implementation = UserUploadedReferenceListResponse.class)))
    public ResponseEntity<ApiResponse<UserUploadedReferenceListResponse>> getUserUploadReferenceList(
            @AuthenticationPrincipal Long userId
    ){
        UserUploadedReferenceListResponse response = referenceService.getUserUploadedReferenceList(userId);
        return ApiResponse.success(SuccessStatus.GET_USER_REFERENCE_SUCCESS, response);
    }

}
