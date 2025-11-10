package com.roome.roome.be.domain.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.roome.roome.be.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {

	@EntityGraph(attributePaths = {"shop"})
	Optional<Product> findById(Long id);
}