package com.example.customerservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.customerservice.entity.CustomerVoucher;
import com.example.customerservice.service.CustomerVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.com.grpc.voucher.entity.SearchVoucherResponse;

import java.util.List;

@RestController
@RequestMapping("/api/customers/vouchers")
@RequiredArgsConstructor
public class CustomerVoucherController {

    private final CustomerVoucherService customerVoucherService;

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<List<CustomerVoucher>>> getMyVouchers(@PathVariable Long customerId) {
        return ResponseEntity.ok(BaseResponse.<List<CustomerVoucher>>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(customerVoucherService.getMyVouchers(customerId))
                .build());
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<SearchVoucherResponse>> getAvailableVouchers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(BaseResponse.<SearchVoucherResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(customerVoucherService.getAvailableVouchers(page, size))
                .build());
    }
}
