package com.example.common;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class BaseException extends RuntimeException{
    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String description;

}
