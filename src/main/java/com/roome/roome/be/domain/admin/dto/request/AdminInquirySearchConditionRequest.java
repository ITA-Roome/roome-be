package com.roome.roome.be.domain.admin.dto.request;

import com.roome.roome.be.domain.inquiry.enums.InquiryStatus;
import com.roome.roome.be.domain.inquiry.enums.InquiryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record AdminInquirySearchConditionRequest(
        @Size(max = 100)
        String keyword,

        @Valid  InquiryStatus status,
        @Valid InquiryType type,

        @Min(0)
        Integer page,
        @Min(1) @Max(50)
        Integer size
) {

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }

    public static AdminInquirySearchConditionRequest of(
            String keyword,
            InquiryStatus status,
            InquiryType type,
            Integer page,
            Integer size
            ) {
        return new AdminInquirySearchConditionRequest(keyword,status,type,page,size);
    }
}
