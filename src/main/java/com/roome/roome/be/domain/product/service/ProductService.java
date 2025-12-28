package com.roome.roome.be.domain.product.service;

import java.util.*;

import com.roome.roome.be.domain.product.dto.response.*;
import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.product.enums.TagType;
import com.roome.roome.be.domain.user.repository.UserLikeRepository;
import com.roome.roome.be.domain.user.service.UserViewService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.common.s3.service.S3Service;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.product.dto.request.RegisterProductRequest;
import com.roome.roome.be.domain.product.dto.request.UpdateProductRequest;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.repository.ProductImageRepository;
import com.roome.roome.be.domain.product.repository.ProductRepository;
import com.roome.roome.be.domain.product.repository.ProductTagRepository;
import com.roome.roome.be.domain.shop.dto.response.ShopSummaryResponse;
import com.roome.roome.be.domain.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductTagRepository productTagRepository;
    private final UserLikeRepository userLikeRepository;

    private final ProductTagService productTagService;
    private final ProductImageService productImageService;
    private final S3Service s3Service;
    private final ImageUrlBuilder imageUrlBuilder;
    private final UserViewService userViewService;

    @Value("${storage.defaults.shop-logo}")
    private String defaultShopLogoUrl;

    // 상품 등록
    @Transactional
    public Long register(RegisterProductRequest registerProductRequest) {
        Product product = productRepository.save(Product.builder()
                .name(registerProductRequest.name())
                .price(registerProductRequest.price())
                .category(registerProductRequest.category())
                .productUrl(registerProductRequest.productUrl())
                .description(registerProductRequest.description())
                .shop(shopRepository.findById(registerProductRequest.shopId())
                        .orElseThrow(() -> new GeneralException(ErrorStatus.SHOP_NOT_FOUND)))
                .build());

        if (registerProductRequest.images() != null) {
            productImageService.commitSessionImages(product.getId(), registerProductRequest.images());
        }

        productTagService.applyTags(product.getId(), registerProductRequest.tags());
        return product.getId();
    }

    // 상품 상세 조회
    @Transactional
    public ProductDetailResponse getDetail(Long productId, Long userId) {
        Product product = getProductById(productId);

        // ------------------------------
        // 1) 대표 이미지 + 상세 이미지
        // ------------------------------
        var images = productImageRepository.findByProductIdOrderBySortOrder(productId)
                .stream()
                .map(productImage -> ProductImageResponse.from(productImage, imageUrlBuilder))
                .toList();

        String thumbnailUrl = (product.getThumbnailKey() != null)
                ? imageUrlBuilder.build(product.getThumbnailKey())
                : (images.isEmpty() ? null : images.get(0).url());

        // ------------------------------
        // 2) 상품 태그
        // ------------------------------
        var tags = productTagRepository.findByProductIdWithTag(productId).stream()
                .map(productTag -> ProductTagResponse.from(productTag.getTag()))
                .toList();

        // ------------------------------
        // 3) Shop 정보
        // ------------------------------
        String logoUrl = (product.getShop().getLogoObjectKey() != null && !product.getShop().getLogoObjectKey().isBlank())
                ? imageUrlBuilder.build(product.getShop().getLogoObjectKey())
                : defaultShopLogoUrl;

        // ------------------------------
        // 4) 유저 조회 로그 저장
        // ------------------------------
        var shop = ShopSummaryResponse.from(product.getShop(), logoUrl);
        userViewService.registerUserView(product, userId);

        List<Long> tagIdList = tags.stream().map(ProductTagResponse::id).toList();

        List<RelatedProductResponse> relatedProductList =
                productRepository.findRelatedProductList(
                        productId,
                        product.getCategory(),
                        tagIdList
                );
        return ProductDetailResponse.from(product, thumbnailUrl, images, tags, shop, relatedProductList);
    }

    // 상품 목록 조회
    @Transactional(readOnly = true)
    public Page<ProductListItemResponse> getList(
            Long shopId,                 // 가게 필터
            ProductCategory category,
            List<String> colorTags,
            List<String> materialTags,
            List<String> styleTags,
            List<String> featureTags,
            List<String> moodTags,
            String match,                // 기본 any
            String keyWord,
            Integer minPrice,
            Integer maxPrice,
            Pageable pageable,
            Long userId
    ) {
        // match 문자열 정규화
        final String normalizedMatch = (match == null) ? "any" : match.trim().toLowerCase();

        // 모든 태그 파라미터를 Map으로 조립
        Map<TagType, List<String>> tagFilters = new HashMap<>();
        if (colorTags != null && !colorTags.isEmpty()) tagFilters.put(TagType.COLOR, colorTags);
        if (materialTags != null && !materialTags.isEmpty()) tagFilters.put(TagType.MATERIAL, materialTags);
        if (styleTags != null && !styleTags.isEmpty()) tagFilters.put(TagType.STYLE, styleTags);
        if (featureTags != null && !featureTags.isEmpty()) tagFilters.put(TagType.FEATURE, featureTags);
        if (moodTags != null && !moodTags.isEmpty()) tagFilters.put(TagType.MOOD, moodTags);

        Page<Product> page = productRepository.findByDynamicFilters(
                shopId,
                category,
                keyWord,
                minPrice,
                maxPrice,
                tagFilters,
                normalizedMatch,
                pageable
        );

        Set<Long> likedProductIds = new HashSet<>();
        if (userId != null && !page.isEmpty()) {
            List<Long> productIds = page.getContent().stream()
                    .map(Product::getId)
                    .toList();

            if (!productIds.isEmpty()) {
                likedProductIds = userLikeRepository.findLikedProductIds(userId, productIds);
            }
        }

        // [수정] map 할 때 likedProductIds에 포함되어 있는지 확인하여 true/false 전달
        final Set<Long> finalLikedProductIds = likedProductIds;
        return page.map(p -> ProductListItemResponse.from(
                p,
                imageUrlBuilder,
                finalLikedProductIds.contains(p.getId())
        ));
    }

    // 상품 수정
    @Transactional
    public void updateProduct(Long productId, UpdateProductRequest updateProductRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

        if (updateProductRequest.name() != null) product.updateName(updateProductRequest.name());
        if (updateProductRequest.price() != null) product.updatePrice(updateProductRequest.price());
        if (updateProductRequest.category() != null) product.updateCategory(updateProductRequest.category());
        if (updateProductRequest.productUrl() != null) product.updateProductUrl(updateProductRequest.productUrl());
        if (updateProductRequest.description() != null) product.updateDescription(updateProductRequest.description());

        if (updateProductRequest.tags() != null) {
            productTagService.applyTags(product.getId(), updateProductRequest.tags());
        }
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

        List<String> objectKeys = productImageRepository.findByProductIdOrderBySortOrder(productId)
                .stream()
                .map(productImage -> productImage.getObjectKey())
                .toList();
        String thumbnailKey = product.getThumbnailKey();

        productImageRepository.deleteByProductId(productId);
        productTagRepository.deleteByProductId(productId);
        productRepository.delete(product);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    if (thumbnailKey != null) s3Service.deleteObject(thumbnailKey);
                    for (String key : objectKeys) {
                        s3Service.deleteObject(key);
                    }
                } catch (Exception e) {
                    log.warn("S3 delete failed for productId={}", productId, e);
                }
            }
        });
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));
    }

    public List<CandidateProductInfo> getCandidateProductList(
            Integer maxBudget,
            Integer minBudget,
            List<String> preferredColors
    ) {
        return productRepository.findCandidateProductList(maxBudget,minBudget,preferredColors);
    }

}
