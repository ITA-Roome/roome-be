package com.roome.roome.be.domain.user.repository;

import com.roome.roome.be.domain.user.dto.response.UserLikeProduct;

import java.util.List;

public interface UserLikeCustomRepository {
    List<UserLikeProduct> findUserLikeProductListByUserId(Long userId);
}
