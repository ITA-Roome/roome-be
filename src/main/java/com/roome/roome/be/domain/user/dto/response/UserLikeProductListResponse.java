package com.roome.roome.be.domain.user.dto.response;

import java.util.List;

public record UserLikeProductListResponse(
        List<UserLikeProduct> userLikeProductList
) {
}
