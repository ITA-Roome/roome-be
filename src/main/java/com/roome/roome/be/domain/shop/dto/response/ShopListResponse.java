package com.roome.roome.be.domain.shop.dto.response;

import java.util.List;

public record ShopListResponse(
	List<ShopSummaryResponse> content,
	int page,
	int size,
	long totalElements
) {
}
