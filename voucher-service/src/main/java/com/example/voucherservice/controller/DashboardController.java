package com.example.voucherservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.voucherservice.dto.response.DashboardStatsResponse;
import com.example.voucherservice.dto.response.MissionStatsResponse;
import com.example.voucherservice.dto.response.VoucherMonthlyStatsResponse;
import com.example.voucherservice.dto.response.VoucherRequestStatusStatsResponse;
import com.example.voucherservice.dto.response.VoucherUsageStatsPageResponse;
import com.example.voucherservice.dto.response.VoucherUsageStatsResponse;
import com.example.voucherservice.service.DashboardService;
import com.example.voucherservice.service.helper.excel.VoucherUsageExcelExporter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    public ResponseEntity<BaseResponse<List<VoucherMonthlyStatsResponse>>> getVoucherMonthlyStats(
            @RequestParam Integer year) {
        List<VoucherMonthlyStatsResponse> data = dashboardService.getVoucherMonthlyStats(year);
        
        return ResponseEntity.ok(BaseResponse.<List<VoucherMonthlyStatsResponse>>builder()
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

    @GetMapping("/mission-stats")
    public ResponseEntity<BaseResponse<MissionStatsResponse>> getMissionStats() {
        MissionStatsResponse data = dashboardService.getMissionStats();
        
        return ResponseEntity.ok(BaseResponse.<MissionStatsResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(data)
                .build());
    }

    @GetMapping("/mission-monthly-stats")
    public ResponseEntity<BaseResponse<List<VoucherMonthlyStatsResponse>>> getMissionMonthlyStats(
            @RequestParam Integer year) {
        List<VoucherMonthlyStatsResponse> data = dashboardService.getMissionMonthlyStats(year);
        
        return ResponseEntity.ok(BaseResponse.<List<VoucherMonthlyStatsResponse>>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(data)
                .build());
    }

    @GetMapping("/voucher-request-status-stats")
    public ResponseEntity<BaseResponse<VoucherRequestStatusStatsResponse>> getVoucherRequestStatusStats() {
        VoucherRequestStatusStatsResponse data = dashboardService.getVoucherRequestStatusStats();
        
        return ResponseEntity.ok(BaseResponse.<VoucherRequestStatusStatsResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(data)
                .build());
    }

    @GetMapping("/voucher-usage-stats")
    public ResponseEntity<BaseResponse<VoucherUsageStatsPageResponse>> getVoucherUsageStats(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam(required = false) String nameStore,
            @RequestParam(required = false) String requestId) {
        VoucherUsageStatsPageResponse data = dashboardService.getVoucherUsageStats(month, year, nameStore, requestId);

        return ResponseEntity.ok(BaseResponse.<VoucherUsageStatsPageResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(data)
                .build());
    }

    @GetMapping("/voucher-usage-stats/export")
    public ResponseEntity<byte[]> exportVoucherUsageStats(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam(required = false) String nameStore,
            @RequestParam(required = false) String requestId) throws Exception {
        VoucherUsageStatsPageResponse data = dashboardService.getVoucherUsageStats(month, year, nameStore, requestId);
        byte[] excelBytes = VoucherUsageExcelExporter.export(data);

        String fileName = String.format("voucher_usage_stats_%d_%d.xlsx", month, year);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}