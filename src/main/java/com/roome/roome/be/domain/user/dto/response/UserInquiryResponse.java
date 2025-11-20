package com.roome.roome.be.domain.user.dto.response;

import com.roome.roome.be.domain.inquiry.enums.InquiryStatus;
import com.roome.roome.be.domain.inquiry.enums.InquiryType;

import java.time.LocalDateTime;

public record UserInquiryResponse(
        Long inquiryId,                // 문의 ID
        InquiryType inquiryType,       // 문의 타입
        InquiryStatus inquiryStatus,   // 문의 상태
        String inquiryContent,         // 문의 내용
        LocalDateTime inquiryCreatedAt,// 문의 작성 시간
        String answerContent,          // 답변 내용
        LocalDateTime answerCreatedAt  // 답변 작성 시간
) { }
