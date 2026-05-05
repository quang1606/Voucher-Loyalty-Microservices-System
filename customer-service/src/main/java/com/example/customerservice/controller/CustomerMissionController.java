package com.example.customerservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.customerservice.dto.request.ClaimMissionRewardRequest;
import com.example.customerservice.dto.request.ClaimMissionRewardResponse;
import com.example.customerservice.dto.response.MissionResponse;
import com.example.customerservice.service.CustomerMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/missions")
@RequiredArgsConstructor
public class CustomerMissionController {

    private final CustomerMissionService customerMissionService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<MissionResponse>> getCustomerMissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        MissionResponse result = customerMissionService.getCustomerMissions(page, size);
        
        return ResponseEntity.ok(BaseResponse.<MissionResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(result)
                .build());
    }

    @PostMapping("/claim-reward")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<ClaimMissionRewardResponse>> claimMissionReward(
            @RequestBody ClaimMissionRewardRequest request) {
        
        ClaimMissionRewardResponse result = customerMissionService.claimMissionReward(request.getMissionId());
        
        return ResponseEntity.ok(BaseResponse.<ClaimMissionRewardResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(result)
                .build());
    }
}