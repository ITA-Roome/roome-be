package com.roome.roome.be.domain.auth.service;

import com.roome.roome.be.domain.auth.dto.ConfirmEmailVerificationRequest;
import com.roome.roome.be.domain.auth.dto.EmailVerificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailVerificationService emailVerificationService;

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

}
