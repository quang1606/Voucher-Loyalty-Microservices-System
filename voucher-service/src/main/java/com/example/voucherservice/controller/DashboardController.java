package com.example.voucherservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.voucherservice.dto.projection.VoucherMonthlyStatsProjection;
import com.example.voucherservice.dto.response.DashboardStatsResponse;
import com.example.voucherservice.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/voucher-monthly-stats")
    public ResponseEntity<BaseResponse<List<VoucherMonthlyStatsProjection>>> getVoucherMonthlyStats(
            @RequestParam Integer year) {
        List<VoucherMonthlyStatsProjection> data = dashboardService.getVoucherMonthlyStats(year);
        
        return ResponseEntity.ok(BaseResponse.<List<VoucherMonthlyStatsProjection>>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(data)
                .build());
    }

    @GetMapping("/voucher-request-stats")
    public ResponseEntity<BaseResponse<DashboardStatsResponse>> getVoucherRequestStats() {
        DashboardStatsResponse data = dashboardService.getVoucherRequestStats();
        
        return ResponseEntity.ok(BaseResponse.<DashboardStatsResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(data)
                .build());
    }
}