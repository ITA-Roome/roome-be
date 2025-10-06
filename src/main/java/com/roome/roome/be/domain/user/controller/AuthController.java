package com.roome.roome.be.domain.user.controller;

import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.jwt.JwtService;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.user.dto.response.LoginResponse;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.service.GoogleService;
import com.roome.roome.be.domain.user.service.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

}
