package com.roome.roome.be.domain.product.repository;

import java.util.List;
import java.util.Optional;

import com.roome.roome.be.domain.product.enums.TagType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.roome.roome.be.domain.product.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {

	@EntityGraph(attributePaths = {"shop"})
	Optional<Product> findById(Long id);

	@Query("SELECT DISTINCT p FROM Product p " +
			"JOIN p.productTagList pt " +
			"JOIN pt.tag t " +
			"WHERE t.type = :tagType " +
			"AND t.name IN :tagNames " +
			"AND p.price BETWEEN :minPrice AND :maxPrice")
	List<Product> findByCategoryTagsAndBudget(
			@Param("tagType") TagType tagType,
			@Param("tagNames") List<String> tagNames,
			@Param("minPrice") Integer minPrice,
			@Param("maxPrice") Integer maxPrice
	);
}