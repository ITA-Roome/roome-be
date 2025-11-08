package com.roome.roome.be.domain.product.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.enums.Category;
import com.roome.roome.be.domain.product.enums.TagType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Map;

import static com.roome.roome.be.domain.product.entity.QProduct.product;
import static com.roome.roome.be.domain.product.entity.QProductTag.productTag;
import static com.roome.roome.be.domain.product.entity.QTag.tag;
import static com.roome.roome.be.domain.shop.entity.QShop.shop;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory; // QueryDSL 사용을 위해 주입

    @Override
    public Page<Product> findByDynamicFilters(
            Long shopId,
            Category category,
            String keyWord,
            Integer minPrice,
            Integer maxPrice,
            Map<TagType, List<String>> tagFilters,
            String match,
            Pageable pageable
    ) {

        // 1. 기본 쿼리 (SELECT p FROM Product p ...)
        JPAQuery<Product> query = queryFactory
                .selectFrom(product)
                .leftJoin(product.shop, shop).fetchJoin() // N+1 방지
                .distinct();

        // 2. 동적 WHERE 조건 조립
        query.where(
                shopIdEq(shopId),
                categoryEq(category),
                nameContains(keyWord),
                priceGoe(minPrice),
                priceLoe(maxPrice),
                tagFilter(tagFilters, match) // ★ 모든 태그 필터를 이 메서드가 처리
        );

        // 3. 페이징 및 정렬 적용
        query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 4. 쿼리 실행 (Content)
        List<Product> content = query.fetch();

        // 5. Count 쿼리 실행 (페이징을 위해)
        JPAQuery<Long> countQuery = queryFactory
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

    private BooleanExpression shopIdEq(Long shopId) {
        return shopId != null ? product.shop.id.eq(shopId) : null;
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? product.category.eq(category) : null;
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
        }

        else {
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
}