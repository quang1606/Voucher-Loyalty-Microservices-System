package com.example.customerservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.customerservice.dto.response.CustomerProfileResponse;
import com.example.customerservice.service.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers/profile")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    @GetMapping("/{customerId}")
    public ResponseEntity<BaseResponse<CustomerProfileResponse>> getProfile(@PathVariable UUID customerId) {
        return ResponseEntity.ok(BaseResponse.<CustomerProfileResponse>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(customerProfileService.getProfile(customerId))
                .build());
    }
}
