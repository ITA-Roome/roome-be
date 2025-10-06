package com.roome.roome.be.common.status;

import com.roome.roome.be.common.base.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseStatus {

    // 예시
    ERROR_STATUS("ROOME_400", HttpStatus.BAD_REQUEST, "Bad Request"),

    KAKAO_TOKEN_FAILED("ERROR_400", HttpStatus.BAD_REQUEST, "카카오 토큰 발급에 실패했습니다"),
    KAKAO_USER_INFO_FAILED("ERROR_400", HttpStatus.BAD_REQUEST, "카카오 사용자 정보 조회에 실패했습니다"),
    EMAIL_AGREEMENT_REQUIRED("ERROR_400", HttpStatus.BAD_REQUEST, "카카오 이메일 동의가 필요합니다"),
    OAUTH_LOGIN_FAILED("ERROR_500", HttpStatus.INTERNAL_SERVER_ERROR, "소셜 로그인 처리 중 오류가 발생했습니다");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
