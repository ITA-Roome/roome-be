package com.roome.roome.be.domain.inquiry.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.admin.dto.request.AdminInquirySearchCondition;
import com.roome.roome.be.domain.inquiry.dto.request.RegisterInquiryRequest;
import com.roome.roome.be.domain.inquiry.dto.response.AdminInquiryResponse;
import com.roome.roome.be.domain.inquiry.entity.Inquiry;
import com.roome.roome.be.domain.inquiry.enums.InquiryStatus;
import com.roome.roome.be.domain.inquiry.repository.InquiryCustomRepository;
import com.roome.roome.be.domain.inquiry.repository.InquiryRepository;
import com.roome.roome.be.domain.user.dto.request.UserInquirySearchCondition;
import com.roome.roome.be.domain.user.dto.response.UserInquiryResponse;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryCustomRepository inquiryCustomRepository;
    private final UserService userService;

    public void registerInquiry(Long userId, RegisterInquiryRequest request) {
        User user = userService.getUserById(userId);

        inquiryRepository.save(Inquiry.builder()
                .user(user)
                .content(request.content())
                .status(InquiryStatus.OPEN)
                .type(request.type())
                .build()
        );
    }

    public Page<AdminInquiryResponse> getAdminInquiryList(
            AdminInquirySearchCondition condition,
            Pageable pageable
    ) {
        return inquiryCustomRepository.findAdminInquiryList(condition,pageable);
    }

    public Page<UserInquiryResponse> getUserInquiryList(
            UserInquirySearchCondition condition,
            Pageable pageable
    ){
        return inquiryCustomRepository.findUserInquiryList(condition,pageable);
    }

    public Inquiry getInquiryById(Long id) {
        return inquiryRepository.findById(id).orElseThrow(
                () ->  new GeneralException(ErrorStatus.INQUIRY_NOT_FOUND)
        );
    }

}
