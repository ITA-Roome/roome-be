package com.roome.roome.be.domain.user.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record UpdateUserProfileRequest(
        MultipartFile profileImage,
        String nickname
) {
}
