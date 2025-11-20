package com.roome.roome.be.domain.inquiry.dto.request;

import com.roome.roome.be.domain.inquiry.enums.InquiryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterInquiryRequest(
        @NotNull InquiryType type,
        @NotBlank String content
) {
}
