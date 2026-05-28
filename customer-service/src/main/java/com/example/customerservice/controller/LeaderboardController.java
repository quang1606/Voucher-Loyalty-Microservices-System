package com.example.customerservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.customerservice.dto.response.LeaderboardResponse;
import com.example.customerservice.service.LeaderboardService;
import com.example.customerservice.service.impl.LeaderboardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final LeaderboardServiceImpl leaderboardServiceImpl;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<LeaderboardResponse>> getLeaderboard() {
        
        LeaderboardResponse result = leaderboardService.getLeaderboard();
        
        return ResponseEntity.ok(BaseResponse.<LeaderboardResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(result)
                .build());
    }

    @PostMapping("/sync")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<String>> syncLeaderboard() {
        leaderboardServiceImpl.syncAllCustomersToRedis();
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data("Leaderboard synced successfully")
                .build());
    }
}