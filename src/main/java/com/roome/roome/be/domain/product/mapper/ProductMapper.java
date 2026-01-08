package com.roome.roome.be.domain.product.mapper;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.product.dto.response.*;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.ProductImage;
import com.roome.roome.be.domain.product.entity.ProductTag;
import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.product.enums.TagType;
import com.roome.roome.be.domain.shop.dto.response.ShopSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ImageUrlBuilder imageUrlBuilder;

    @Value("${storage.defaults.shop-logo}")
    private String defaultShopLogoUrl;

    // 상세 조회 응답 변환
    public ProductDetailResponse toDetailResponse(
            Product product,
            List<ProductImage> productImages,
            List<ProductTag> productTags,
            List<RelatedProductResponse> relatedProducts,
            boolean isLiked,
            boolean isScrapped
    ) {
        // 1. 이미지 DTO 변환
        List<ProductImageResponse> imageResponses = productImages.stream()
                .map(img -> ProductImageResponse.from(img, imageUrlBuilder))
                .toList();

        // 2. 썸네일 URL 결정
        String thumbnailUrl = (product.getThumbnailKey() != null)
                ? imageUrlBuilder.build(product.getThumbnailKey())
                : (imageResponses.isEmpty() ? null : imageResponses.get(0).url());

        // 3. 태그 DTO 변환 및 카테고리 추출
        List<ProductTagResponse> tagResponses = productTags.stream()
                .map(pt -> ProductTagResponse.from(pt.getTag()))
                .toList();

        ProductCategory category = tagResponses.stream()
                .filter(t -> t.tagType() == TagType.PRODUCT_TYPE)
                .findFirst()
                .map(ProductTagResponse::name)
                .map(ProductCategory::valueOf)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

        // 4. Shop 정보 변환
        String logoUrl = (product.getShop().getLogoObjectKey() != null && !product.getShop().getLogoObjectKey().isBlank())
                ? imageUrlBuilder.build(product.getShop().getLogoObjectKey())
                : defaultShopLogoUrl;

        ShopSummaryResponse shopResponse = ShopSummaryResponse.from(product.getShop(), logoUrl);

        // 5. 최종 응답 생성
        return ProductDetailResponse.from(
                product,
                thumbnailUrl,
                category,
                imageResponses,
                tagResponses,
                shopResponse,
                relatedProducts,
                isLiked,
                isScrapped
        );
    }

    // 목록 조회 아이템 변환
    public ProductListItemResponse toListItemResponse(
            Product product,
            ProductCategory category,
            boolean isLiked,
            boolean isScrapped
    ) {
        return ProductListItemResponse.from(
                product,
                category,
                imageUrlBuilder,
                isLiked,
                isScrapped
        );
    }
}