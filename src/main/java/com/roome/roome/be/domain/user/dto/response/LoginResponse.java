package com.roome.roome.be.domain.user.dto.response;

import com.roome.roome.be.domain.user.enums.LoginType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserInfo userInfo;

    @Getter
    @Builder
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private String email;
        private LoginType loginType;
    }
}
