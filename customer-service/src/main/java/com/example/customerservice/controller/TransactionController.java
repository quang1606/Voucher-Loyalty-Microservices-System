package com.example.customerservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseException;
import com.example.common.BaseResponse;
import com.example.customerservice.dto.response.TransactionListResponse;
import com.example.customerservice.dto.response.TransactionResponse;
import com.example.customerservice.dto.response.VoucherRequestStatsResponse;
import com.example.customerservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<TransactionListResponse>> getTransactions(
            @RequestParam Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        TransactionListResponse data = transactionService.getTransactions(customerId, pageable);

        return ResponseEntity.ok(BaseResponse.<TransactionListResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(data)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<TransactionResponse>> getTransactionDetail(
            @PathVariable Long id,
            @RequestParam Long customerId) {

        TransactionResponse data = transactionService.getTransactionDetail(id, customerId);

        return ResponseEntity.ok(BaseResponse.<TransactionResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(data)
                .build());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<VoucherRequestStatsResponse>> getVoucherRequestStats(
            @RequestParam String requestId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        if (requestId == null || requestId.isBlank()) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("INVALID_REQUEST_ID")
                    .description("requestId is required")
                    .build();
        }

        VoucherRequestStatsResponse data = transactionService.getVoucherRequestStats(requestId, month, year);

        return ResponseEntity.ok(BaseResponse.<VoucherRequestStatsResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(data)
                .build());
    }
}
