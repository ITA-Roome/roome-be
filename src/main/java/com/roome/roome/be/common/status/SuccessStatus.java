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
    SUCCESS_204("ROOME_204", HttpStatus.NO_CONTENT, "성공입니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

}
