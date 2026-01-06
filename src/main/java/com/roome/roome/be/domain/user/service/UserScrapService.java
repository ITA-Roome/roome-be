package com.roome.roome.be.domain.user.service;

import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.domain.product.dto.response.CommonProductInfo;
import com.roome.roome.be.domain.product.dto.response.ProductToggleScrapResponse;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.service.ProductService;
import com.roome.roome.be.domain.reference.dto.response.CommonReferenceInfo;
import com.roome.roome.be.domain.reference.dto.response.ReferenceToggleScrapResponse;
import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.reference.service.ReferenceService;
import com.roome.roome.be.domain.user.dto.response.UserScrappedProductListResponse;
import com.roome.roome.be.domain.user.dto.response.UserScrappedReferenceListResponse;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserScrapProduct;
import com.roome.roome.be.domain.user.entity.UserScrapReference;
import com.roome.roome.be.domain.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserScrapService {

    private final UserScrapProductRepository userScrapProductRepository;
    private final UserScrapReferenceRepository userScrapReferenceRepository;

    private final UserScrapReferenceCustomRepository scrapReferenceCustomRepository;
    private final UserScrapProductCustomRepository userScrapProductCustomRepository;

    private final UserLikeReferenceRepository userLikeReferenceRepository;

    private final UserService userService;
    private final ProductService productService;
    private final ReferenceService referenceService;

    private final ImageUrlBuilder imageUrlBuilder;
    private final UserLikeProductRepository userLikeProductRepository;

    // 상품 스크랩 or 스크랩 취소 기능 구현
    @Transactional
    public ProductToggleScrapResponse toggleProductScrap(Long productId, Long userId) {
        User user = userService.getUserById(userId);
        Product product = productService.getProductById(productId);

        boolean scrapped;

        Optional<UserScrapProduct> existing = userScrapProductRepository.findByUserAndProduct(user, product);
        if (existing.isEmpty()) {
            UserScrapProduct userScrapProduct = UserScrapProduct.builder()
                    .user(user)
                    .product(product)
                    .build();
            userScrapProductRepository.save(userScrapProduct);
            scrapped = true;
        }
        else {
            userScrapProductRepository.delete(existing.get());
            scrapped = false;
        }

        return new ProductToggleScrapResponse(scrapped);
    }

    public ReferenceToggleScrapResponse toggleReferenceScrap(Long referenceId, Long userId) {
        User user = userService.getUserById(userId);
        Reference reference = referenceService.findReferenceById(referenceId);

        boolean scrapped;

        Optional<UserScrapReference> existing = userScrapReferenceRepository.findByUserAndReference(user,reference);
        if (existing.isEmpty()) {
            UserScrapReference userScrapReference = UserScrapReference.builder()
                    .user(user)
                    .reference(reference)
                    .build();
            userScrapReferenceRepository.save(userScrapReference);
            scrapped = true;
        }
        else {
            userScrapReferenceRepository.delete(existing.get());
            scrapped = false;
        }

        return new ReferenceToggleScrapResponse(scrapped);
    }

    // 유저가 스크랩한 상품 리스트 조회
    public UserScrappedProductListResponse getUserScrappedProductList(Long userId) {
        List<CommonProductInfo> rawList = userScrapProductCustomRepository.findUserScrappedProductListByUserId(userId);

        List<Long> productIds = rawList.stream()
                .map(CommonProductInfo::id)
                .toList();

        Set<Long> likeIds = new HashSet<>();
        if(!productIds.isEmpty()) {
            likeIds = userLikeProductRepository.findLikedProductIds(userId, productIds);
        }

        final Set<Long> finalLikeIds = likeIds;
        List<CommonProductInfo> finalList = rawList.stream()
                .map(raw -> new CommonProductInfo(
                        raw.id(),
                        raw.name(),
                        raw.category(),
                        raw.price(),
                        raw.description(),
                        raw.productUrl(),
                        raw.thumbnailKey(),
                        raw.imageList(),
                        raw.tagList(),
                        raw.likeCount(),
                        raw.scrapCount(),
                        finalLikeIds.contains(raw.id()),
                        raw.isScrapped(),
                        raw.createdAt(),
                        raw.updatedAt()
                ))
                .toList();
        return new UserScrappedProductListResponse(finalList);
    }

    // 유저가 스크랩한 레퍼런스 리스트 조회
    public UserScrappedReferenceListResponse getUserScrappedReferenceList(Long userId) {

        List<CommonReferenceInfo> rawList = scrapReferenceCustomRepository
                .findUserScrappedReferenceListByUserId(userId);

        List<Long> referenceIds = rawList.stream()
                .map(CommonReferenceInfo::referenceId)
                .toList();

        Set<Long> likedIds = new HashSet<>();
        if (!referenceIds.isEmpty()) {
            likedIds = userLikeReferenceRepository.findLikedReferenceIds(userId, referenceIds);
        }

        final Set<Long> finalLikedIds = likedIds;

        List<CommonReferenceInfo> finalList = rawList.stream()
                .map(raw -> new CommonReferenceInfo(
                        raw.referenceId(),
                        raw.nickname(),
                        raw.userId(),
                        raw.imageUrlList().stream()
                                .map(imageUrlBuilder::build)
                                .toList(),
                        raw.scrapCount(),
                        raw.likeCount(),
                        true,
                        finalLikedIds.contains(raw.referenceId())
                ))
                .toList();

        return new UserScrappedReferenceListResponse(finalList);
    }
}
