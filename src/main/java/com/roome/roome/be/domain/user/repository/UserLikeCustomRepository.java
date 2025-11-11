package com.roome.roome.be.domain.user.repository;

import com.roome.roome.be.domain.product.dto.response.CommonProductInfo;

import java.util.List;

public interface UserLikeCustomRepository {
    List<CommonProductInfo> findUserLikeProductListByUserId(Long userId);
}
