package com.roome.roome.be.domain.user.dto.request;

import com.roome.roome.be.domain.user.enums.*;
import jakarta.validation.constraints.NotNull;

public record UserOnboardingRequest(
        @NotNull(message = "나이대는 필수입니다.")
        AgeGroup ageGroup,

        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

        @NotNull(message = "분위기 타입은 필수입니다.")
        MoodType moodType,

        @NotNull(message = "공간 타입은 필수입니다.")
        SpaceType spaceType
) {
}
