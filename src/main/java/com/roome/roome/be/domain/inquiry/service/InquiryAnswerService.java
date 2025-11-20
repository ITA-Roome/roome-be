package com.roome.roome.be.domain.inquiry.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.inquiry.entity.Inquiry;
import com.roome.roome.be.domain.inquiry.entity.InquiryAnswer;
import com.roome.roome.be.domain.inquiry.enums.InquiryStatus;
import com.roome.roome.be.domain.inquiry.repository.InquiryAnswerRepository;
import com.roome.roome.be.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryAnswerService {

    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final InquiryService inquiryService;

    @Transactional
    public void registerInquiryAnswer(User admin, Long inquiryId, String content) {
        Inquiry inquiry = inquiryService.getInquiryById(inquiryId);

        // inquiryAnswer 존재 여부가 가장 중요함
        if (inquiry.getAnswer() != null) {
            throw new GeneralException(ErrorStatus.INQUIRY_ANSWER_ALREADY_EXISTS);
        }

        InquiryAnswer answer = InquiryAnswer.builder()
                .inquiry(inquiry)
                .admin(admin)
                .content(content)
                .build();

        inquiryAnswerRepository.save(answer);
        inquiry.updateInquiryStatus(InquiryStatus.ANSWERED);
    }


    @Transactional
    public void updateInquiryAnswer(Long inquiryId, String content) {
        Inquiry inquiry = inquiryService.getInquiryById(inquiryId);

        // 답변된 경우만 수정 가능
        if (inquiry.getStatus() != InquiryStatus.ANSWERED) {
            throw new GeneralException(ErrorStatus.INQUIRY_NOT_ANSWERED);
        }

        InquiryAnswer inquiryAnswer = inquiry.getAnswer();
        if (inquiryAnswer == null) {
            throw new GeneralException(ErrorStatus.INQUIRY_ANSWER_NOT_FOUND);
        }

        inquiryAnswer.updateContent(content);
    }

}
