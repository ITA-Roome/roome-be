package com.roome.roome.be.domain.inquiry.repository;

import com.roome.roome.be.domain.inquiry.entity.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {
}
