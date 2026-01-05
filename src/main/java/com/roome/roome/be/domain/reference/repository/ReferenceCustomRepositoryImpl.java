package com.roome.roome.be.domain.reference.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.roome.roome.be.domain.product.enums.TagType;
import com.roome.roome.be.domain.reference.dto.response.CandidateReferenceInfo;
import com.roome.roome.be.domain.reference.dto.response.ReferenceTagInfo;
import com.roome.roome.be.domain.reference.enums.ReferenceCategoryMapping;
import com.roome.roome.be.domain.reference.enums.ReferenceMood;
import com.roome.roome.be.domain.reference.enums.ReferenceStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.roome.roome.be.domain.product.entity.QProductTag.productTag;
import static com.roome.roome.be.domain.product.entity.QTag.tag;
import static com.roome.roome.be.domain.reference.entity.QReference.reference;
import static com.roome.roome.be.domain.reference.entity.QReferenceImage.referenceImage;
import static com.roome.roome.be.domain.reference.entity.QReferenceTag.referenceTag;

@RequiredArgsConstructor
@Repository
public class ReferenceCustomRepositoryImpl implements ReferenceCustomRepository {

    private final JPAQueryFactory jpaQueryFactory; // QueryDSL 사용을 위해 주입

    @Override
    public List<CandidateReferenceInfo> findCandidateReferenceList(
            List<ReferenceCategoryMapping> matchedCategories,
            Set<ReferenceMood> moodList,
            Set<ReferenceStyle> styleList,
            Integer minBudget,
            Integer maxBudget
    ) {


        return jpaQueryFactory
                .from(reference)
                .leftJoin(reference.referenceImageList, referenceImage)
                .leftJoin(reference.referenceTagList, referenceTag)
                .leftJoin(referenceTag.tag, tag)
                .where(
//                        categoryIn(matchedCategories),
                        moodIn(moodList),
                        styleIn(styleList),
                        priceBetween(minBudget, maxBudget)
                )
                .limit(30)
                .transform(
                        GroupBy.groupBy(reference.id).list(
                                Projections.constructor(
                                        CandidateReferenceInfo.class,
                                        reference.id,
                                        reference.description,
                                        referenceImage.imageUrl.min(),
                                        GroupBy.set(
                                                Projections.constructor(
                                                        ReferenceTagInfo.class,
                                                        tag.id,
                                                        tag.name,
                                                        tag.type
                                                )
                                        )
                                )
                        )
                );
    }

    private BooleanExpression moodIn(Set<ReferenceMood> moodList) {
        if (moodList == null || moodList.isEmpty()) {
            return null;
        }

        return tag.type.eq(TagType.MOOD)
                .and(
                        tag.name.in(
                                moodList.stream()
                                        .map(Enum::name)
                                        .toList()
                        )
                );
    }

    private BooleanExpression styleIn(Set<ReferenceStyle> styleList) {
        if (styleList == null || styleList.isEmpty()) {
            return null;
        }

        return tag.type.eq(TagType.STYLE)
                .and(
                        tag.name.in(
                                styleList.stream()
                                        .map(Enum::name)
                                        .toList()
                        )
                );
    }


    private BooleanExpression priceBetween(Integer minBudget, Integer maxBudget) {
        if (minBudget != null && maxBudget != null) {
            return reference.price.between(minBudget, maxBudget);
        }

        if (minBudget != null) {
            return reference.price.goe(minBudget);
        }

        if (maxBudget != null) {
            return reference.price.loe(maxBudget);
        }

        return null;
    }

    @Override
    public List<ReferenceMatchResult> findRelatedReferencesByProductTags(
            Long productId,
            int minMatchCount,
            int limit
    ) {
        List<Tuple> productTags = jpaQueryFactory
                .select(tag.name, tag.type)
                .from(productTag)
                .join(productTag.tag, tag)
                .where(productTag.product.id.eq(productId))
                .fetch();

        if (productTags.isEmpty()) {
            return List.of();
        }

        BooleanBuilder builder = new BooleanBuilder();
        for (Tuple tuple : productTags) {
            String tagName = tuple.get(tag.name);
            TagType type = tuple.get(tag.type);

            TagType referenceType = mapToReferenceTagType(type);
            if (referenceType != null) {
                builder.or(tag.name.eq(tagName).and(tag.type.eq(referenceType)));
            }
        }

        return jpaQueryFactory
                .select(Projections.constructor(
                        ReferenceMatchResult.class,
                        reference.id,
                        referenceTag.tag.id.countDistinct().intValue()
                ))
                .from(reference)
                .join(reference.referenceTagList, referenceTag)
                .join(referenceTag.tag, tag)
                .where(builder)
                .groupBy(reference.id)
                .having(referenceTag.tag.id.countDistinct().goe(minMatchCount))
                .orderBy(referenceTag.tag.id.countDistinct().desc()) // 많이 일치하는 순서대로 정렬
                .limit(limit)
                .fetch();
    }

    private TagType mapToReferenceTagType(TagType referenceTagType) {
        if (referenceTagType == null) return null;

        return switch (referenceTagType) {
            case USAGE -> TagType.REFERENCE_TYPE;
            case MOOD -> TagType.REFERENCE_MOOD;
            case STYLE -> TagType.REFERENCE_STYLE;
            case SIZE -> TagType.REFERENCE_SIZE;
            default -> null;
        };
    }
}
