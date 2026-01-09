package com.roome.roome.be.domain.user.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.s3.enums.StorageScope;
import com.roome.roome.be.common.s3.service.ImageUrlBuilder;
import com.roome.roome.be.common.s3.service.S3Service;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.auth.dto.response.CheckEmailResponse;
import com.roome.roome.be.domain.auth.dto.response.CheckNicknameResponse;
import com.roome.roome.be.domain.auth.dto.request.SignUpRequest;
import com.roome.roome.be.domain.user.dto.request.UpdateUserProfileRequest;
import com.roome.roome.be.domain.user.dto.response.*;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.enums.LoginType;
import com.roome.roome.be.domain.user.enums.Role;
import com.roome.roome.be.domain.user.repository.*;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final S3Service s3Service;
    private final ImageUrlBuilder imageUrlBuilder;

    public UserProfileResponse getUserProfile(Long userId) {
        User user = getUserById(userId);
        return new UserProfileResponse(
                user.getId(),
                user.getProfileImage(),
                user.getNickname(),
                user.getCreatedAt().toLocalDate(),
                user.getPhoneNumber(),
                user.getEmail());
    }

    @Transactional
    public void updateUserProfile(Long userId, UpdateUserProfileRequest request){
        User user = getUserById(userId);

        if(request.nickname() != null && !request.nickname().isBlank())
            user.updateNickname(request.nickname());

        MultipartFile file = request.profileImage();
        if(file != null&& !file.isEmpty()){
            String objectKey = s3Service.uploadObject(StorageScope.USER_PROFILE,userId,file);
            user.updateProfileImage(imageUrlBuilder.build(objectKey));
        }
    }

    public void getUserUploadReferenceList(Long userId) {
        User user = getUserById(userId);
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
    public CheckEmailResponse checkEmail(String email) {
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
                .isDeleted(false)
                .providerId(null)
                .build();
        userRepository.save(user);
    }

    // 이메일과 isDeleted로 유저 찾기
    public User findByEmailAndIsDeleted(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    // 이메일로 유저 찾기
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMAIL_NOT_FOUND_2));
    }

    // 아이디로 유저 찾기
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    // 전화번호로 유저 찾기
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findUserByPhoneNumber(phoneNumber);
    }

    // RefreshToken으로 User 검색
    public User getUserByRefreshToken(Claims claims, String refreshToken) {
        String id = claims.getSubject();

        User user = userRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_MISMATCH);
        }
        return user;
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

    // 관리자 검사
    public User validateAdmin(Long userId) {
        User user = getUserById(userId);
        if (user.getRole() != Role.ADMIN) {
            throw new GeneralException(ErrorStatus.NOT_ADMIN_ERROR);
        }
        return user;
    }

}
