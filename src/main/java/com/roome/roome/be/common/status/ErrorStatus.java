package com.roome.roome.be.common.status;

import com.roome.roome.be.common.base.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseStatus {

    // 예시
    ERROR_STATUS("ROOME_400", HttpStatus.BAD_REQUEST, "Bad Request");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
