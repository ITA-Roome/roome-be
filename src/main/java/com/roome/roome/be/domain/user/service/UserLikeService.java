package com.roome.roome.be.domain.user.service;

import com.roome.roome.be.domain.product.dto.response.CommonProductInfo;
import com.roome.roome.be.domain.product.dto.response.ProductToggleLikeResponse;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.product.service.ProductService;
import com.roome.roome.be.domain.user.dto.response.UserLikeProductListResponse;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserLikeProduct;
import com.roome.roome.be.domain.user.repository.UserLikeProductCustomRepository;
import com.roome.roome.be.domain.user.repository.UserLikeProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLikeService {

    private final UserLikeProductRepository userLikeRepository;
    private final UserLikeProductCustomRepository userLikeCustomRepository;

    private final UserService userService;
    private final ProductService productService;

    // 상품 좋아요 or 좋아요 취소 기능 구현
    @Transactional
    public ProductToggleLikeResponse toggleProductLike(Long productId, Long userId) {
        Product product = productService.getProductById(productId);
        User user = userService.getUserById(userId);

        boolean liked;

        Optional<UserLikeProduct> existing = userLikeRepository.findByUserAndProduct(user, product);
        if (existing.isEmpty()) {
            UserLikeProduct userLikeProduct = UserLikeProduct.builder()
                    .user(user)
                    .product(product)
                    .build();
            userLikeRepository.save(userLikeProduct);
            liked = true;
            product.incrementLikeCount();
        }
        else {
            userLikeRepository.delete(existing.get());
            liked = false;
            product.decrementLikeCount();
        }

        return new ProductToggleLikeResponse(liked);
    }

    // 유저가 좋아요를 누른 상품 리스트 조회
    public UserLikeProductListResponse getUserLikedProductList(Long userId) {
        List<CommonProductInfo> userLikeProductList = userLikeCustomRepository.findUserLikeProductListByUserId(userId);
        return new UserLikeProductListResponse(userLikeProductList);
    }

}
