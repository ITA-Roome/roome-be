package com.roome.roome.be.common.sqs.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationMessageRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String verificationCode;
}
