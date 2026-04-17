package com.example.voucherservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.voucherservice.dto.response.AuditLogResponse;
import com.example.voucherservice.service.AuditLogService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHECKER')")
    public ResponseEntity<BaseResponse<Page<AuditLogResponse>>> getAuditLogs(
        @RequestParam(name = "userRole", required = false) String userRole,
        @RequestParam(name = "userId", required = false) String userId,
        @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
        @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
        @PageableDefault(size = 20) Pageable pageable) {
        Page<AuditLogResponse> data = auditLogService.getAuditLogs(userRole, userId, fromDate, toDate, pageable);
        return ResponseEntity.ok(BaseResponse.<Page<AuditLogResponse>>builder()
            .status(BaseErrorCode.SUCCESS.getErrorNumCode())
            .code(BaseErrorCode.SUCCESS.getErrorCode())
            .message(BaseErrorCode.SUCCESS.getErrorDescription())
            .data(data)
            .build());
    }
}
