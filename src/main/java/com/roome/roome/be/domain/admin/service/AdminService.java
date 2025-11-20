package com.roome.roome.be.domain.admin.service;

import com.roome.roome.be.domain.admin.dto.request.AdminInquirySearchCondition;
import com.roome.roome.be.domain.admin.dto.request.AdminInquirySearchConditionRequest;
import com.roome.roome.be.domain.inquiry.dto.response.AdminInquiryDetailResponse;
import com.roome.roome.be.domain.inquiry.dto.response.AdminInquiryListResponse;
import com.roome.roome.be.domain.inquiry.entity.Inquiry;
import com.roome.roome.be.domain.inquiry.service.InquiryService;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final InquiryService inquiryService;
    private final UserService userService;

    public Page<AdminInquiryDetailResponse> getInquiryList(
            Long userId,
            AdminInquirySearchConditionRequest request
    ) {
        userService.validateAdmin(userId);
        return inquiryService.getInquiryList((new AdminInquirySearchCondition(request.keyword(), request.status(), request.type())), request.toPageable());
    }
}
