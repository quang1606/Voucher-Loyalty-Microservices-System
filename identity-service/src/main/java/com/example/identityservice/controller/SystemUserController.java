package com.example.identityservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.identityservice.dto.request.*;
import com.example.identityservice.dto.response.*;
import com.example.identityservice.service.SystemUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/system-users")
@RequiredArgsConstructor
public class SystemUserController {

    private final SystemUserService systemUserService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<SystemUserResponse>>> getAllUsers() {
        return ResponseEntity.ok(BaseResponse.<List<SystemUserResponse>>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(systemUserService.getAllUsers())
                .build());
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CreateUserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<CreateUserResponse>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message("Tạo user thành công")
                .data(systemUserService.createUser(request))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        systemUserService.updateUser(id, request);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message("Cập nhật user thành công")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable UUID id) {
        systemUserService.deleteUser(id);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message("Xóa user thành công")
                .build());
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<BaseResponse<Void>> resetPassword(@PathVariable UUID id, @Valid @RequestBody ResetPasswordRequest request) {
        systemUserService.resetPassword(id, request);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message("Reset mật khẩu thành công")
                .build());
    }
}
