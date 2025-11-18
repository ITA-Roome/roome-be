package com.roome.roome.be.domain.user.service;

import com.roome.roome.be.domain.product.dto.response.CommonProductInfo;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.user.dto.response.UserRecentViewedProductListResponse;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserView;
import com.roome.roome.be.domain.user.repository.UserViewCustomRepository;
import com.roome.roome.be.domain.user.repository.UserViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserViewService {

    private final UserViewRepository userViewRepository;
    private final UserViewCustomRepository userViewCustomRepository;

    private final UserService userService;


    // 유저가 최근 본 상품 리스트 조회
    public UserRecentViewedProductListResponse getUserRecentViewedProductList(Long userId) {
        List<CommonProductInfo> userRecentViewedProductList = userViewCustomRepository.findUserRecentViewedProductListByUserId(userId);
        return new UserRecentViewedProductListResponse(userRecentViewedProductList);
    }

    // User View 등록
    public void registerUserView(Product product, Long userId) {
        User user = userService.getUserById(userId);
        UserView userView = userViewRepository.findByUserAndProduct(user, product);

        if (userView == null) {
            userView = UserView.builder()
                    .user(user)
                    .product(product)
                    .build();
            userViewRepository.save(userView);
        } else {
            userView.touch();
        }
    }
}
