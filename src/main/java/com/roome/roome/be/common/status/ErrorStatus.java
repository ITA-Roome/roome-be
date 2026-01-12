package com.roome.roome.be.common.status;

import com.roome.roome.be.common.base.BaseStatus;
import com.roome.roome.be.domain.chat.enums.ChatMode;
import com.roome.roome.be.domain.chat.model.ChatSession;
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
    EMAIL_NOT_FOUND_1("AUTH_404", HttpStatus.NOT_FOUND, "인증 요청한 이메일이 아닙니다."),
    EMAIL_NOT_FOUND_2("AUTH_404", HttpStatus.NOT_FOUND, "존재하지 않는 유저 이메일입니다"),
    USER_NOT_FOUND("AUTH_404", HttpStatus.NOT_FOUND, "존재하지 않는 유저 이메일입니다."),
    ID_ALREADY_EXISTS("AUTH_409", HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    EMAIL_ALREADY_EXISTS("AUTH_409", HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    SEND_VERIFICATION_CODE_EMAIL_INTERNAL_SERVER_ERROR("500", HttpStatus.INTERNAL_SERVER_ERROR, "이메일 인증 코드 발송 중 오류가 발생했습니다."),
    NOT_ADMIN_ERROR("AUTH_403",HttpStatus.FORBIDDEN,"관리자가 아닙니다."),

    /**
     * JWT
     */
    JWT_TOKEN_NOT_FOUND("JWT_401", HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
    JWT_INVALID_SIGNATURE("JWT_401", HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다."),
    JWT_MALFORMED("JWT_401", HttpStatus.UNAUTHORIZED, "잘못된 JWT 형식입니다."),
    JWT_EXPIRED("JWT_401", HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    JWT_UNSUPPORTED("JWT_401", HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
    JWT_INVALID("JWT_401", HttpStatus.UNAUTHORIZED, "JWT 토큰이 잘못되었습니다."),
    JWT_EXTRACT_ID_FAILED("JWT_401", HttpStatus.UNAUTHORIZED, "토큰에서 사용자 정보를 추출할 수 없습니다."),
    JWT_GENERAL_ERROR("JWT_401", HttpStatus.UNAUTHORIZED, "JWT 토큰 처리 중 알 수 없는 오류가 발생했습니다."),
    JWT_INVALID_TYPE("JWT_401", HttpStatus.UNAUTHORIZED, "토큰 타입이 유효하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND("JWT_401", HttpStatus.UNAUTHORIZED, "DB에 저장된 토큰과 일치하지 않습니다."),
    REFRESH_TOKEN_MISMATCH("JWT_401", HttpStatus.UNAUTHORIZED, "리프레시 토큰 정보가 사용자 정보와 일치하지 않습니다."),
    JWT_EXTRACT_ROLE_FAILED("JWT_401", HttpStatus.UNAUTHORIZED, "토큰에서 사용자 Role을 추출할 수 없습니다."),

    /**
     * Shop
     */
    SHOP_NOT_FOUND("SHOP_404", HttpStatus.NOT_FOUND, "존재하지 않는 가게입니다."),

    /**
     * Inquiry
     */
    INQUIRY_NOT_FOUND("INQUIRY_404", HttpStatus.NOT_FOUND, "존재하지 않는 문의내역입니다."),
    INQUIRY_ANSWER_NOT_FOUND("INQUIRY_404", HttpStatus.NOT_FOUND, "존재하지 않는 문의 답변입니다."),
    INQUIRY_ANSWER_ALREADY_EXISTS("INQUIRY_409", HttpStatus.CONFLICT, "이미 답변이 작성되었습니다"),
    INQUIRY_NOT_ANSWERED("INQUIRY_409",HttpStatus.CONFLICT, "답변을 수정할 수 없는 상태입니다."),

    /**
     * image
     */
    INVALID_FILE_TYPE("S3-001", HttpStatus.BAD_REQUEST, "허용되지 않은 파일 형식입니다."),
    FILE_TOO_LARGE( "S3-002", HttpStatus.BAD_REQUEST,"파일 크기가 5MB를 초과했습니다."),
    S3_SERVER_ERROR("S3_500", HttpStatus.INTERNAL_SERVER_ERROR,"S3 업로드 중 오류가 발생하였습니다."),

    /**
     * Tag
     */
    TAG_NOT_FOUND("TAG_404", HttpStatus.NOT_FOUND, "존재하지 않는 태그입니다."),
    TAG_NAME_DUPLICATED("TAG_409", HttpStatus.CONFLICT, "이미 존재하는 태그 이름입니다."),

    /**
     * Product
     */
    PRODUCT_NOT_FOUND("PRODUCT_404", HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
    INVALID_IMAGE_ORDER("PRODUCT_400", HttpStatus.BAD_REQUEST,  "잘못된 이미지 순서입니다."),

    /**
     * Reference
     */
    REFERENCE_NOT_FOUND("REFERENCE_404", HttpStatus.NOT_FOUND, "존재하지 않는 레퍼런스입니다."),

    /**
     * AI
     * */
    AI_RESPONSE_NOT_JSON("AI_500",HttpStatus.INTERNAL_SERVER_ERROR, "AI 응답이 JSON 형식이 아님"),
    AI_RESPONSE_NOT_PARSE("AI_5OO", HttpStatus.INTERNAL_SERVER_ERROR, "AI 응답 파싱 실패"),

    /**
     * Comment
     */
    COMMENT_NOT_FOUND("COMMENT_404", HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),
    COMMENTABLE_ENTITY_NOT_FOUND("COMMENT_404", HttpStatus.NOT_FOUND, "댓글을 달 대상(게시글/상품 등)이 존재하지 않습니다."),
    COMMENT_AUTHOR_MISMATCH("COMMENT_403", HttpStatus.FORBIDDEN, "댓글 수정/삭제 권한이 없습니다."),
    COMMENT_CONTENT_TOO_LONG("COMMENT_400", HttpStatus.BAD_REQUEST, "댓글 내용은 최대 400자까지 입력 가능합니다."),

    /**
     * Board
     */
    SESSION_NOT_FOUND("SESSION_404", HttpStatus.NOT_FOUND, "유효하지 않은 세션입니다."),
    SESSION_USER_MISMATCH("SESSION_403", HttpStatus.FORBIDDEN, "본인의 세션 결과만 저장할 수 있습니다."),
    RESULT_NOT_FOUND("SESSION_404", HttpStatus.NOT_FOUND, "저장할 추천 결과가 없습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
