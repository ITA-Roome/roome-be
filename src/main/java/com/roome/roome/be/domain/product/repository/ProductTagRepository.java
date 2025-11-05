package com.roome.roome.be.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.ProductTag;
import com.roome.roome.be.domain.product.entity.Tag;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {

	List<ProductTag> findByProduct(Product product);

	void deleteByProductAndTag(Product product, Tag tag);

	@Query("""
           select pt from ProductTag pt
           join fetch pt.tag t
           where pt.product.id = :productId
           """)
	List<ProductTag> findByProductIdWithTag(Long productId);

	@Modifying
	@Query("delete from ProductTag pt where pt.product.id = :productId")
	void deleteByProductId(@Param("productId") Long productId);
}