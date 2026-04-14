package com.example.common;


import lombok.Getter;

@Getter
public enum BaseErrorCode {

    ERROR("error", "General error", 1),
    SUCCESS("success", "Success", 0),
    VALIDATION_ERROR("VALIDATION_ERROR", "Validation failed", 1001),
    UNAUTHORIZED("UNAUTHORIZED", "Unauthorized", 1002),
    NOT_FOUND("NOT_FOUND", "Resource not found", 1003),
    CONFLICT("CONFLICT", "Resource already exists", 1004),
    BAD_REQUEST("BAD_REQUEST", "Bad request", 1005),
    INTERNAL_ERROR("INTERNAL_ERROR", "Internal server error", 1006);

    private final String errorCode;
    private final String errorDescription;
    private final Integer errorNumCode;

    BaseErrorCode(String errorCode, String errorDescription, Integer errorNumCode) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.errorNumCode = errorNumCode;
    }
}
