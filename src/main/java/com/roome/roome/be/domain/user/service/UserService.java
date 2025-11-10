package com.roome.roome.be.domain.user.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.auth.dto.response.CheckEmailResponse;
import com.roome.roome.be.domain.auth.dto.response.CheckNicknameResponse;
import com.roome.roome.be.domain.auth.dto.request.SignUpRequest;
import com.roome.roome.be.domain.product.entity.Product;
import com.roome.roome.be.domain.user.dto.request.UserOnboardingRequest;
import com.roome.roome.be.domain.user.dto.response.UserLikeProduct;
import com.roome.roome.be.domain.user.dto.response.UserLikeProductListResponse;
import com.roome.roome.be.domain.user.dto.response.UserOnboardingExistResponse;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserOnboarding;
import com.roome.roome.be.domain.user.entity.UserView;
import com.roome.roome.be.domain.user.enums.LoginType;
import com.roome.roome.be.domain.user.enums.Role;
import com.roome.roome.be.domain.user.repository.UserLikeCustomRepositoryImpl;
import com.roome.roome.be.domain.user.repository.UserOnboardingRepository;
import com.roome.roome.be.domain.user.repository.UserRepository;
import com.roome.roome.be.domain.user.repository.UserViewRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserOnboardingRepository userOnboardingRepository;
    private final UserViewRepository userViewRepository;
    private final UserLikeCustomRepositoryImpl userLikeCustomRepositoryImpl;

    // 유저 온보딩 저장
    @Transactional
    public void saveUserOnboarding(Long userId, UserOnboardingRequest userOnboardingRequest){
        User user = findUserById(userId);
        userOnboardingRepository.findByUser(user)
                .ifPresentOrElse(
                        UserOnboarding -> updateUserOnboarding(UserOnboarding, userOnboardingRequest),
                        () -> registerUserOnboarding(user,userOnboardingRequest)
                );
    }

    // 유저 온보딩 업데이트
    private void updateUserOnboarding(UserOnboarding userOnboarding, UserOnboardingRequest userOnboardingRequest){
        userOnboarding.update(
                userOnboardingRequest.ageGroup(),
                userOnboardingRequest.gender(),
                userOnboardingRequest.moodType(),
                userOnboardingRequest.spaceType()
        );
    }

    // 유저 온보딩 저장
    private void registerUserOnboarding(User user, UserOnboardingRequest userOnboardingRequest) {
        UserOnboarding newOnboarding = UserOnboarding.builder()
                .user(user)
                .ageGroup(userOnboardingRequest.ageGroup())
                .gender(userOnboardingRequest.gender())
                .moodType(userOnboardingRequest.moodType())
                .spaceType(userOnboardingRequest.spaceType())
                .build();

        userOnboardingRepository.save(newOnboarding);
    }

    //유저온보딩 존재 여부
    public UserOnboardingExistResponse checkExistence(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        boolean exists = userOnboardingRepository.findByUser(user).isPresent();

        return new UserOnboardingExistResponse(exists);
    }

    // 유저가 좋아요를 누른 상품 리스트 조회
    public UserLikeProductListResponse getUserLikedProductList(Long userId) {
        List<UserLikeProduct> userLikeProductList = userLikeCustomRepositoryImpl.findUserLikeProductListByUserId(userId);
        return new UserLikeProductListResponse(userLikeProductList);
    }

    // 이메일 중복 검사
    public void checkEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new GeneralException(ErrorStatus.EMAIL_ALREADY_EXISTS);
        }
    }

    // 닉네임 중복 검사
    public CheckNicknameResponse checkNickname(String nickname) {
        boolean isExist = userRepository.existsByNickname(nickname);
        return new CheckNicknameResponse(isExist);
    }

    // 이메일 중복 검사
    public CheckEmailResponse checkEmail(String email){
        boolean isExist = userRepository.existsByEmail(email);
        return new CheckEmailResponse(isExist);
    }

    // 유저 등록
    public void registerUser(SignUpRequest request) {
        User user = User.builder()
                .password(request.password())
                .email(request.email())
                .nickname(request.nickname())
                .phoneNumber(request.phoneNumber())
                .loginType(LoginType.EMAIL)
                .role(Role.USER)
                .providerId(null)
                .build();
        userRepository.save(user);
    }

    // User View 등록
    public void registerUserView(Product product, User user ){
        UserView userView = userViewRepository.findByUserAndProduct(user,product);

        if(userView == null) {
            userView = UserView.builder()
                    .user(user)
                    .product(product)
                    .build();
            userViewRepository.save(userView);
        }
        else{
            userView.touch();
        }

    }

    // 이메일로 유저 찾기
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_FOUND_2));
    }

    // 아이디로 유저 찾기
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    // 전화번호로 유저 찾기
    public Optional<User> findUserByPhoneNumber(String phoneNumber) {
        return userRepository.findUserByPhoneNumber(phoneNumber);
    }

    // RefreshToken 업데이트
    public void updateRefreshToken(User user, String refreshToken) {
        user.updateRefreshToken(refreshToken);
    }

    // 회원 탈퇴
    public void withdrawUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // 로그아웃 시 RefreshToken 제거
    public void clearRefreshToken(User user) {
        user.clearRefreshToken();
    }

    // 비밀번호 수정
    public void updatePassword(User user, String encryptedPassword) {
        user.updatePassword(encryptedPassword);
    }

    // RefreshToken으로 User 검색
    public User findUserByRefreshToken(Claims claims, String refreshToken) {
        String id = claims.getSubject();

        User user = userRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_MISMATCH);
        }
        return user;
    }

}
