package com.roome.roome.be.domain.inquiry.repository;

import com.roome.roome.be.domain.admin.dto.request.AdminInquirySearchCondition;
import com.roome.roome.be.domain.inquiry.dto.response.AdminInquiryResponse;
import com.roome.roome.be.domain.user.dto.request.UserInquirySearchCondition;
import com.roome.roome.be.domain.user.dto.response.UserInquiryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface InquiryCustomRepository {
     Page<AdminInquiryResponse> findAdminInquiryList(AdminInquirySearchCondition condition, Pageable pageable);
     Page<UserInquiryResponse> findUserInquiryList(UserInquirySearchCondition condition, Pageable pageable);
}
