package com.roome.roome.be.domain.user.dto.response;

import com.roome.roome.be.domain.product.dto.response.CommonProductInfo;

import java.util.List;

public record UserScrappedProductListResponse(
        List<CommonProductInfo> userScrapProductList
) {
}
