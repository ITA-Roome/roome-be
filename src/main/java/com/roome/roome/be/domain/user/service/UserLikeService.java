package com.roome.roome.be.domain.user.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roome.roome.be.common.s3.service.ImageUrlBuilder; // [추가]
import com.roome.roome.be.domain.product.dto.response.CommonProductInfo;
import com.roome.roome.be.domain.product.dto.response.ProductToggleLikeResponse;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.service.ProductService;
import com.roome.roome.be.domain.reference.dto.response.CommonReferenceInfo;
import com.roome.roome.be.domain.reference.dto.response.ReferenceToggleLikeResponse;
import com.roome.roome.be.domain.reference.entity.Reference;
import com.roome.roome.be.domain.reference.service.ReferenceService;
import com.roome.roome.be.domain.user.dto.response.UserLikeProductListResponse;
import com.roome.roome.be.domain.user.dto.response.UserLikeReferenceListResponse;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserLikeProduct;
import com.roome.roome.be.domain.user.entity.UserLikeReference;
import com.roome.roome.be.domain.user.repository.UserLikeProductCustomRepository;
import com.roome.roome.be.domain.user.repository.UserLikeProductRepository;
import com.roome.roome.be.domain.user.repository.UserLikeReferenceCustomRepository;
import com.roome.roome.be.domain.user.repository.UserLikeReferenceRepository;
import com.roome.roome.be.domain.user.repository.UserScrapReferenceRepository; // [추가]

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserLikeService {

    private final UserLikeProductRepository userLikeProductRepository;
    private final UserLikeProductCustomRepository userLikeProductCustomRepository;
    private final UserLikeReferenceRepository userLikeReferenceRepository;
    private final UserLikeReferenceCustomRepository userLikeReferenceCustomRepository;

    private final UserScrapReferenceRepository userScrapReferenceRepository;

    private final UserService userService;
    private final ProductService productService;
    private final ReferenceService referenceService;

    private final ImageUrlBuilder imageUrlBuilder;

    @Transactional
    public ProductToggleLikeResponse toggleProductLike(Long productId, Long userId) {
        Product product = productService.getProductById(productId);
        User user = userService.getUserById(userId);

        boolean liked;

        Optional<UserLikeProduct> existing = userLikeProductRepository.findByUserAndProduct(user, product);
        if (existing.isEmpty()) {
            UserLikeProduct userLikeProduct = UserLikeProduct.builder()
                    .user(user)
                    .product(product)
                    .build();
            userLikeProductRepository.save(userLikeProduct);
            liked = true;
            product.incrementLikeCount();
        }
        else {
            userLikeProductRepository.delete(existing.get());
            liked = false;
            product.decrementLikeCount();
        }

        return new ProductToggleLikeResponse(liked);
    }

    // 유저가 좋아요를 누른 상품 리스트 조회
    public UserLikeProductListResponse getUserLikedProductList(Long userId) {
        List<CommonProductInfo> userLikeProductList = userLikeProductCustomRepository.findUserLikeProductListByUserId(userId);
        return new UserLikeProductListResponse(userLikeProductList);
    }

    // 레퍼런스 좋아요 or 좋아요 취소 기능 구현
    @Transactional
    public ReferenceToggleLikeResponse toggleReferenceLike(Long referenceId, Long userId) {
        Reference reference = referenceService.findReferenceById(referenceId);
        User user = userService.getUserById(userId);

        boolean liked;

        Optional<UserLikeReference> existing = userLikeReferenceRepository.findByUserAndReference(user, reference);
        if (existing.isEmpty()) {
            UserLikeReference userLikeReference = UserLikeReference.builder()
                    .user(user)
                    .reference(reference)
                    .build();
            userLikeReferenceRepository.save(userLikeReference);
            liked = true;
            reference.incrementLikeCount();
        }
        else {
            userLikeReferenceRepository.delete(existing.get());
            liked = false;
            reference.decrementLikeCount();
        }

        return new ReferenceToggleLikeResponse(liked);
    }

    //  유저가 좋아요를 누른 레퍼런스 리스트 조회
    public UserLikeReferenceListResponse getUserLikedReferenceList(Long userId) {
        List<CommonReferenceInfo> rawList = userLikeReferenceCustomRepository.findUserLikeReferenceListByUserId(userId);

        List<Long> referenceIds = rawList.stream()
                .map(CommonReferenceInfo::referenceId)
                .toList();

        Set<Long> scrappedIds = new HashSet<>();
        if (!referenceIds.isEmpty()) {
            scrappedIds = userScrapReferenceRepository.findScrappedReferenceIds(userId, referenceIds);
        }

        final Set<Long> finalScrappedIds = scrappedIds;

        List<CommonReferenceInfo> finalList = rawList.stream()
                .map(raw -> new CommonReferenceInfo(
                        raw.referenceId(),
                        raw.nickname(),
                        raw.userId(),
                        raw.imageUrlList().stream()
                                .map(imageUrlBuilder::build) // URL 변환
                                .toList(),
                        raw.scrapCount(),
                        finalScrappedIds.contains(raw.referenceId()), // isScrapped: DB 조회 결과 반영
                        true
                ))
                .toList();

        return new UserLikeReferenceListResponse(finalList);
    }

}