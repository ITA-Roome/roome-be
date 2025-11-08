package com.roome.roome.be.domain.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.enums.Category;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@EntityGraph(attributePaths = {"shop"})
	Optional<Product> findById(Long id);


	// 색상 any 옵션
	@EntityGraph(attributePaths = {"shop"})
	@Query("""
SELECT DISTINCT p
FROM Product p
LEFT JOIN p.shop s
LEFT JOIN p.productTags pt
LEFT JOIN pt.tag t
WHERE (:shopId IS NULL OR s.id = :shopId)
  AND (:category IS NULL OR p.category = :category)
  AND (:q IS NULL OR p.name LIKE CONCAT('%', :q, '%'))
  AND (:minPrice IS NULL OR p.price >= :minPrice)
  AND (:maxPrice IS NULL OR p.price <= :maxPrice)
  AND (
        :hasColor = false
        OR (t.type = com.roome.roome.be.domain.product.enums.TagType.COLOR AND t.name IN :colorNames)
      )
""")
	Page<Product> findByFiltersAnyColors(@Param("shopId") Long shopId,
		@Param("category") Category category,
		@Param("q") String q,
		@Param("minPrice") Integer minPrice,
		@Param("maxPrice") Integer maxPrice,
		@Param("hasColor") boolean hasColor,
		@Param("colorNames") List<String> colorNames,
		Pageable pageable);

//색상 all 옵션
	@EntityGraph(attributePaths = {"shop"})
	@Query("""
SELECT p
FROM Product p
JOIN p.shop s
LEFT JOIN p.productTags pt
LEFT JOIN pt.tag t
WHERE (:shopId IS NULL OR s.id = :shopId)
  AND (:category IS NULL OR p.category = :category)
  AND (:q IS NULL OR p.name LIKE CONCAT('%', :q, '%'))
  AND (:minPrice IS NULL OR p.price >= :minPrice)
  AND (:maxPrice IS NULL OR p.price <= :maxPrice)
  AND (
        :hasColor = false
        OR p.id IN (
            SELECT pt2.product.id
            FROM ProductTag pt2
            JOIN pt2.tag t2
            WHERE t2.type = com.roome.roome.be.domain.product.enums.TagType.COLOR
              AND t2.name IN :colorNames
            GROUP BY pt2.product.id
            HAVING COUNT(DISTINCT t2.name) = :size
        )
      )
GROUP BY p.id
""")
	Page<Product> findByFiltersAllColors(@Param("shopId") Long shopId,
		@Param("category") Category category,
		@Param("q") String q,
		@Param("minPrice") Integer minPrice,
		@Param("maxPrice") Integer maxPrice,
		@Param("hasColor") boolean hasColor,
		@Param("colorNames") List<String> colorNames,
		@Param("size") long size,
		Pageable pageable);
}