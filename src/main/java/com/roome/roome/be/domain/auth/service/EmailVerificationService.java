package com.roome.roome.be.domain.auth.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.auth.entity.EmailVerification;
import com.roome.roome.be.domain.auth.repository.EmailVerificationRepository;
import com.roome.roome.be.domain.auth.util.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    // 이메일 인증 코드 확인
    public void confirmEmailVerificationCode(String email, String code) {
        EmailVerification emailVerification = findEmailVerificationByEmail(email);

        validateCodeExpiration(emailVerification.getUpdatedAt());
        validateVerificationCode(code, emailVerification.getVerificationCode());

        emailVerification.updateIsVerified(true);
    }

    // 이메일 검증 여부 검사
    public void isEmailVerified(String email) {
        EmailVerification emailVerification = findEmailVerificationByEmail(email);

        if (!emailVerification.isVerified()) {
            throw new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED);
        }
        emailVerificationRepository.deleteById(emailVerification.getEmailVerificationId());
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

    // 이메일 정보 조회 (없을 경우 예외 발생)
    private EmailVerification findEmailVerificationByEmail(String email) {
        return emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_FOUND));
    }

    // 인증 코드 만료 검증(예: 5분 이상 지난 경우 만료 처리)
    private void validateCodeExpiration(LocalDateTime updatedAt) {
        LocalDateTime now = LocalDateTime.now();
        if (updatedAt.isBefore(now.minusMinutes(5))) {
            throw new GeneralException(ErrorStatus.VERIFICATION_CODE_EXPIRED);
        }
    }

    // 인증 코드 일치 여부 검증
    private void validateVerificationCode(String inputCode, String storedCode) {
        if (!storedCode.equals(inputCode)) {
            throw new GeneralException(ErrorStatus.INVALID_VERIFICATION_CODE);
        }
    }

    // 4자리 랜덤 숫자 코드 생성
    private String createEmailVerificationCode() {
        int code = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(code);
    }
}
