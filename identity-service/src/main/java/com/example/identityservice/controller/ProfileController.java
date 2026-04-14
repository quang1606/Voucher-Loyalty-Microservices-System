package com.example.identityservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.identityservice.dto.request.ChangePasswordRequest;
import com.example.identityservice.dto.request.UpdateProfileRequest;
import com.example.identityservice.dto.response.ProfileResponse;
import com.example.identityservice.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<BaseResponse<ProfileResponse>> getProfile() {
        return ResponseEntity.ok(BaseResponse.<ProfileResponse>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(profileService.getProfile())
                .build());
    }

    @PutMapping
    public ResponseEntity<BaseResponse<Void>> updateProfile(@RequestBody UpdateProfileRequest request) {
        profileService.updateProfile(request);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .build());
    }

    @PutMapping("/password")
    public ResponseEntity<BaseResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        profileService.changePassword(request);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .build());
    }
}
