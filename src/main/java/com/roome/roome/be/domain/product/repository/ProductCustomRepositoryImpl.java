package com.roome.roome.be.domain.product.repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.roome.roome.be.domain.product.dto.response.CandidateProductInfo;
import com.roome.roome.be.domain.product.dto.response.ProductTagInfo;
import com.roome.roome.be.domain.product.dto.response.RelatedProductResponse;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.QProductTag;
import com.roome.roome.be.domain.product.entity.QTag;
import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.product.enums.ProductType;
import com.roome.roome.be.domain.product.enums.TagType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.roome.roome.be.domain.product.entity.QProduct.product;
import static com.roome.roome.be.domain.product.entity.QProductTag.productTag;
import static com.roome.roome.be.domain.product.entity.QProductImage.productImage;
import static com.roome.roome.be.domain.product.entity.QTag.tag;
import static com.roome.roome.be.domain.shop.entity.QShop.shop;

@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory; // QueryDSL 사용을 위해 주입

    @Override
    public Page<Product> findByDynamicFilters(
            Long shopId,
            ProductCategory category,
            String keyWord,
            Integer minPrice,
            Integer maxPrice,
            Map<TagType, List<String>> tagFilters,
            String match,
            Pageable pageable
    ) {

        JPAQuery<Product> query = jpaQueryFactory
                .selectFrom(product)
                .leftJoin(product.shop, shop).fetchJoin()
                .distinct();

        query.where(
                shopIdEq(shopId),
                categoryEq(category),
                nameContains(keyWord),
                priceGoe(minPrice),
                priceLoe(maxPrice),
                tagFilter(tagFilters, match)
        );

        applySort(query, pageable);

        query.offset(pageable.getOffset())
            .limit(pageable.getPageSize());

        List<Product> content = query.fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(product.countDistinct())
                .from(product)
                .where(
                        shopIdEq(shopId),
                        categoryEq(category),
                        nameContains(keyWord),
                        priceGoe(minPrice),
                        priceLoe(maxPrice),
                        tagFilter(tagFilters, match)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<RelatedProductResponse> findRelatedProductList(Long excludeProductId, ProductCategory category, List<Long> tagIdList) {

        NumberExpression<Integer> relevanceScore =
                buildRelevanceScore(category, tagIdList);

        return jpaQueryFactory.
                select(Projections.constructor(
                        RelatedProductResponse.class,
                        product.id,
                        product.name,
                        tag.name,
                        product.description,
                        product.price,
                        productImage.imageUrl,
                        product.likeCount,
                        product.scrapCount
                ))
                .from(product)
                .leftJoin(productImage).on(product.id.eq(productImage.product.id).and(productImage.sortOrder.eq(1)))
                .leftJoin(productTag).on(product.id.eq(productTag.product.id))
                .leftJoin(tag).on(productTag.tag.id.eq(tag.id).and(tag.type.eq(TagType.PRODUCT_TYPE)))
                .where(product.id.ne(excludeProductId))
                .groupBy(product.id)
                .orderBy(relevanceScore.desc())
                .limit(20)
                .fetch();
    }

    @Override
    public long countByDynamicFilters(
        Long shopId,
        ProductCategory category,
        String keyWord,
        Integer minPrice,
        Integer maxPrice,
        Map<TagType, List<String>> tagFilters,
        String match
    ) {
        Long cnt = jpaQueryFactory
            .select(product.countDistinct())
            .from(product)
            .where(
                shopIdEq(shopId),
                categoryEq(category),
                nameContains(keyWord),
                priceGoe(minPrice),
                priceLoe(maxPrice),
                tagFilter(tagFilters, match)
            )
            .fetchOne();

        return (cnt == null) ? 0L : cnt;
    }

    @Override
    public List<Product> findSliceByDynamicFilters(
        Long shopId,
        ProductCategory category,
        String keyWord,
        Integer minPrice,
        Integer maxPrice,
        Map<TagType, List<String>> tagFilters,
        String match,
        List<Long> excludeIds,
        long offset,
        int limit,
        Sort sort
    ) {
        JPAQuery<Product> query = jpaQueryFactory
            .selectFrom(product)
            .leftJoin(product.shop, shop).fetchJoin()
            .distinct();

        BooleanExpression excludeCond = disclosureSafeNotEmpty(excludeIds)
            ? product.id.notIn(excludeIds)
            : null;

        query.where(
            shopIdEq(shopId),
            categoryEq(category),
            nameContains(keyWord),
            priceGoe(minPrice),
            priceLoe(maxPrice),
            tagFilter(tagFilters, match),
            excludeCond
        );

        applySort(query, sort);

        query.offset(offset).limit(limit);

        return query.fetch();
    }

    private boolean disclosureSafeNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }

    private BooleanExpression shopIdEq(Long shopId) {
        return shopId != null ? product.shop.id.eq(shopId) : null;
    }

    private BooleanExpression categoryEq(ProductCategory category) {
        if (category == null) return null;

        return JPAExpressions
            .selectOne()
            .from(productTag)
            .join(productTag.tag, tag)
            .where(
                productTag.product.eq(product),
                tag.type.eq(TagType.PRODUCT_TYPE),
                tag.name.eq(category.name())
            )
            .exists();
    }
    @Override
    public List<Product> findNonRecommendedSliceByFilters(
        Long shopId,
        ProductCategory category,
        String keyWord,
        Integer minPrice,
        Integer maxPrice,
        Map<TagType, List<String>> baseFilters,
        String baseMatch,
        Map<TagType, List<String>> recommendedFilters,
        String recommendedMatch,
        long offset,
        int limit,
        Sort sort
    ) {
        if (limit <= 0) return List.of();

        JPAQuery<Product> query = jpaQueryFactory
            .selectFrom(product)
            .leftJoin(product.shop, shop).fetchJoin()
            .distinct();

        query.where(
            shopIdEq(shopId),
            categoryEq(category),
            nameContains(keyWord),
            priceGoe(minPrice),
            priceLoe(maxPrice),
            tagFilter(baseFilters, baseMatch),
            excludeRecommendedByFilters(recommendedFilters, recommendedMatch) // ✅ 핵심
        );

        applySort(query, sort);

        query.offset(offset).limit(limit);

        return query.fetch();
    }


    // 추천 조건을 만족하는 상품은 제외
    private BooleanExpression excludeRecommendedByFilters(
        Map<TagType, List<String>> recommendedFilters,
        String recommendedMatch
    ) {
        if (recommendedFilters == null || recommendedFilters.isEmpty()) return null;

        BooleanExpression isRecommended = tagFilter(recommendedFilters, recommendedMatch);

        return (isRecommended == null) ? null : isRecommended.not();
    }

    private BooleanExpression nameContains(String keyWord) {
        return keyWord != null && !keyWord.isBlank() ? product.name.containsIgnoreCase(keyWord) : null;
    }

    private BooleanExpression priceGoe(Integer minPrice) {
        return minPrice != null ? product.price.goe(minPrice) : null;
    }

    private BooleanExpression priceLoe(Integer maxPrice) {
        return maxPrice != null ? product.price.loe(maxPrice) : null;
    }

    // ★★★ 태그 동적 처리 (핵심) ★★★
    private BooleanExpression tagFilter(Map<TagType, List<String>> tagFilters, String match) {
        if (tagFilters == null || tagFilters.isEmpty()) {
            return null; // 태그 필터 없으면 null 반환
        }

        // 'match=all' (모든 조건 AND)
        // (예: COLOR=[WHITE], MATERIAL=[WOOD] -> (WHITE) AND (WOOD)를 가진 상품)
        if ("all".equalsIgnoreCase(match)) {
            BooleanExpression allMatch = null;
            for (Map.Entry<TagType, List<String>> entry : tagFilters.entrySet()) {
                TagType type = entry.getKey();
                List<String> names = entry.getValue();

                if (names != null && !names.isEmpty()) {
                    BooleanExpression subQuery = product.id.in(
                            JPAExpressions.select(productTag.product.id)
                                    .from(productTag)
                                    .join(productTag.tag, tag)
                                    .where(
                                            tag.type.eq(type),
                                            tag.name.in(names)
                                    )
                                    .groupBy(productTag.product.id)
                                    .having(tag.name.countDistinct().eq((long) names.size())) // all
                    );
                    allMatch = (allMatch == null) ? subQuery : allMatch.and(subQuery);
                }
            }
            return allMatch;
        } else {
            BooleanExpression anyMatch = null;
            for (Map.Entry<TagType, List<String>> entry : tagFilters.entrySet()) {
                TagType type = entry.getKey();
                List<String> names = entry.getValue();

                if (names != null && !names.isEmpty()) {
                    BooleanExpression condition = JPAExpressions
                            .selectOne()
                            .from(productTag)
                            .join(productTag.tag, tag)
                            .where(
                                    productTag.product.eq(product), // Correlated subquery
                                    tag.type.eq(type),
                                    tag.name.in(names)
                            ).exists();
                    anyMatch = (anyMatch == null) ? condition : anyMatch.or(condition);
                }
            }
            return anyMatch;
        }
    }

    private BooleanExpression sameProductType(ProductCategory category) {
        if (category == null) return null;

        return JPAExpressions
            .selectOne()
            .from(productTag)
            .join(productTag.tag, tag)
            .where(
                productTag.product.eq(product),
                tag.type.eq(TagType.PRODUCT_TYPE),
                tag.name.eq(category.name())
            )
            .exists();
    }


    private NumberExpression<Integer> buildRelevanceScore(ProductCategory category, List<Long> tagIds) {
        NumberExpression<Integer> typeScore =
            new CaseBuilder().when(sameProductType(category)).then(1).otherwise(0);

        NumberExpression<Integer> tagScore =
            new CaseBuilder().when(hasAnyTags(tagIds)).then(1).otherwise(0);

        return typeScore.add(tagScore);
    }

    private void applySort(JPAQuery<Product> query, Pageable pageable) {


        for (Sort.Order o : pageable.getSort()) {
            String prop = o.getProperty();
            Order dir = o.isAscending() ? Order.ASC : Order.DESC;

            OrderSpecifier<?> spec = switch (prop) {
                case "id" -> new OrderSpecifier<>(dir, product.id);
                case "price" -> new OrderSpecifier<>(dir, product.price);
                case "createdAt" -> new OrderSpecifier<>(dir, product.createdAt);
                case "popularity" -> new OrderSpecifier<>(dir, product.likeCount); //좋아요 순
                default -> null;
            };

            if (spec != null) query.orderBy(spec);
        }

    }

    private void applySort(JPAQuery<Product> query, Sort sort) {

        for (Sort.Order o : sort) {
            String prop = o.getProperty();
            Order dir = o.isAscending() ? Order.ASC : Order.DESC;

            OrderSpecifier<?> spec = switch (prop) {
                case "id" ->new OrderSpecifier<>(dir, product.id);
                case "price" -> new OrderSpecifier<>(dir, product.price);
                case "createdAt" -> new OrderSpecifier<>(dir, product.createdAt);
                case "popularity" -> new OrderSpecifier<>(dir, product.likeCount);
                default -> null;
            };
            if (spec != null) query.orderBy(spec);
        }
    }


    public List<CandidateProductInfo> findCandidateProductList(
            ProductType productType,
            List<ProductCategory> categories,
            Integer maxBudget,
            Integer minBudget,
            List<String> preferredColors
    ) {

        return jpaQueryFactory
                .from(product)
                .leftJoin(product.productImageList, productImage)
                .leftJoin(product.productTagList, productTag)
                .leftJoin(productTag.tag, tag)
                .where(
                        categoryOrTypeEq(categories, productType),

                        priceBetween(minBudget, maxBudget),

                        colorIn(preferredColors)
                )
                .limit(50)
                .transform(
                        groupBy(product.id).list(
                                Projections.constructor(CandidateProductInfo.class,
                                        product.id,
                                        product.name,
                                        product.price,
                                        product.description,
                                        GroupBy.set(productImage.imageUrl),
                                        GroupBy.set(
                                                Projections.constructor(
                                                        ProductTagInfo.class,
                                                        tag.id,
                                                        tag.name,
                                                        tag.type
                                                )
                                        )
                                )
                        )
                );
    }

// --------------------------------------------------
// [동적 쿼리 조건 메서드 - 서브쿼리 사용]
// --------------------------------------------------

    /**
     * 상품이 해당 카테고리(또는 타입) 태그를 가지고 있는지 검사
     */
    private BooleanExpression categoryOrTypeEq(List<ProductCategory> categories, ProductType type) {
        // 검색 대상이 없으면 조건 패스
        if ((categories == null || categories.isEmpty()) && type == null) {
            return null;
        }

        QProductTag subPt = new QProductTag("subPt_cat");
        QTag subTag = new QTag("subTag_cat");

        List<String> targetTagNames = new ArrayList<>();

        if (categories != null && !categories.isEmpty()) {
            // 카테고리가 있으면 카테고리 이름들로 검색 (예: "CUSHION", "RUG")
            targetTagNames = categories.stream()
                    .map(Enum::name)
                    .toList();
        } else {
            // 카테고리가 없으면 대분류 이름으로 검색 (예: "FABRIC_DECOR")
            targetTagNames.add(type.name());
        }

        // 2. 서브쿼리: "해당 이름을 가진 PRODUCT_TYPE 태그를 보유한 Product ID 찾기"
        return product.id.in(
                JPAExpressions
                        .select(subPt.product.id)
                        .from(subPt)
                        .join(subPt.tag, subTag)
                        .where(
                                subTag.type.eq(TagType.PRODUCT_TYPE), // 태그 타입이 PRODUCT_TYPE이고
                                subTag.name.in(targetTagNames)       // 이름이 매칭되는 것
                        )
        );
    }

    /**
     * 상품이 해당 컬러 태그를 가지고 있는지 검사
     */
    private BooleanExpression colorIn(List<String> colors) {
        if (colors == null || colors.isEmpty()) {
            return null;
        }

        QProductTag subPt = new QProductTag("subPt_color");
        QTag subTag = new QTag("subTag_color");

        return product.id.in(
                JPAExpressions
                        .select(subPt.product.id)
                        .from(subPt)
                        .join(subPt.tag, subTag)
                        .where(
                                subTag.type.eq(TagType.COLOR),
                                subTag.name.in(colors)
                        )
        );
    }

    private BooleanExpression priceBetween(Integer min, Integer max) {
        if (min == null && max == null) return null;
        if (min != null && max != null) return product.price.between(min, max);
        if (min != null) return product.price.goe(min);
        return product.price.loe(max);
    }
    private BooleanExpression hasAnyTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return null;

        return JPAExpressions.selectOne()
            .from(productTag)
            .where(
                productTag.product.eq(product),
                productTag.tag.id.in(tagIds)
            )
            .exists();
    }
}