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
     * Inquiry
     */
    GET_INQUIRY_LIST_SUCCESS("INQUIRY_200",HttpStatus.OK,"문의하기 전체 내역 조회 성공"),
    UPDATE_INQUIRY_ANSWER_SUCCESS("INQUIRY_200", HttpStatus.CREATED, "문의 답변 수정 성공"),
    REGISTER_INQUIRY_SUCCESS("INQUIRY_201", HttpStatus.CREATED, "문의 등록 성공"),
    REGISTER_INQUIRY_ANSWER_SUCCESS("INQUIRY_201", HttpStatus.CREATED, "문의 답변 등록 성공"),


    /**
     * User
     */
    SAVE_USER_ONBOARDING_SUCCESS("AUTH_201", HttpStatus.CREATED, "유저 온보딩 저장 성공"),
    CHECK_USER_ONBOARDING_EXISTENCE_SUCCESS("AUTH_200", HttpStatus.OK, "유저 온보딩 존재 여부 조회 성공"),
    GET_USER_LIKE_PRODUCT_LIST_SUCCESS("USER_200",HttpStatus.OK, "유저 좋아요 상품 리스트 조회 성공"),
    GET_USER_SCRAP_PRODUCT_LIST_SUCCESS("USER_200",HttpStatus.OK, "유저 스크랩 상품 리스트 조회 성공"),
    GET_USER_SCRAP_REFERENCE_LIST_SUCCESS("USER_200",HttpStatus.OK, "유저 스크랩 레퍼런스 리스트 조회 성공"),
    GET_USER_PROFILE_SUCCESS("USER_200",HttpStatus.OK,"유저 프로필 조회 성공"),
    UPDATE_USER_PROFILE_SUCCESS("USER_200",HttpStatus.OK,"유저 프로필 수정 성공"),

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
    S3_COMMIT_SUCCESS("S3_200",HttpStatus.OK,"S3 이미지 업로드 성공"),

    /**
     * Product
     */
    PRODUCT_REGISTER_SUCCESS("PRODUCT_201", HttpStatus.CREATED, "상품 등록 성공"),
    CREATE_PRODUCT_LIKE("PRODUCT_200", HttpStatus.OK, "상품 좋아요 토글 성공"),
    CREATE_PRODUCT_SCRAP("PRODUCT_200", HttpStatus.OK, "상품 스크랩 토글 성공"),
    GET_PRODUCT_DETAIL("PRODUCT_200", HttpStatus.OK, "상품 상세 조회 성공"),
    GET_PRODUCT_LIST("PRODUCT_200", HttpStatus.OK, "상품 목록 조회 성공"),
    UPDATE_PRODUCT_SUCCESS("PRODUCT_200", HttpStatus.OK,"상품 수정 성공"),
    UPDATE_PRODUCT_IMAGES_SUCCESS("PRODUCT_200", HttpStatus.OK,"상품 이미지 수정 성공"),
    DELETE_PRODUCT_SUCCESS("PRODUCT_200", HttpStatus.OK, "상품 삭제 성공"),

    /**
     * Reference
     */
    REGISTER_REFERENCE_SUCCESS("REFERENCE_201", HttpStatus.CREATED,"레퍼런스 등록 성공"),
    REGISTER_REFERENCE_IMAGE_SUCCESS("REFERENCE_201", HttpStatus.CREATED,"레퍼런스 이미지 등록 성공"),
    CREATE_REFERENCE_SCRAP("REFERENCE_200", HttpStatus.OK, "레퍼런스 스크랩 토글 성공"),
    GET_REFERENCE_LIST_SUCCESS("REFERENCE_200",HttpStatus.OK,"레퍼런스 리스트 조회 성공"),

    /**
     * Comment
     */
    CREATE_COMMENT_SUCCESS("COMMENT_201", HttpStatus.CREATED, "댓글 생성 성공"),
    GET_COMMENT_LIST_SUCCESS("COMMENT_200", HttpStatus.OK, "댓글 목록 조회 성공"),
    UPDATE_COMMENT_SUCCESS("COMMENT_200", HttpStatus.OK, "댓글 수정 성공"),
    DELETE_COMMENT_SUCCESS("COMMENT_200", HttpStatus.OK, "댓글 삭제 성공"),
    TOGGLE_COMMENT_LIKE_SUCCESS("COMMENT_200", HttpStatus.OK, "댓글 좋아요 토글 성공"),

    /**
     * Search
     */
    GET_POPULAR_KEYWORDS_LIST_SUCCESS("SEARCH_200",HttpStatus.OK, "인기 검색어 조회 성공"),
    RECORD_SEARCH_KEYWORD_SUCCESS( "SEARCH_201", HttpStatus.OK,"검색어 기록 성공"),
    GET_RECENT_KEYWORDS_LIST_SUCCESS("SEARCH_200",HttpStatus.OK, "최근 검색어 조회 성공");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

}
