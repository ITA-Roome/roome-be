package com.roome.roome.be.domain.admin.service;

import com.roome.roome.be.domain.admin.dto.request.AdminInquiryAnswerRequest;
import com.roome.roome.be.domain.admin.dto.request.AdminInquirySearchCondition;
import com.roome.roome.be.domain.admin.dto.request.AdminInquirySearchConditionRequest;
import com.roome.roome.be.domain.inquiry.dto.response.AdminInquiryResponse;
import com.roome.roome.be.domain.inquiry.service.InquiryAnswerService;
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
    private final InquiryAnswerService inquiryAnswerService;
    private final UserService userService;

    public Page<AdminInquiryResponse> getAdminInquiryList(
            Long userId,
            AdminInquirySearchConditionRequest request
    ) {
        userService.validateAdmin(userId);
        return inquiryService.getAdminInquiryList((new AdminInquirySearchCondition(request.keyword(), request.status(), request.type())), request.toPageable());
    }

    public void registerAdminInquiryAnswer(
            Long userId,
            Long inquiryId,
            AdminInquiryAnswerRequest request
    ){
        User admin = userService.validateAdmin(userId);
        inquiryAnswerService.registerInquiryAnswer(admin,inquiryId, request.content());
    }
}
