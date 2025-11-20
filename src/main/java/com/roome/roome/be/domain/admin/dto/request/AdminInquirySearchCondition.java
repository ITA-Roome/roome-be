package com.roome.roome.be.domain.admin.dto.request;

import com.roome.roome.be.domain.inquiry.enums.InquiryStatus;
import com.roome.roome.be.domain.inquiry.enums.InquiryType;

public record AdminInquirySearchCondition(
         String keyword,
         InquiryStatus status,
         InquiryType type
) {
}
