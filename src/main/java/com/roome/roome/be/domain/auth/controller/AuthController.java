package com.roome.roome.be.domain.auth.controller;

import com.roome.roome.be.common.jwt.JwtService;
import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.SuccessStatus;
import com.roome.roome.be.domain.auth.dto.request.*;
import com.roome.roome.be.domain.auth.dto.response.*;
import com.roome.roome.be.domain.auth.service.AuthService;
import com.roome.roome.be.domain.user.dto.response.LoginResponse;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.service.GoogleService;
import com.roome.roome.be.domain.user.service.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @Operation(summary = "카카오 로그인 콜백", description = "카카오에서 받은 인가 코드로 로그인을 처리하고 JWT 토큰을 발급합니다.")
    public ResponseEntity<ApiResponse<LoginResponse>> kakaoLogin(
            @RequestParam  String code
    ) {
        User user = kakaoService.loginWithKakao(code);

        LoginResponse response = LoginResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
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
            @RequestParam  String code
    ) {
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

    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(mediaType = "application/json"))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "비밀번호 양식이 올바르지 않거나 이메일 인증이 완료되지 않은 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 이메일이 존재하는 경우", content = @Content)
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignUpRequest request
    ) {
        authService.signUp(request);
        return ApiResponse.success(SuccessStatus.CREATE_USER_SUCCESS);
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원탈퇴")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원탈퇴 성공", content = @Content(mediaType = "application/json"))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않는 경우", content = @Content)
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal Long userId
    ) {
        authService.withdraw(userId);
        return ApiResponse.success(SuccessStatus.DELETE_USER_SUCCESS);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailLoginResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "비밀번호가 일치하지 않는 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않는 경우", content = @Content)
    public ResponseEntity<ApiResponse<EmailLoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        EmailLoginResponse response = authService.login(request);
        return ApiResponse.success(SuccessStatus.LOGIN_SUCCESS, response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(mediaType = "application/json"))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않는 경우", content = @Content)
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal Long userId
    ) {
        authService.logout(userId);
        return ApiResponse.success(SuccessStatus.LOGOUT_SUCCESS);
    }

    @GetMapping("/check-nickname")
    @Operation(summary = "닉네임 중복확인")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "아이디 중복 확인 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckNicknameResponse.class)))
    public ResponseEntity<ApiResponse<CheckNicknameResponse>> checkNickname(
            @RequestParam String nickname
    ) {
        CheckNicknameResponse response = authService.checkNickname(nickname);
        return ApiResponse.success(SuccessStatus.CHECK_NICKNAME_SUCCESS, response);
    }

    @PatchMapping("/password")
    @Operation(summary = "비밀번호 변경")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 변경 성공", content = @Content(mediaType = "application/json"))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "기존과 동일한 비밀번호를 입력한 경우", content = @Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않는 경우", content = @Content)
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestBody @Valid UpdatePasswordRequest request
    ) {
        authService.updatePassword(request);
        return ApiResponse.success(SuccessStatus.UPDATE_PASSWORD_SUCCESS);
    }

    @PostMapping("/password/confirm")
    @Operation(summary = "비밀번호 검증")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 검증 성공", content = @Content(mediaType = "application/json"))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "비밀번호 틀린 경우", content = @Content)
    public ResponseEntity<ApiResponse<Void>> confirmPassword(
            @RequestBody @Valid ConfirmPasswordRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        authService.confirmPassword(userId, request);
        return ApiResponse.success(SuccessStatus.CONFIRM_PASSWORD_SUCCESS);
    }

    @PostMapping("/find-email")
    @Operation(summary = "이메일 찾기")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 찾기 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindEmailResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "전화번호 형식이 틀린 경우",content = @Content)
    public ResponseEntity<ApiResponse<FindEmailResponse>> findEmail(
            @RequestBody @Valid FindEmailRequest request
    ) {
        FindEmailResponse response = authService.findEmail(request);
        return ApiResponse.success(SuccessStatus.FIND_EMAIL_SUCCESS, response);
    }

    @GetMapping("/check-email")
    @Operation(summary = "이메일 존재 여부 검사")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 존재 여부 검사 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckEmailResponse.class)))
    public ResponseEntity<ApiResponse<CheckEmailResponse>> checkEmail(
            @RequestParam String email
    ) {
        CheckEmailResponse response = authService.checkEmail(email);
        return ApiResponse.success(SuccessStatus.CHECK_EMAIL_SUCCESS, response);
    }

    @PostMapping("/email-verification")
    @Operation(summary = "이메일 인증 코드 요청")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 인증 요청 성공", content = @Content(mediaType = "application/json"))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증 코드 시간이 만료되었거나 인증 코드가 일치하지 않는 경우",content =@Content)
    public ResponseEntity<ApiResponse<Void>> requestEmailVerificationCode(
            @Valid @RequestBody EmailVerificationRequest request
    ) {
        authService.requestEmailVerificationCode(request);
        return ApiResponse.success(SuccessStatus.SEND_EMAIL_VERIFICATION_SUCCESS);
    }

    @PostMapping("/email-verification/confirm")
    @Operation(summary = "이메일 인증 코드 확인")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 인증 요청 성공", content = @Content(mediaType = "application/json"))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증 코드 시간이 만료되었거나 인증 코드가 일치하지 않는 경우",content =@Content)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 이메일로 인증요청을 보내지 않은 경우",content = @Content)
    public ResponseEntity<ApiResponse<Void>> confirmEmailVerification(
            @Valid @RequestBody ConfirmEmailVerificationRequest request
    ) {
        authService.confirmEmailVerificationCode(request);
        return ApiResponse.success(SuccessStatus.CONFIRM_EMAIL_VERIFICATION_SUCCESS);
    }

    @PostMapping("/token/reissue")
    @Operation(summary = "토큰 재발급")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Access Token 재발급 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReissueAccessTokenResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh Token이 유효하지 않거나 일치하지 않는 경우", content = @Content())
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Refresh Token에서 파싱한 유저가 존재하지 않는 경우", content =@Content())
    public ResponseEntity<ApiResponse<ReissueAccessTokenResponse>> reissueAccessToken(
            @RequestHeader("Authorization") String refreshToken
    ) {
        String tokenWithoutPrefix = refreshToken.replace("Bearer ", "").trim();

        ReissueAccessTokenResponse response = authService.reissueAccessToken(tokenWithoutPrefix);
        return ApiResponse.success(SuccessStatus.CREATE_TOKEN_SUCCESS, response);
    }

}
