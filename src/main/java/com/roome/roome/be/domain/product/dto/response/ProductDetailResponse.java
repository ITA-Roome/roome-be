package com.roome.roome.be.domain.product.dto.response;

import java.util.List;

import com.roome.roome.be.domain.shop.dto.response.ShopSummaryResponse;

public record ProductDetailResponse(
	Long id,
	String name,
	Integer price,
	String category,
	String productUrl,
	String description,
	ShopSummaryResponse shop,
	String thumbnailUrl, //이거 다시 확인하자 예진아
	List<ProductImageResponse> images,
	List<ProductTagResponse> tags
) {}