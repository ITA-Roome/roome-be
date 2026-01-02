package com.roome.roome.be.domain.chat.enums;

public enum ChatIntentType {
    /** 사용자가 정보를 입력함 (예산, 분위기, 공간 등) */
    SET_INFO,

    /** 추천을 요청함 */
    REQUEST_RECOMMEND,

    /** 흐름 변경 (제품 ↔ 인테리어) */
    CHANGE_FLOW,

    /** 초기화 */
    RESET,

    /** 의미 불명 / 추가 질문 필요 */
    UNKNOWN
}
