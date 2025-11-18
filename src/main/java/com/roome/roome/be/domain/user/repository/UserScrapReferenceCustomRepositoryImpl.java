package com.roome.roome.be.domain.user.repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.roome.roome.be.domain.reference.dto.response.CommonReferenceInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.roome.roome.be.domain.user.entity.QUserScrapReference.userScrapReference;
import static com.roome.roome.be.domain.reference.entity.QReference.reference;
import static com.roome.roome.be.domain.reference.entity.QReferenceImage.referenceImage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserScrapReferenceCustomRepositoryImpl implements UserScrapReferenceCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CommonReferenceInfo> findUserScrappedReferenceListByUserId(Long userId) {
        return jpaQueryFactory
                .from(userScrapReference)
                .join(userScrapReference.reference, reference)
                .leftJoin(reference.referenceImageList, referenceImage)
                .where(userScrapReference.user.id.eq(userId))
                .orderBy(reference.scrapCount.desc())
                .transform(
                        groupBy(reference.id).list(
                                Projections.constructor(CommonReferenceInfo.class,
                                        reference.id,
                                        reference.user.nickname,
                                        reference.user.id,
                                        GroupBy.list(referenceImage.objectKey),
                                        reference.scrapCount
                                )
                        )
                );
    }
}
