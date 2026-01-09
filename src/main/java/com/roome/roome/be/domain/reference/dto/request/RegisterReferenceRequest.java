package com.roome.roome.be.domain.reference.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterReferenceRequest(
        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotBlank
        String mood
) {
}
