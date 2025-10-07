package com.roome.roome.be.domain.auth.dto;

import jakarta.validation.constraints.*;

public record EmailVerificationRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 30, message = "이메일은 30자를 초과할 수 없습니다.")
        String email
) {}
