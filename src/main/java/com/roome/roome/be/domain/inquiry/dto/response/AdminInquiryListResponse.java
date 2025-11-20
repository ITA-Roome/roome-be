package com.roome.roome.be.domain.inquiry.dto.response;

import java.util.List;

public record AdminInquiryListResponse(
        List<AdminInquiryDetailResponse> inquiryList
) {
}
