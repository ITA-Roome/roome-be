package com.roome.roome.be.domain.user.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.user.dto.request.UserOnboardingRequest;
import com.roome.roome.be.domain.user.dto.response.UserLikeProductListResponse;
import com.roome.roome.be.domain.user.dto.response.UserOnboardingExistResponse;
import com.roome.roome.be.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User", description = "유저 API")
public class UserController {

    private final UserService userService;

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
    public ResponseEntity<ApiResponse<UserLikeProductListResponse>> getUserLikedProductList(
            @AuthenticationPrincipal Long userId
    ){
        UserLikeProductListResponse response = userService.getUserLikedProductList(userId);
        return ApiResponse.success(SuccessStatus.GET_USER_LIKE_PRODUCT_LIST_SUCCESS, response);
    }
}
