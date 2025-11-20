package com.roome.roome.be.domain.inquiry.repository;

import com.roome.roome.be.domain.admin.dto.request.AdminInquirySearchCondition;
import com.roome.roome.be.domain.inquiry.dto.response.AdminInquiryDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface InquiryCustomRepository {
     Page<AdminInquiryDetailResponse> findInquiryList(AdminInquirySearchCondition condition, Pageable pageable);
}
