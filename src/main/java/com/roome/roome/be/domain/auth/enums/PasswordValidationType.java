package com.roome.roome.be.domain.auth.enums;

public enum PasswordValidationType {
    LOGIN,      // 로그인 시 비밀번호 확인
    UPDATE,      // 비밀번호 변경 시 비밀번호 확인
    OTHER        // 회원탈퇴 등 기타
}
