package com.roome.roome.be.domain.user.repository;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserScrapRepository extends JpaRepository<UserScrap, Long> {
    Optional<UserScrap> findByUserAndProduct(User user, Product product);
}
