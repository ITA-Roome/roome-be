package com.roome.roome.be.domain.user.dto.response;

import java.time.LocalDate;

public record UserProfileResponse(
        Long userId,
        String profileImage,
        String nickname,
        LocalDate signUpDate,
        String phoneNumber,
        String email
) {
}
