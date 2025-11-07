package com.roome.roome.be.domain.product.repository;

import java.util.List;
import java.util.Optional;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.enums.Category;
import com.roome.roome.be.domain.product.enums.Color;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@EntityGraph(attributePaths = {"shop"})
	Optional<Product> findById(Long id);
	Page<Product> findByCategory(Category category, Pageable pageable);

	@Query("""
SELECT DISTINCT p
FROM Product p
LEFT JOIN p.productTags pt
LEFT JOIN pt.tag t
WHERE (:category IS NULL OR p.category = :category)
  AND (t.type = com.roome.roome.be.domain.product.enums.TagType.COLOR AND t.name IN :colorNames)
""")
	Page<Product> findByCategoryAndAnyColorTags(@Param("category") Category category,
		@Param("colorNames") List<String> colorNames,
		Pageable pageable);

	@Query("""
SELECT p
FROM Product p
JOIN p.productTags pt
JOIN pt.tag t
WHERE (:category IS NULL OR p.category = :category)
  AND t.type = com.roome.roome.be.domain.product.enums.TagType.COLOR
  AND t.name IN :colorNames
GROUP BY p.id
HAVING COUNT(DISTINCT t.name) = :size
""")
	Page<Product> findByCategoryAndAllColorTags(@Param("category") Category category,
		@Param("colorNames") List<String> colorNames,
		@Param("size") long size,
		Pageable pageable);
}