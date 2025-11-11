package com.roome.roome.be.domain.product.repository;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.enums.Category;
import com.roome.roome.be.domain.product.enums.TagType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * QueryDSL을 사용한 동적 쿼리 인터페이스
 */
public interface ProductCustomRepository {

    /**
     * 상품 목록을 동적 필터(태그 포함)로 조회합니다.
     *
     * @param tagFilters (예: {TagType.COLOR: ["WHITE", "BLUE"], TagType.MATERIAL: ["WOOD"]})
     * @param match      (any: OR 조건, all: AND 조건)
     */
    Page<Product> findByDynamicFilters(
            Long shopId,
            Category category,
            String keyWord,
            Integer minPrice,
            Integer maxPrice,
            Map<TagType, List<String>> tagFilters,
            String match,
            Pageable pageable
    );
}