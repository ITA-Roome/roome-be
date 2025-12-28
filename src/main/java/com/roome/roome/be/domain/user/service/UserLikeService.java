package com.roome.roome.be.domain.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserLikeService {

    private final UserLikeProductRepository userLikeProductRepository;
    private final UserLikeProductCustomRepository userLikeProductCustomRepository;
    private final UserLikeReferenceRepository userLikeReferenceRepository;
    private final UserLikeReferenceCustomRepository userLikeReferenceCustomRepository;

    private final UserService userService;
    private final ProductService productService;
    private final ReferenceService referenceService;

    // 상품 좋아요 or 좋아요 취소 기능 구현
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

    // 유저가 좋아요를 누른 레퍼런스 리스트 조회
    public UserLikeReferenceListResponse getUserLikedReferenceList(Long userId) {
        List<CommonReferenceInfo> userLikeReferenceList = userLikeReferenceCustomRepository.findUserLikeReferenceListByUserId(userId);
        return new UserLikeReferenceListResponse(userLikeReferenceList);
    }

}
