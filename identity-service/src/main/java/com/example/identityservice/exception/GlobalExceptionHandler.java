package com.example.identityservice.exception;

import com.example.common.BaseErrorCode;
import com.example.common.BaseException;
import com.example.common.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Void>> handleBaseException(BaseException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(
                BaseResponse.<Void>builder()
                        .code(BaseErrorCode.ERROR.getErrorCode())
                        .status(BaseErrorCode.ERROR.getErrorNumCode())
                        .message(ex.getDescription())
                        .build()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
                BaseResponse.<Void>builder()
                        .code(BaseErrorCode.ERROR.getErrorCode())
                        .status(BaseErrorCode.ERROR.getErrorNumCode())
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                BaseResponse.<Void>builder()
                        .code(BaseErrorCode.ERROR.getErrorCode())
                        .status(BaseErrorCode.ERROR.getErrorNumCode())
                        .message(ex.getMessage())
                        .build()
        );
    }
}
