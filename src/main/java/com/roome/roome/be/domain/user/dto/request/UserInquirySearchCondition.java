package com.roome.roome.be.domain.user.dto.request;

import com.roome.roome.be.domain.inquiry.enums.InquiryStatus;
import com.roome.roome.be.domain.inquiry.enums.InquiryType;

public record UserInquirySearchCondition(
        String keyword,
        InquiryStatus status,
        InquiryType type
) {
    public static UserInquirySearchCondition of(
            String keyword,
            InquiryStatus status,
            InquiryType type
    ) {
        return new UserInquirySearchCondition(keyword, status, type);
    }
}
