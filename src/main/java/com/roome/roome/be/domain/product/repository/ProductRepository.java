package com.roome.roome.be.domain.product.repository;

import java.util.Optional;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.enums.Category;
import com.roome.roome.be.domain.product.enums.Color;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@EntityGraph(attributePaths = {"shop"})
	Optional<Product> findById(Long id);
	Page<Product> findByCategory(Category category, Pageable pageable);
	Page<Product> findByColor(Color color, Pageable pageable);
	Page<Product> findByCategoryAndColor(Category category, Color color, Pageable pageable);
}