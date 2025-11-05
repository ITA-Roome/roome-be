package com.roome.roome.be.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.roome.roome.be.domain.product.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

	@Query("select i from ProductImage i where i.product.id = :productId order by i.sortOrder asc")
	List<ProductImage> findByProductIdOrderBySortOrder(Long productId);
}
