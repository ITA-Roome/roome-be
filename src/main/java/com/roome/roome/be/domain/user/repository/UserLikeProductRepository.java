package com.roome.roome.be.domain.user.repository;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserLikeProduct;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserLikeProductRepository extends JpaRepository<UserLikeProduct, Long> {
    public Optional<UserLikeProduct> findByUserAndProduct(User user, Product product);

    @Query("SELECT ul.product.id FROM UserLikeProduct ul WHERE ul.user.id = :userId AND ul.product.id IN :productIds")
    Set<Long> findLikedProductIds(@Param("userId") Long userId, @Param("productIds") List<Long> productIds);
}
