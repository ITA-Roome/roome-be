package com.roome.roome.be.domain.inquiry.dto.response;

import com.roome.roome.be.domain.inquiry.enums.InquiryStatus;
import com.roome.roome.be.domain.inquiry.enums.InquiryType;

import java.time.LocalDateTime;

public record AdminInquiryDetailResponse(

        Long inquiryId,                // 문의 ID
        InquiryType inquiryType,       // 문의 타입
        InquiryStatus inquiryStatus,   // 문의 상태

        Long writerId,                 // 작성자 ID
        String writerNickname,         // 작성자 닉네임

        String inquiryContent,         // 문의 내용
        LocalDateTime inquiryCreatedAt,// 문의 작성 시간

        String answerContent,          // 답변 내용
        Long answerWriterId,           // 답변자 ID
        String answerWriterNickname,   // 답변자 닉네임
        LocalDateTime answerCreatedAt  // 답변 작성 시간
) {}

