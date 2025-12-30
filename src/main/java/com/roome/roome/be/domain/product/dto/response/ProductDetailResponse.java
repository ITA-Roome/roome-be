package com.roome.roome.be.domain.product.dto.response;

import java.util.List;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.shop.dto.response.ShopSummaryResponse;
import org.springframework.data.domain.Page;

public record ProductDetailResponse(
	Long id,
	String name,
	Integer price,
	String category,
	String productUrl,
	String description,
	ShopSummaryResponse shop,
	String thumbnailUrl,
	List<ProductImageResponse> images,
	List<ProductTagResponse> tags,
	List<RelatedProductResponse> relatedProductList
) {
	public static ProductDetailResponse from(
			Product product,
			String thumbnailUrl,
			List<ProductImageResponse> images,
			List<ProductTagResponse> tags,
			ShopSummaryResponse shop,
			List<RelatedProductResponse> relatedProductList
	) {
		return new ProductDetailResponse(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getCategory().name(),
				product.getProductUrl(),
				product.getDescription(),
				shop,
				thumbnailUrl,
				images,
				tags,
				relatedProductList
		);
	}
}