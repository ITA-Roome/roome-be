package com.roome.roome.be.domain.user.repository;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserViewRepository extends JpaRepository<UserView, Long> {
    public UserView findByUserAndProduct(User user, Product product);
}
