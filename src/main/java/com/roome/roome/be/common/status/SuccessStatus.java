package com.roome.roome.be.common.status;

import com.roome.roome.be.common.base.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SuccessStatus implements BaseStatus {

    SUCCESS_200("ROOME_200", HttpStatus.OK, "성공입니다."),
    SUCCESS_201("ROOME_201", HttpStatus.CREATED, "성공입니다."),
    SUCCESS_204("ROOME_204", HttpStatus.NO_CONTENT, "성공입니다."),

    /**
     * Auth
     */
    AUTH_URL_SUCCESS("AUTH_200", HttpStatus.OK, "로그인 URL 조회 성공"),
    LOGIN_SUCCESS("AUTH_200", HttpStatus.OK, "로그인 성공"),
    LOGOUT_SUCCESS("AUTH_200", HttpStatus.OK, "로그아웃 성공"),
    DELETE_USER_SUCCESS("AUTH_200", HttpStatus.OK, "회원탈퇴 성공"),
    CHECK_ID_SUCCESS("AUTH_200", HttpStatus.OK, "아이디 중복 확인 가능"),
    FIND_ID_SUCCESS("AUTH_200", HttpStatus.OK, "아이디 찾기 성공"),
    UPDATE_PASSWORD_SUCCESS("AUTH_200", HttpStatus.OK, "비밀번호 변경 성공"),
    SEND_EMAIL_VERIFICATION_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 인증 코드 발송 성공"),
    CONFIRM_EMAIL_VERIFICATION_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 인증 성공"),
    CREATE_TOKEN_SUCCESS("AUTH_200", HttpStatus.OK, "토큰 재발급 성공"),
    CREATE_USER_SUCCESS("AUTH_201", HttpStatus.CREATED, "회원가입 성공");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

}
