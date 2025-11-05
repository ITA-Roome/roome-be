package com.roome.roome.be.domain.product.repository;

import java.util.Optional;

import com.roome.roome.be.domain.product.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@EntityGraph(attributePaths = {"shop"})
	Optional<Product> findById(Long id);
}