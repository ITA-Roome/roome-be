package com.roome.roome.be.domain.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record SignUpRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 30, message = "이메일은 30자를 초과할 수 없습니다.")
        String email,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 20, message = "닉네임은 20자를 초과할 수 없습니다.")
        String nickname,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(
                regexp = "^010-\\d{3,4}-\\d{4}$",
                message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678"
        )
        String phoneNumber
) {
    // 비밀번호 암호화 후 새로운 요청 생성
    public SignUpRequest withEncodedPassword(String encodedPassword) {
        return new SignUpRequest(email, nickname,encodedPassword,phoneNumber);
    }
}
