package com.roome.roome.be.domain.auth.dto.response;

public record ReissueAccessTokenResponse(
        String accessToken,
        String refreshToken
) {
}
