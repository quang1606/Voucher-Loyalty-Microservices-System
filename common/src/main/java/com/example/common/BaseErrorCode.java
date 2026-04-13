package com.example.common;


import lombok.Getter;

@Getter
public enum BaseErrorCode {

    ERROR("error", "General error", 1),
    SUCCESS("success", "Success", 0);

    private final String errorCode;
    private final String errorDescription;
    private final Integer errorNumCode;

    BaseErrorCode(String errorCode, String errorDescription, Integer errorNumCode) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.errorNumCode = errorNumCode;
    }
}
