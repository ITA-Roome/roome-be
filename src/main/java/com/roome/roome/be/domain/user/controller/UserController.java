package com.roome.roome.be.domain.user.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.user.dto.request.UpdateUserProfileRequest;
import com.roome.roome.be.domain.user.dto.request.UserOnboardingRequest;
import com.roome.roome.be.domain.user.dto.response.*;
import com.roome.roome.be.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User", description = "유저 API")
public class UserController {

    private final UserService userService;

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
        userService.saveUserOnboarding(userId, userOnboardingRequest);
        return ApiResponse.success(SuccessStatus.SAVE_USER_ONBOARDING);
    }

    @GetMapping("/onboarding/existence")
    @Operation(summary = "유저 온보딩 존재 여부 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "온보딩 존재 여부 조회 성공", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않음", content = @Content)
    public ResponseEntity<ApiResponse<UserOnboardingExistResponse>> checkOnboardingExistence(
            @AuthenticationPrincipal Long userId
    ) {
        UserOnboardingExistResponse response = userService.checkExistence(userId);
        return ApiResponse.success(SuccessStatus.CHECK_USER_ONBOARDING_EXISTENCE, response);
    }

    @GetMapping("/likes")
    @Operation(summary = "유저가 좋아요를 누른 상품 리스트 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 상품 리스트 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserLikeProductListResponse.class)))
    public ResponseEntity<ApiResponse<UserLikeProductListResponse>> getUserLikedProductList(
            @AuthenticationPrincipal Long userId
    ) {
        UserLikeProductListResponse response = userService.getUserLikedProductList(userId);
        return ApiResponse.success(SuccessStatus.GET_USER_LIKE_PRODUCT_LIST_SUCCESS, response);
    }

    @GetMapping("/scraps")
    @Operation(summary = "유저가 스크랩한 상품 리스트 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스크랩 내역 리스트 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserScrappedProductListResponse.class)))
    public ResponseEntity<ApiResponse<UserScrappedProductListResponse>> getUserScrappedProductList(
            @AuthenticationPrincipal Long userId
    ) {
        UserScrappedProductListResponse response = userService.getUserScrappedProductList(userId);
        return ApiResponse.success(SuccessStatus.GET_USER_LIKE_PRODUCT_LIST_SUCCESS, response);
    }

    @GetMapping("/recent-views")
    @Operation(summary = "유저가 최근 본 상품 리스트 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "최근 본 상품 리스트 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRecentViewedProductListResponse.class)))
    public ResponseEntity<ApiResponse<UserRecentViewedProductListResponse>> getUserRecentViewedProductList(
            @AuthenticationPrincipal Long userId
    ) {
        UserRecentViewedProductListResponse response = userService.getUserRecentViewedProductList(userId);
        return ApiResponse.success(SuccessStatus.GET_USER_LIKE_PRODUCT_LIST_SUCCESS, response);
    }
}
