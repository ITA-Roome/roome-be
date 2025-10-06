package com.roome.roome.be.domain.user.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
public class KakaoInfoDto {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        private Profile profile;
        private String email;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Getter
        @NoArgsConstructor
        public static class Profile {
            private String nickname;
        }
    }
}