package com.roome.roome.be.domain.reference.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.roome.roome.be.domain.product.enums.TagType;
import com.roome.roome.be.domain.reference.dto.response.CandidateReferenceInfo;
import com.roome.roome.be.domain.reference.dto.response.ReferenceTagInfo;
import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.reference.enums.ReferenceCategoryMapping;
import com.roome.roome.be.domain.reference.enums.ReferenceMood;
import com.roome.roome.be.domain.reference.enums.ReferenceStyle;
import com.roome.roome.be.domain.user.enums.MoodType;
import com.roome.roome.be.domain.user.enums.SpaceType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.roome.roome.be.domain.product.entity.QProductTag.productTag;
import static com.roome.roome.be.domain.product.entity.QTag.tag;
import static com.roome.roome.be.domain.reference.entity.QReference.reference;
import static com.roome.roome.be.domain.reference.entity.QReferenceImage.referenceImage;
import static com.roome.roome.be.domain.reference.entity.QReferenceTag.referenceTag;
import static com.roome.roome.be.domain.user.entity.QUser.user;

@RequiredArgsConstructor
@Repository
public class ReferenceCustomRepositoryImpl implements ReferenceCustomRepository {

    private final JPAQueryFactory jpaQueryFactory; // QueryDSL 사용을 위해 주입

    @Override
    public Page<Reference> findRecommendedList(List<MoodType> moodTypes, List<SpaceType> spaceTypes, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (moodTypes != null && !moodTypes.isEmpty()) {
            builder.or(matchesMoodType(moodTypes));
        }
        if (spaceTypes != null && !spaceTypes.isEmpty()) {
            builder.or(matchesSpaceType(spaceTypes));
        }

        List<Reference> content = jpaQueryFactory
                .selectFrom(reference)
                .distinct() // 태그 조인 시 중복 제거
                .leftJoin(reference.user, user).fetchJoin() // 작성자 정보 N+1 방지
                .leftJoin(reference.referenceTagList, referenceTag) // 태그 필터링을 위한 조인
                .leftJoin(referenceTag.tag, tag)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getReferenceSort(pageable)) // 정렬 적용
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(reference.countDistinct())
                .from(reference)
                .leftJoin(reference.referenceTagList, referenceTag)
                .leftJoin(referenceTag.tag, tag)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression matchesMoodType(List<MoodType> moodTypes) {
        return tag.type.eq(TagType.REFERENCE_MOOD)
                .and(tag.name.in(moodTypes.stream().map(Enum::name).toList()));
    }

    private BooleanExpression matchesSpaceType(List<SpaceType> spaceTypes) {
        return tag.type.eq(TagType.REFERENCE_TYPE)
                .and(tag.name.in(spaceTypes.stream().map(Enum::name).toList()));
    }

    private OrderSpecifier<?>[] getReferenceSort(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "scrapCount":
                        orders.add(new OrderSpecifier<>(direction, reference.scrapCount));
                        break;
                    case "createdAt":
                        orders.add(new OrderSpecifier<>(direction, reference.createdAt));
                        break;
                    case "id":
                        orders.add(new OrderSpecifier<>(direction, reference.id));
                        break;
                    default:
                        orders.add(new OrderSpecifier<>(Order.DESC, reference.id));
                        break;
                }
            }
        } else {
            orders.add(new OrderSpecifier<>(Order.DESC, reference.scrapCount));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    @Override
    public List<CandidateReferenceInfo> findCandidateReferenceList(
            List<ReferenceCategoryMapping> matchedCategories,
            Set<ReferenceMood> moodList,
            Set<ReferenceStyle> styleList,
            Integer minBudget,
            Integer maxBudget
    ) {

        BooleanExpression moodCondition = moodIn(moodList);
        BooleanExpression styleCondition = styleIn(styleList);
        BooleanExpression priceCondition = priceBetween(minBudget, maxBudget);

        BooleanExpression tagCondition = null;

        if (moodCondition != null && styleCondition != null) {
            tagCondition = moodCondition.or(styleCondition);
        } else {
            tagCondition = (moodCondition != null) ? moodCondition : styleCondition;
        }

        return jpaQueryFactory
                .from(reference)
                .leftJoin(reference.referenceImageList, referenceImage)
                .leftJoin(reference.referenceTagList, referenceTag)
                .leftJoin(referenceTag.tag, tag)
                .where(
                        // 주석 처리 하셨던 카테고리(공간타입) 필터가 있다면 여기에 AND로 추가 필요
                        // categoryIn(matchedCategories),

                        priceCondition, // 예산은 필수 조건 (AND)
                        tagCondition    // (무드 OR 스타일) 조건 (AND)
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
        if (moodList == null || moodList.isEmpty()) return null;

        return tag.type.eq(TagType.REFERENCE_MOOD)
                .and(tag.name.in(moodList.stream().map(Enum::name).toList()));
    }

    private BooleanExpression styleIn(Set<ReferenceStyle> styleList) {
        if (styleList == null || styleList.isEmpty()) return null;

        return tag.type.eq(TagType.REFERENCE_STYLE)
                .and(tag.name.in(styleList.stream().map(Enum::name).toList()));
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
