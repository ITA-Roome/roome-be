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
    CREATE_USER_SUCCESS("AUTH_201", HttpStatus.CREATED, "회원가입 성공"),
    DELETE_USER_SUCCESS("AUTH_200", HttpStatus.OK, "회원탈퇴 성공"),
    CHECK_ID_SUCCESS("AUTH_200", HttpStatus.OK, "아이디 중복 확인 성공"),
    CHECK_NICKNAME_SUCCESS("AUTH_200", HttpStatus.OK, "닉네임 중복 확인 성공"),
    UPDATE_PASSWORD_SUCCESS("AUTH_200", HttpStatus.OK, "비밀번호 변경 성공"),
    FIND_EMAIL_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 찾기 성공"),
    CHECK_EMAIL_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 중복 확인 성공"),
    SEND_EMAIL_VERIFICATION_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 인증 코드 발송 성공"),
    CONFIRM_EMAIL_VERIFICATION_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 인증 성공"),
    CREATE_TOKEN_SUCCESS("AUTH_200", HttpStatus.OK, "토큰 재발급 성공"),

    /**
     * User
     */
    SAVE_USER_ONBOARDING("AUTH_201", HttpStatus.CREATED, "유저 온보딩 저장 성공"),

    /**
     * Shop
     */
    REGISTER_SHOP_SUCCESS("SHOP_201", HttpStatus.CREATED, "가게 등록 성공"),
    UPDATE_SHOP_SUCCESS("SHOP_200", HttpStatus.OK, "가게 수정 성공"),
    DELETE_SHOP_SUCCESS("SHOP_200", HttpStatus.OK, "가게 삭제 성공"),
    GET_SHOP_DETAIL_SUCCESS("SHOP_200",HttpStatus.OK ,"가게 상세 조회 성공" ),
    GET_SHOP_LIST_SUCCESS("SHOP_200",HttpStatus.OK ,"가게 목록 조회 성공" ),

    /**
     * S3
     */
    S3_PRESIGNED_ISSUE_SUCCESS("S3_200", HttpStatus.OK, "Presigned URL 발급 성공"),

    /**
     * Product
     */
    PRODUCT_REGISTER_SUCCESS("PRODUCT_201", HttpStatus.CREATED, "상품 등록 성공");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

}
