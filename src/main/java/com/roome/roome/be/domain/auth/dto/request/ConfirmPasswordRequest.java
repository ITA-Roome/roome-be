package com.roome.roome.be.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ConfirmPasswordRequest(
        @NotBlank String password
) {
}
