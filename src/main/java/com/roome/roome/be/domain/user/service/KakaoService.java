package com.roome.roome.be.domain.user.service;

import com.roome.roome.be.domain.user.dto.kakao.KakaoInfoDto;
import com.roome.roome.be.domain.user.dto.kakao.KakaoTokenResponse;
import com.roome.roome.be.domain.user.entity.User;
import com.roome.roome.be.domain.user.enums.LoginType;
import com.roome.roome.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class KakaoService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${social.kakao.client-id}")
    private String clientId;

    @Value("${social.kakao.client-secret}")
    private String clientSecret;

    @Value("${social.kakao.redirect-uri}")
    private String redirectUri;

    // 카카오 로그인 처리
    @Transactional
    public User loginWithKakao(String code) {
        KakaoTokenResponse token = getKakaoToken(code);
        KakaoInfoDto userInfo = getKakaoUserInfo(token.getAccessToken());
        return findOrCreateUser(userInfo);
    }

    // 카카오 인증 URL 생성
    public String getKakaoAuthorizeUri() {
        return UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "profile_nickname, account_email")
                .build()
                .toUriString();
    }

    // 인가 코드로 카카오 액세스 토큰 발급
    private KakaoTokenResponse getKakaoToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, request, KakaoTokenResponse.class);

        return response.getBody();
    }

    // 액세스 토큰으로 카카오 사용자 정보 조회
    private KakaoInfoDto getKakaoUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoInfoDto> response =
                restTemplate.exchange(url, HttpMethod.GET, request, KakaoInfoDto.class);

        return response.getBody();
    }

    // 카카오 정보로 회원 조회 또는 생성
    private User findOrCreateUser(KakaoInfoDto kakaoInfo) {
        String providerId = String.valueOf(kakaoInfo.getId());

        return userRepository.findByLoginTypeAndProviderId(LoginType.KAKAO, providerId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .providerId(providerId)
                                .nickname(kakaoInfo.getKakaoAccount().getProfile().getNickname())
                                .email(kakaoInfo.getKakaoAccount().getEmail())
                                .loginType(LoginType.KAKAO)
                                .build()
                ));
    }
}
