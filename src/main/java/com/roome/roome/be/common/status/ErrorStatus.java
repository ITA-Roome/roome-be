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

    /**
     * Common
     */
    BAD_REQUEST("COMM_400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED("COMM_401", HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN("COMM_403", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND("COMM_404", HttpStatus.NOT_FOUND, "요청한 자원을 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED("COMM_405", HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메소드입니다."),
    INTERNAL_SERVER_ERROR("COMM_500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),

    /**
     * Oauth2
     */
    KAKAO_TOKEN_FAILED("AUTH_400", HttpStatus.BAD_REQUEST, "카카오 토큰 발급에 실패했습니다"),
    KAKAO_USER_INFO_FAILED("AUTH_400", HttpStatus.BAD_REQUEST, "카카오 사용자 정보 조회에 실패했습니다"),
    EMAIL_AGREEMENT_REQUIRED("AUTH_400", HttpStatus.BAD_REQUEST, "카카오 이메일 동의가 필요합니다"),
    OAUTH_LOGIN_FAILED("AUTH_500", HttpStatus.INTERNAL_SERVER_ERROR, "소셜 로그인 처리 중 오류가 발생했습니다"),


    /**
     * Auth
     */
    INVALID_PASSWORD_FORMAT("AUTH_400", HttpStatus.BAD_REQUEST, "비밀번호 양식이 올바르지 않습니다."),
    EMAIL_NOT_VERIFIED("AUTH_400", HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),
    VERIFICATION_CODE_EXPIRED("AUTH_400", HttpStatus.BAD_REQUEST, "인증 코드가 만료되었습니다."),
    INVALID_VERIFICATION_CODE("AUTH_400", HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),
    PASSWORD_SAME_AS_OLD("AUTH_400", HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일합니다."),

    INVALID_PASSWORD("AUTH_401", HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),

    EMAIL_NOT_FOUND("AUTH_404", HttpStatus.NOT_FOUND, "인증 요청한 이메일이 아닙니다."),
    USER_NOT_FOUND("AUTH_404", HttpStatus.NOT_FOUND, "존재하지 않는 유저아이디입니다."),
    ID_NOT_FOUND("AUTH_404", HttpStatus.NOT_FOUND, "존재하지 않는 아이디입니다."),

    ID_ALREADY_EXISTS("AUTH_409", HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    EMAIL_ALREADY_EXISTS("AUTH_409", HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),

    SEND_VERIFICATION_CODE_EMAIL_INTERNAL_SERVER_ERROR("500", HttpStatus.INTERNAL_SERVER_ERROR, "이메일 인증 코드 발송 중 오류가 발생했습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
