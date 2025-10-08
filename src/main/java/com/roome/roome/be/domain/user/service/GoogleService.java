package com.roome.roome.be.domain.user.service;

import com.roome.roome.be.domain.user.dto.social.GoogleInfoDto;
import com.roome.roome.be.domain.user.dto.social.GoogleTokenResponse;
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
public class GoogleService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${social.google.client-id}")
    private String clientId;

    @Value("${social.google.client-secret}")
    private String clientSecret;

    @Value("${social.google.redirect-uri}")
    private String redirectUri;

    // 구글 로그인 처리
    @Transactional
    public User loginWithGoogle(String code) {
        GoogleTokenResponse token = getGoogleToken(code);
        GoogleInfoDto userInfo = getGoogleUserInfo(token.getAccessToken());
        return findOrCreateUser(userInfo);
    }

    // 구글 인증 URL 생성
    public String getGoogleAuthorizeUri() {
        return UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid email profile")
                .build()
                .toUriString();
    }

    // 인가 코드로 구글 액세스 토큰 발급
    private GoogleTokenResponse getGoogleToken(String code) {
        String url = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<GoogleTokenResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, request, GoogleTokenResponse.class);

        return response.getBody();
    }

    // 액세스 토큰으로 구글 사용자 정보 조회
    private GoogleInfoDto getGoogleUserInfo(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<GoogleInfoDto> response =
                restTemplate.exchange(url, HttpMethod.GET, request, GoogleInfoDto.class);

        return response.getBody();
    }

    // 구글 정보로 회원 조회 또는 생성
    private User findOrCreateUser(GoogleInfoDto googleInfo) {
        String providerId = googleInfo.getId();

        return userRepository.findByLoginTypeAndProviderId(LoginType.GOOGLE, providerId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .providerId(providerId)
                                .nickname(googleInfo.getName())
                                .email(googleInfo.getEmail())
                                .loginType(LoginType.GOOGLE)
                                .build()
                ));
    }
}