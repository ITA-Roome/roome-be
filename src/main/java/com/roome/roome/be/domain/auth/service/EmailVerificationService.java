package com.roome.roome.be.domain.auth.service;

import com.roome.roome.be.domain.auth.entity.EmailVerification;
import com.roome.roome.be.domain.auth.repository.EmailVerificationRepository;
import com.roome.roome.be.domain.auth.util.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailSender emailSender;

    // 이메일 인증 코드 요청
    @Transactional
    public void requestEmailVerificationCode(String email) {
        String verificationCode = createEmailVerificationCode();
        emailSender.send(email, verificationCode);

        emailVerificationRepository.findByEmail(email)
                .ifPresentOrElse(
                        existing -> {
                            updateEmailVerification(existing, verificationCode);
                        },
                        () -> {
                            registerEmailVerification(email, verificationCode);
                        }
                );
    }

    // Update EmailVerification
    private void updateEmailVerification(EmailVerification emailVerification, String verificationCode) {
        emailVerification.updateIsVerified(false);
        emailVerification.updateVerificationCode(verificationCode);
    }

    // Register EmailVerification
    private void registerEmailVerification(String email, String verificationCode) {
        EmailVerification newVerification = EmailVerification.builder()
                .email(email)
                .verificationCode(verificationCode)
                .isVerified(false)
                .build();

        emailVerificationRepository.save(newVerification);
    }

    // 4자리 랜덤 숫자 코드 생성
    private String createEmailVerificationCode() {
        int code = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(code);
    }
}
