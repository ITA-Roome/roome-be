package com.roome.roome.be.domain.product.service;

import java.util.*;
import java.util.stream.Collectors;

import com.roome.roome.be.domain.product.dto.response.*;
import com.roome.roome.be.domain.product.entity.ProductImage;
import com.roome.roome.be.domain.product.entity.ProductTag;
import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.product.enums.ProductType;
import com.roome.roome.be.domain.product.enums.ProductTypeMapper;
import com.roome.roome.be.domain.product.enums.TagType;
import com.roome.roome.be.domain.product.mapper.ProductMapper;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserOnboarding;
import com.roome.roome.be.domain.user.repository.UserLikeProductRepository;
import com.roome.roome.be.domain.user.repository.UserOnboardingRepository;
import com.roome.roome.be.domain.user.repository.UserRepository;
import com.roome.roome.be.domain.user.repository.UserScrapProductRepository;
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

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductTagRepository productTagRepository;
    private final UserLikeProductRepository userLikeProductRepository;

    private final UserOnboardingRepository userOnboardingRepository;
    private final UserRepository userRepository;

    private final ProductTagService productTagService;
    private final ProductImageService productImageService;
    private final S3Service s3Service;
    private final UserViewService userViewService;
    private final UserScrapProductRepository userScrapProductRepository;

    private final ProductMapper productMapper;

    @Value("${storage.defaults.shop-logo}")
    private String defaultShopLogoUrl;

    // 상품 등록
    @Transactional
    public Long register(RegisterProductRequest registerProductRequest) {
        Product product = productRepository.save(Product.builder()
                .name(registerProductRequest.name())
                .price(registerProductRequest.price())
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

    // 상품 상세 조회 (리팩토링됨)
    @Transactional
    public ProductDetailResponse getProductDetail(Long productId, Long userId) {
        // 1. 데이터 조회
        Product product = getProductById(productId);
        List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrder(productId);
        List<ProductTag> productTags = productTagRepository.findByProductIdWithTag(productId);

        // 2. 유저 조회 기록 저장
        userViewService.registerUserView(product, userId);

        // 3. 좋아요/스크랩 여부 확인
        boolean isLiked = (userId != null) && userLikeProductRepository.existsByUserIdAndProductId(userId, productId);
        boolean isScrapped = (userId != null) && userScrapProductRepository.existsByUserIdAndProductId(userId, productId);

        // 4. 연관 상품 조회
        ProductCategory category = productTags.stream()
                .map(pt -> pt.getTag())
                .filter(t -> t.getType() == TagType.PRODUCT_TYPE)
                .findFirst()
                .map(t -> ProductCategory.valueOf(t.getName()))
                .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

        List<Long> tagIdList = productTags.stream().map(pt -> pt.getTag().getId()).toList();
        List<RelatedProductResponse> relatedProductList = productRepository.findRelatedProductList(productId, category, tagIdList);

        // 5. 변환
        return productMapper.toDetailResponse(product, images, productTags, relatedProductList, isLiked, isScrapped);
    }

    // 상품 목록 조회
    @Transactional(readOnly = true)
    public Page<ProductListItemResponse> getList(
            Long shopId,
            ProductCategory category,
            List<String> colorTags,
            List<String> materialTags,
            List<String> styleTags,
            List<String> featureTags,
            List<String> moodTags,
            List<String> usageTags,
            String match,
            String keyWord,
            Integer minPrice,
            Integer maxPrice,
            Pageable pageable,
            Long userId
    ) {
        final String normalizedMatch = (match == null) ? "any" : match.trim().toLowerCase();

        // 1. 태그 필터 구성
        Map<TagType, List<String>> tagFilters = new HashMap<>();
        if (colorTags != null && !colorTags.isEmpty()) tagFilters.put(TagType.COLOR, colorTags);
        if (materialTags != null && !materialTags.isEmpty()) tagFilters.put(TagType.MATERIAL, materialTags);
        if (styleTags != null && !styleTags.isEmpty()) tagFilters.put(TagType.STYLE, styleTags);
        if (featureTags != null && !featureTags.isEmpty()) tagFilters.put(TagType.FEATURE, featureTags);
        if (moodTags != null && !moodTags.isEmpty()) tagFilters.put(TagType.MOOD, moodTags);
        if (usageTags != null && !usageTags.isEmpty()) tagFilters.put(TagType.USAGE, usageTags);

        // 2. 온보딩 필터 적용 로직
        boolean hasExplicitFilters = (keyWord != null && !keyWord.isBlank())
                || (category != null)
                || (shopId != null)
                || (!tagFilters.isEmpty());

        if (!hasExplicitFilters && userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
            UserOnboarding onboarding = userOnboardingRepository.findByUser(user).orElse(null);

            if (onboarding != null) {
                if (onboarding.getMoodType() != null) tagFilters.put(TagType.MOOD, List.of(onboarding.getMoodType().name()));
                if (onboarding.getSpaceType() != null) tagFilters.put(TagType.USAGE, List.of(onboarding.getSpaceType().name()));
            }
        }

        // 3. 동적 쿼리 실행
        Page<Product> page = productRepository.findByDynamicFilters(
                shopId, category, keyWord, minPrice, maxPrice, tagFilters, normalizedMatch, pageable
        );

        if (page.isEmpty()) return Page.empty(pageable);

        // 4. 부가 데이터 조회 (카테고리, 좋아요/스크랩)
        List<Long> productIds = page.getContent().stream().map(Product::getId).toList();

        Map<Long, ProductCategory> categoryByProductId = productTagRepository
                .findProductTypeByProductIds(productIds, TagType.PRODUCT_TYPE)
                .stream()
                .collect(Collectors.toMap(
                        ProductTagRepository.ProductTypeRow::getProductId,
                        row -> ProductCategory.valueOf(row.getCategoryName())
                ));

        Set<Long> likedProductIds = (userId != null && !productIds.isEmpty())
                ? userLikeProductRepository.findLikedProductIds(userId, productIds)
                : new HashSet<>();
        Set<Long> scrappedProductIds = (userId != null && !productIds.isEmpty())
                ? userScrapProductRepository.findScrappedProductIds(userId, productIds)
                : new HashSet<>();

        // 5. 변환 (Mapper 위임)
        return page.map(p -> productMapper.toListItemResponse(
                p,
                categoryByProductId.get(p.getId()),
                likedProductIds.contains(p.getId()),
                scrappedProductIds.contains(p.getId())
        ));
    }

    // 상품 수정
    @Transactional
    public void updateProduct(Long productId, UpdateProductRequest updateProductRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

        if (updateProductRequest.name() != null) product.updateName(updateProductRequest.name());
        if (updateProductRequest.price() != null) product.updatePrice(updateProductRequest.price());
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

    @Transactional(readOnly = true)
    public List<CandidateProductInfo> getCandidateProductList(
            ProductType productType,            // 대분류 (가구)
            List<ProductCategory> detailedCategories, // 소분류 (책상, 의자...)
            Integer minBudget,
            Integer maxBudget,
            List<String> preferredColors
    ) {
        List<String> searchTagNames;

        if (detailedCategories != null && !detailedCategories.isEmpty()) {
            searchTagNames = detailedCategories.stream()
                    .map(Enum::name)
                    .toList();
        } else {
            searchTagNames = ProductTypeMapper.getProductCategoryList(List.of(productType))
                    .stream()
                    .map(Enum::name)
                    .toList();
        }

        int effectiveMin = (minBudget == null) ? 0 : minBudget;
        int effectiveMax = (maxBudget == null) ? Integer.MAX_VALUE : maxBudget;

        List<Product> products = productRepository.findByCategoryTagsAndBudget(
                TagType.PRODUCT_TYPE,
                searchTagNames,
                effectiveMin,
                effectiveMax
        );

        return products.stream()
                .map(CandidateProductInfo::from)
                .toList();
    }
}
