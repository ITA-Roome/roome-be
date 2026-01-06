package com.roome.roome.be.domain.product.dto.response;

import java.util.List;

import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.shop.dto.response.ShopSummaryResponse;

public record ProductDetailResponse(
	Long id,
	String name,
	Integer price,
	ProductCategory category,
	String productUrl,
	String description,
	ShopSummaryResponse shop,
	String thumbnailUrl,
	Integer likeCount,
	Integer scrapCount,
	Boolean isLiked,
	Boolean isScrapped,
	List<ProductImageResponse> images,
	List<ProductTagResponse> tags,
	List<RelatedProductResponse> relatedProductList
) {
	public static ProductDetailResponse from(
			Product product,
			String thumbnailUrl,
			ProductCategory category,
			List<ProductImageResponse> images,
			List<ProductTagResponse> tags,
			ShopSummaryResponse shop,
			List<RelatedProductResponse> relatedProductList,
			Boolean isLiked,
			Boolean isScrapped
	) {
		return new ProductDetailResponse(
				product.getId(),
				product.getName(),
				product.getPrice(),
				category,
				product.getProductUrl(),
				product.getDescription(),
				shop,
				thumbnailUrl,
				product.getLikeCount(),
				product.getScrapCount(),
				isLiked,
				isScrapped,
				images,
				tags,
				relatedProductList
		);
	}
}