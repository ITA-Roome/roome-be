package com.roome.roome.be.domain.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record SignUpRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 30, message = "이메일은 30자를 초과할 수 없습니다.")
        String email,

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
    // 비밀번호 암호화 후 새로운 요청 생성
    public SignUpRequest withEncodedPassword(String encodedPassword) {
        return new SignUpRequest(encodedPassword, email, nickname);
    }
}
