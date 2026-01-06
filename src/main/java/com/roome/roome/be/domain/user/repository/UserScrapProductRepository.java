package com.roome.roome.be.domain.user.repository;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserScrapProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserScrapProductRepository extends JpaRepository<UserScrapProduct, Long> {
    Optional<UserScrapProduct> findByUserAndProduct(User user, Product product);

    @Query("SELECT usp.product.id FROM UserScrapProduct usp WHERE usp.user.id = :userId And usp.product.id IN :productIds")
    Set<Long> findScrappedProductIds(@Param("userId") Long userId, @Param("productIds") List<Long> productIds);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
