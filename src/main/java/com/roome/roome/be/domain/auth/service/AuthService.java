package com.roome.roome.be.domain.auth.service;

import com.roome.roome.be.domain.auth.dto.EmailVerificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailVerificationService emailVerificationService;

    public void requestEmailVerificationCode(EmailVerificationRequest request) {
        emailVerificationService.requestEmailVerificationCode(request.email());
    }

}
