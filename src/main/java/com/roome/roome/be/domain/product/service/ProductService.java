package com.roome.roome.be.domain.product.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.s3.service.S3Service;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.product.dto.request.RegisterProductRequest;
import com.roome.roome.be.domain.product.dto.request.UpdateProductRequest;
import com.roome.roome.be.domain.product.dto.response.CandidateProductInfo;
import com.roome.roome.be.domain.product.dto.response.ProductDetailResponse;
import com.roome.roome.be.domain.product.dto.response.ProductListItemResponse;
import com.roome.roome.be.domain.product.dto.response.RelatedProductResponse;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.entity.ProductImage;
import com.roome.roome.be.domain.product.entity.ProductTag;
import com.roome.roome.be.domain.product.enums.ProductCategory;
import com.roome.roome.be.domain.product.enums.ProductType;
import com.roome.roome.be.domain.product.enums.ProductTypeMapper;
import com.roome.roome.be.domain.product.enums.TagType;
import com.roome.roome.be.domain.product.mapper.ProductMapper;
import com.roome.roome.be.domain.product.repository.ProductImageRepository;
import com.roome.roome.be.domain.product.repository.ProductRepository;
import com.roome.roome.be.domain.product.repository.ProductTagRepository;
import com.roome.roome.be.domain.shop.repository.ShopRepository;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserOnboarding;
import com.roome.roome.be.domain.user.repository.UserLikeProductCustomRepositoryImpl;
import com.roome.roome.be.domain.user.repository.UserLikeProductRepository;
import com.roome.roome.be.domain.user.repository.UserOnboardingRepository;
import com.roome.roome.be.domain.user.repository.UserRepository;
import com.roome.roome.be.domain.user.repository.UserScrapProductRepository;
import com.roome.roome.be.domain.user.service.UserViewService;

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
    private final UserLikeProductRepository userLikeProductRepository;

    private final UserOnboardingRepository userOnboardingRepository;
    private final UserRepository userRepository;

    private final ProductTagService productTagService;
    private final ProductImageService productImageService;
    private final S3Service s3Service;
    private final UserViewService userViewService;
    private final UserScrapProductRepository userScrapProductRepository;

    private final ProductMapper productMapper;
    private final UserLikeProductCustomRepositoryImpl userLikeProductCustomRepositoryImpl;

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
        final int pageSize = pageable.getPageSize();
        final long offset = pageable.getOffset();

        if (pageSize <= 0) throw new GeneralException(ErrorStatus.BAD_REQUEST);

        // sort 파라미터를 명시했는지 여부
        boolean hasSortParam = !pageable.getSort().isUnsorted();

        Map<TagType, List<String>> explicitTagFilters = new HashMap<>();
        if (colorTags != null && !colorTags.isEmpty()) explicitTagFilters.put(TagType.COLOR, colorTags);
        if (materialTags != null && !materialTags.isEmpty()) explicitTagFilters.put(TagType.MATERIAL, materialTags);
        if (styleTags != null && !styleTags.isEmpty()) explicitTagFilters.put(TagType.STYLE, styleTags);
        if (featureTags != null && !featureTags.isEmpty()) explicitTagFilters.put(TagType.FEATURE, featureTags);
        if (moodTags != null && !moodTags.isEmpty()) explicitTagFilters.put(TagType.MOOD, moodTags);
        if (usageTags != null && !usageTags.isEmpty()) explicitTagFilters.put(TagType.USAGE, usageTags);

        boolean hasExplicitFilters = (keyWord != null && !keyWord.isBlank())
            || (category != null)
            || (shopId != null)
            || (!explicitTagFilters.isEmpty());

        // 필터 있거나 sort 있으면  추천 스킵
        if (hasExplicitFilters || hasSortParam ) {
            Page<Product> page = productRepository.findByDynamicFilters(
                shopId, category, keyWord, minPrice, maxPrice,
                explicitTagFilters, normalizedMatch, pageable
            );
            if (page.isEmpty()) return Page.empty(pageable);
            return mapToListItemPage(page, userId);
        }

        //온보딩 추천 상품 보여주는 로직
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        UserOnboarding onboarding = userOnboardingRepository.findByUser(user).orElse(null);

        Map<TagType, List<String>> onboardingFilters = new HashMap<>();
        if (onboarding != null) {
            if (onboarding.getMoodType() != null) onboardingFilters.put(TagType.MOOD, List.of(onboarding.getMoodType().name()));
            if (onboarding.getSpaceType() != null) onboardingFilters.put(TagType.USAGE, List.of(onboarding.getSpaceType().name()));
        }

        if (onboardingFilters.isEmpty()) {
            Page<Product> page = productRepository.findByDynamicFilters(
                shopId, category, keyWord, minPrice, maxPrice,
                Map.of(), normalizedMatch, pageable
            );
            if (page.isEmpty()) return Page.empty(pageable);
            return mapToListItemPage(page, userId);
        }

        // 온보딩 추천 +. 추천 이어서 나머지 모두 id desc로 정렬(기본 정렬)
        Sort bucketSort = Sort.by(Sort.Direction.DESC, "id");

        long recTotal = productRepository.countByDynamicFilters(
            shopId, category, keyWord, minPrice, maxPrice,
            onboardingFilters, "any"
        );

        long totalAll = productRepository.countByDynamicFilters(
            shopId, category, keyWord, minPrice, maxPrice,
            Map.of(), normalizedMatch
        );

        long recStart = offset;
        int recLimit = (recStart < recTotal)
            ? (int) Math.min(pageSize, recTotal - recStart)
            : 0;

        List<Product> recommended = (recLimit > 0)
            ? productRepository.findSliceByDynamicFilters(
            shopId, category, keyWord, minPrice, maxPrice,
            onboardingFilters, "any",
            Collections.emptyList(),
            recStart,
            recLimit,
            bucketSort
        )
            : List.of();

        int remain = pageSize - recommended.size();
        if (remain <= 0) {
            return mapToListItemPage(recommended, pageable, totalAll, userId);
        }

        long nonRecOffset = Math.max(0, offset - recTotal);

        List<Product> nonRecommended = productRepository.findNonRecommendedSliceByFilters(
            shopId, category, keyWord, minPrice, maxPrice,
            Map.of(), normalizedMatch,
            onboardingFilters, "any",
            nonRecOffset,
            remain,
            bucketSort
        );

        List<Product> mixed = new ArrayList<>(pageSize);
        mixed.addAll(recommended);
        mixed.addAll(nonRecommended);

        if (mixed.isEmpty()) return Page.empty(pageable);
        return mapToListItemPage(mixed, pageable, totalAll, userId);
    }



    private Page<ProductListItemResponse> mapToListItemPage(Page<Product> page, Long userId) {
        List<Product> products = page.getContent();
        return mapToListItemPage(products, page.getPageable(), page.getTotalElements(), userId);
    }


    private Page<ProductListItemResponse> mapToListItemPage(List<Product> products, Pageable pageable, long total, Long userId) {
        List<Long> productIds = products.stream().map(Product::getId).toList();

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

        List<ProductListItemResponse> content = products.stream()
            .map(p -> productMapper.toListItemResponse(
                p,
                categoryByProductId.get(p.getId()),
                likedProductIds.contains(p.getId()),
                scrappedProductIds.contains(p.getId())
            ))
            .toList();

        return new org.springframework.data.domain.PageImpl<>(content, pageable, total);
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
