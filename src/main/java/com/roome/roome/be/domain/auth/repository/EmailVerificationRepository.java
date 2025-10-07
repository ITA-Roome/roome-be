package com.roome.roome.be.domain.auth.repository;

import com.roome.roome.be.domain.auth.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmail(String email);
    void deleteByEmailAndVerificationCode(String email, String code);
}
