package com.roome.roome.be.domain.inquiry.repository;

import com.roome.roome.be.domain.inquiry.entity.Inquiry;
import com.roome.roome.be.domain.inquiry.entity.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {
    Optional<InquiryAnswer> findByInquiry(Inquiry inquiry);
}
