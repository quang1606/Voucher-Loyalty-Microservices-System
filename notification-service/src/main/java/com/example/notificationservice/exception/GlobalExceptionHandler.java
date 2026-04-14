package com.example.notificationservice.exception;

import com.example.common.BaseErrorCode;
import com.example.common.BaseException;
import com.example.common.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Void>> handleBaseException(BaseException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(
                BaseResponse.<Void>builder()
                        .code(ex.getErrorCode())
                        .status(ex.getHttpStatus().value())
                        .message(ex.getDescription())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(
                BaseResponse.<Void>builder()
                        .code(BaseErrorCode.VALIDATION_ERROR.getErrorCode())
                        .status(BaseErrorCode.VALIDATION_ERROR.getErrorNumCode())
                        .message(message)
                        .build()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
                BaseResponse.<Void>builder()
                        .code(BaseErrorCode.BAD_REQUEST.getErrorCode())
                        .status(BaseErrorCode.BAD_REQUEST.getErrorNumCode())
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                BaseResponse.<Void>builder()
                        .code(BaseErrorCode.INTERNAL_ERROR.getErrorCode())
                        .status(BaseErrorCode.INTERNAL_ERROR.getErrorNumCode())
                        .message(ex.getMessage())
                        .build()
        );
    }
}
