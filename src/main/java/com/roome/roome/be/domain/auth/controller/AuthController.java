package com.roome.roome.be.domain.auth.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.jwt.JwtService;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.auth.dto.ConfirmEmailVerificationRequest;
import com.roome.roome.be.domain.auth.dto.EmailVerificationRequest;
import com.roome.roome.be.domain.auth.service.AuthService;
import com.roome.roome.be.domain.user.dto.response.LoginResponse;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.service.GoogleService;
import com.roome.roome.be.domain.user.service.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final JwtService jwtService;
    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final AuthService authService;

    @GetMapping("/kakao/authorize-uri")
    @Operation(summary = "카카오 로그인 URL 조회")
    public ResponseEntity<ApiResponse<Map<String, String>>> getKakaoAuthorizeUri() {
        String authorizeUri = kakaoService.getKakaoAuthorizeUri();
        Map<String, String> data = Map.of("authorizeUri", authorizeUri);

        return ApiResponse.success(SuccessStatus.AUTH_URL_SUCCESS, data);
    }

    @GetMapping("/kakao/callback")
    @Operation(summary = "카카오 로그인 콜백",
            description = "카카오에서 받은 인가 코드로 로그인을 처리하고 JWT 토큰을 발급합니다."
    )
    public ResponseEntity<ApiResponse<LoginResponse>> kakaoLogin(
            @RequestParam  String code
    ) {
        User user = kakaoService.loginWithKakao(code);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .userInfo(LoginResponse.UserInfo.builder()
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .loginType(user.getLoginType())
                        .build())
                .build();

        return ApiResponse.success(SuccessStatus.LOGIN_SUCCESS, response);
    }

    @GetMapping("/google/authorize-uri")
    @Operation(summary = "구글 로그인 URL 조회")
    public ResponseEntity<ApiResponse<Map<String, String>>> getGoogleAuthorizeUri() {
        String authorizeUri = googleService.getGoogleAuthorizeUri();
        Map<String, String> data = Map.of("authorizeUri", authorizeUri);
        return ApiResponse.success(SuccessStatus.AUTH_URL_SUCCESS, data);
    }

    @GetMapping("/google/callback")
    @Operation(summary = "구글 로그인 콜백",
            description = "구글 받은 인가 코드로 로그인을 처리하고 JWT 토큰을 발급합니다."
    )
    public ResponseEntity<ApiResponse<LoginResponse>> googleLogin(
            @RequestParam  String code)
    {
        User user = googleService.loginWithGoogle(code);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .userInfo(LoginResponse.UserInfo.builder()
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .loginType(user.getLoginType())
                        .build())
                .build();
        return ApiResponse.success(SuccessStatus.LOGIN_SUCCESS, response);
    }

    @PostMapping("/email-verification")
    @Operation(summary = "이메일 인증 코드 요청")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "이메일 인증 요청 성공",
            content = @Content(mediaType = "application/json")
    )
    public ResponseEntity<ApiResponse<Void>> requestEmailVerificationCode(
            @Valid @RequestBody EmailVerificationRequest request) {

        authService.requestEmailVerificationCode(request);
        return ApiResponse.success(SuccessStatus.SEND_EMAIL_VERIFICATION_SUCCESS);
    }

    @PostMapping("/email-verification/confirm")
    @Operation(summary = "이메일 인증 코드 확인")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 인증 요청 성공", content = @Content(mediaType = "application/json"))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증 코드 시간이 만료되었거나 인증 코드가 일치하지 않는 경우",content =@Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 이메일로 인증요청을 보내지 않은 경우",content = @Content)
    public ResponseEntity<ApiResponse<Void>> confirmEmailVerification(
            @Valid @RequestBody ConfirmEmailVerificationRequest request) {

        authService.confirmEmailVerificationCode(request);
        return ApiResponse.success(SuccessStatus.CONFIRM_EMAIL_VERIFICATION_SUCCESS);
    }
}
