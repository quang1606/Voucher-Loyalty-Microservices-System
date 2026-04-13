package com.example.identityservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.identityservice.dto.request.AllowedPagesRequest;
import com.example.identityservice.dto.request.LoginRequest;
import com.example.identityservice.dto.request.RefreshTokenRequest;
import com.example.identityservice.dto.request.RegisterCustomerRequest;
import com.example.identityservice.dto.response.AllowedPagesResponse;
import com.example.identityservice.dto.response.CreateUserResponse;
import com.example.identityservice.dto.response.LoginResponse;
import com.example.identityservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(BaseResponse.<LoginResponse>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(authService.login(request))
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<CreateUserResponse>> register(@RequestBody RegisterCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<CreateUserResponse>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message("Đăng ký thành công")
                .data(authService.registerCustomer(request))
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<LoginResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(BaseResponse.<LoginResponse>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(authService.refreshToken(request))
                .build());
    }

    @PostMapping("/allowed-pages")
    public ResponseEntity<BaseResponse<AllowedPagesResponse>> getAllowedPages(@RequestBody AllowedPagesRequest request) {
        return ResponseEntity.ok(BaseResponse.<AllowedPagesResponse>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(authService.getAllowedPages(request))
                .build());
    }
}
