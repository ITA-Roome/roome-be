package com.roome.roome.be.domain.auth.dto;

import com.roome.roome.be.domain.user.entity.User;

public record EmailLoginResponse(
        String accessToken,
        String refreshToken,
        String email,
        String nickname,
        Long userId
) {
    public static EmailLoginResponse from(
            String accessToken,
            String refreshToken,
            User user
    ) {
        return new EmailLoginResponse(
                accessToken,
                refreshToken,
                user.getEmail(),
                user.getNickname(),
                user.getId()
        );
    }
}
