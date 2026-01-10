package com.roome.roome.be.domain.user.repository;

import static com.querydsl.core.group.GroupBy.*;
import static com.roome.roome.be.domain.reference.entity.QReference.*;
import static com.roome.roome.be.domain.reference.entity.QReferenceImage.*;
import static com.roome.roome.be.domain.user.entity.QUser.*;
import static com.roome.roome.be.domain.user.entity.QUserLikeReference.*;



import java.util.List;

import com.querydsl.core.types.dsl.Expressions;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.roome.roome.be.domain.reference.dto.response.CommonReferenceInfo;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserLikeReferenceCustomRepositoryImpl implements UserLikeReferenceCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CommonReferenceInfo> findUserLikeReferenceListByUserId(Long userId) {
        return jpaQueryFactory
            .from(userLikeReference)
            .join(userLikeReference.user, user)
            .join(userLikeReference.reference, reference)
            .leftJoin(reference.referenceImageList, referenceImage)
            .where(userLikeReference.user.id.eq(userId))
            .orderBy(userLikeReference.updatedAt.desc())
            .transform(
                groupBy(reference.id).list(
                    Projections.constructor(
                        CommonReferenceInfo.class,
                        reference.id,
                        reference.name,
                        user.nickname,
                        user.id,
                        list(referenceImage.imageUrl),
                        reference.scrapCount,
                        reference.likeCount,
                            Expressions.asBoolean(false),            // 6. isScrapped (스크랩 목록 조희니까 True)
                            Expressions.asBoolean(true)
                    )
                )
            );
    }
}
