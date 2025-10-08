package com.roome.roome.be.domain.auth.dto.request;

import jakarta.validation.constraints.*;

public record FindEmailRequest(
        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(
                regexp = "^010-\\d{3,4}-\\d{4}$",
                message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678"
        )
        String phoneNumber
) {
}
