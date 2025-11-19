package com.roome.roome.be.domain.user.service;

import com.roome.roome.be.domain.user.dto.request.UserOnboardingRequest;
import com.roome.roome.be.domain.user.dto.response.UserOnboardingExistResponse;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.entity.UserOnboarding;
import com.roome.roome.be.domain.user.repository.UserOnboardingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserOnboardingService {

    private final UserOnboardingRepository userOnboardingRepository;
    private final UserService userService;

    // 유저 온보딩 저장
    @Transactional
    public void saveUserOnboarding(Long userId, UserOnboardingRequest userOnboardingRequest) {
        User user = userService.getUserById(userId);
        userOnboardingRepository.findByUser(user)
                .ifPresentOrElse(
                        UserOnboarding -> updateUserOnboarding(UserOnboarding, userOnboardingRequest),
                        () -> registerUserOnboarding(user, userOnboardingRequest)
                );
    }

    // 유저 온보딩 업데이트
    private void updateUserOnboarding(UserOnboarding userOnboarding, UserOnboardingRequest userOnboardingRequest) {
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
        User user = userService.getUserById(userId);
        boolean exists = userOnboardingRepository.findByUser(user).isPresent();

        return new UserOnboardingExistResponse(exists);
    }
}
