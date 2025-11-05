package com.roome.roome.be.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.roome.roome.be.domain.product.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
