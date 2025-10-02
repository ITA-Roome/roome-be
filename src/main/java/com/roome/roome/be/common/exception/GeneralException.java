package com.roome.roome.be.common.exception;

import com.roome.roome.be.common.base.BaseStatus;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {
    private final BaseStatus errorStatus;

    public GeneralException(BaseStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }
}
