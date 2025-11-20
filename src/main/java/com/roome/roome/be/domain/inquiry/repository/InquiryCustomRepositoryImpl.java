package com.roome.roome.be.domain.inquiry.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.roome.roome.be.domain.admin.dto.request.AdminInquirySearchCondition;
import com.roome.roome.be.domain.inquiry.dto.response.AdminInquiryDetailResponse;
import com.roome.roome.be.domain.inquiry.enums.InquiryStatus;
import com.roome.roome.be.domain.inquiry.enums.InquiryType;
import com.roome.roome.be.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.roome.roome.be.domain.inquiry.entity.QInquiry.inquiry;
import static com.roome.roome.be.domain.inquiry.entity.QInquiryAnswer.inquiryAnswer;
import static com.roome.roome.be.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class InquiryCustomRepositoryImpl implements InquiryCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<AdminInquiryDetailResponse> findAdminInquiryList(AdminInquirySearchCondition condition, Pageable pageable) {

        QUser admin = new QUser("admin");

        List<AdminInquiryDetailResponse> content = jpaQueryFactory
                .select(Projections.constructor(
                        AdminInquiryDetailResponse.class,
                        inquiry.id,
                        inquiry.type,
                        inquiry.status,
                        inquiry.user.id,
                        inquiry.user.nickname,
                        inquiry.content,
                        inquiry.createdAt,
                        inquiryAnswer.content,
                        inquiryAnswer.admin.id,
                        inquiryAnswer.admin.nickname,
                        inquiryAnswer.createdAt
                ))
                .from(inquiry)
                .join(inquiry.user, user)
                .leftJoin(inquiry.answer, inquiryAnswer)
                .leftJoin(inquiryAnswer.admin,admin)
                .where(
                        containKeyword(condition.keyword()),
                        statusEq(condition.status()),
                        typeEq(condition.type())
                )
                .orderBy(inquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(
                        containKeyword(condition.keyword()),
                        statusEq(condition.status()),
                        typeEq(condition.type())
                );

        return PageableExecutionUtils.getPage(content,pageable, countQuery::fetchOne);
    }

    private BooleanExpression containKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return inquiry.content.containsIgnoreCase(keyword);
    }

    private BooleanExpression statusEq(InquiryStatus status) {
        return status != null ? inquiry.status.eq(status) : null;
    }

    private BooleanExpression typeEq(InquiryType type) {
        return type != null ? inquiry.type.eq(type) : null;
    }

}
