package com.roome.roome.be.common.exception;

import com.roome.roome.be.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GeneralExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(GeneralException e) {
        return ApiResponse.error(e.getErrorStatus());
    }
}
