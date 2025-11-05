package com.roome.roome.be.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.ProductTag;
import com.roome.roome.be.domain.product.entity.Tag;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {
	List<ProductTag> findByProduct(Product product);
	void deleteByProductAndTag(Product product, Tag tag);
}