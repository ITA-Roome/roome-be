package com.roome.roome.be.domain.user.service;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.auth.dto.SignUpRequest;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.enums.LoginType;
import com.roome.roome.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /** 이메일 중복 검사 */
    public void checkEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new GeneralException(ErrorStatus.EMAIL_ALREADY_EXISTS);
        }
    }

    /** 닉네임 중복 검사 */
    public void checkNicknameNotExists(String email) {
        if (userRepository.existsByNickname(email)) {
            throw new GeneralException(ErrorStatus.EMAIL_ALREADY_EXISTS);
        }
    }

    /** 유저 등록 */
    public void registerUser(SignUpRequest request) {
        User user = User.builder()
                .password(request.password())
                .email(request.email())
                .nickname(request.nickname())
                .loginType(LoginType.EMAIL)
                .providerId(null)
                .build();
        userRepository.save(user);
    }
}
