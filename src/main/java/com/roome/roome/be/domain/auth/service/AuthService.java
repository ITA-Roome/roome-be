package com.roome.roome.be.domain.auth.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.auth.dto.ConfirmEmailVerificationRequest;
import com.roome.roome.be.domain.auth.dto.EmailVerificationRequest;
import com.roome.roome.be.domain.auth.dto.SignUpRequest;
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

}
