package com.roome.roome.be.domain.auth.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.jwt.JwtService;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.auth.dto.*;
import com.roome.roome.be.domain.auth.enums.PasswordValidationType;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailVerificationService emailVerificationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 회원가입
    @Transactional
    public void signUp(SignUpRequest request) {
        userService.checkEmailNotExists(request.email());
        emailVerificationService.isEmailVerified(request.email());

        isValidPassword(request.password());

        String encodedPassword = passwordEncoder.encode(request.password());
        SignUpRequest signupRequest = request.withEncodedPassword(encodedPassword);

        userService.registerUser(signupRequest);
    }

    // 회원탈퇴
    @Transactional
    public void withdraw(Long userId) {
        userService.withdrawUser(userId);
    }

    // 로그인
    @Transactional
    public EmailLoginResponse login(LoginRequest request) {
        User user = userService.findUserByEmail(request.email());
        checkPasswordMatch(request.password(), user.getPassword(), PasswordValidationType.LOGIN);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        userService.updateRefreshToken(user, refreshToken);
        return EmailLoginResponse.from(accessToken, refreshToken, user);
    }

    // 로그아웃
    @Transactional
    public void logout(Long userId) {
        User user = userService.findUserById(userId);
        userService.clearRefreshToken(user);
    }

    // Update Password
    @Transactional
    public void updatePassword(UpdatePasswordRequest request) {
        User user = userService.findUserByEmail(request.email());
        checkPasswordMatch(request.password(), user.getPassword(), PasswordValidationType.UPDATE);

        userService.updatePassword(user, passwordEncoder.encode(request.password()));
    }

    // 전화번호로 이메일 찾기
    public FindEmailResponse findEmail(FindEmailRequest request) {
        return userService.findUserByPhoneNumber(request.phoneNumber())
                .map(user -> new FindEmailResponse(user.getEmail()))
                .orElseGet(() -> new FindEmailResponse(null));
    }

    // 이메일 인증 코드 요청
    public void requestEmailVerificationCode(EmailVerificationRequest request) {
        emailVerificationService.requestEmailVerificationCode(request.email());
    }

    // 이메일 인증 코드 확인
    @Transactional
    public void confirmEmailVerificationCode(ConfirmEmailVerificationRequest request) {
        emailVerificationService.confirmEmailVerificationCode(
                request.email(),
                request.emailVerificationCode()
        );
    }

    // 비밀번호 형식 검사
    private void isValidPassword(String password) {
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>/?].*");

        int charTypeCount = (hasLowercase ? 1 : 0)
                + (hasUppercase ? 1 : 0)
                + (hasDigit ? 1 : 0)
                + (hasSpecial ? 1 : 0);

        if (charTypeCount >= 3 && password.length() >= 8) return;
        if (charTypeCount == 2 && password.length() >= 10) return;

        throw new GeneralException(ErrorStatus.INVALID_PASSWORD_FORMAT);
    }

    // 닉네임 중복 검사
    public CheckNicknameResponse checkNickname(String nickname) {
        return userService.checkNickname(nickname);
    }

    /** 비밀번호 매칭 검사 */
    private void checkPasswordMatch(String rawPassword, String encodedPassword, PasswordValidationType type) {
        boolean isMatch = passwordEncoder.matches(rawPassword, encodedPassword);

        switch (type) {
            case LOGIN -> {
                if (!isMatch) throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
            }
            case UPDATE -> {
                if (isMatch) throw new GeneralException(ErrorStatus.PASSWORD_SAME_AS_OLD);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

}
