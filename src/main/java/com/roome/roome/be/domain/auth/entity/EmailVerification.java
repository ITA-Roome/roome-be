package com.roome.roome.be.domain.auth.entity;

import com.roome.roome.be.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EmailVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailVerificationId;

    @Column(nullable = false, length = 30)
    private String email;

    @Column(nullable = false, length = 6)
    private String verificationCode;

    @Column(nullable = false)
    private boolean isVerified = false;

    public void updateIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public void updateVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}













