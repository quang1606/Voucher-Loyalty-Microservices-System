package com.example.customerservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.customerservice.constant.CustomerVoucherStatus;
import com.example.customerservice.dto.response.CustomerVoucherListResponse;
import com.example.customerservice.dto.response.CustomerVoucherResponse;
import com.example.customerservice.service.CustomerVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<BaseResponse<List<CustomerVoucherResponse>>> getMyVouchers(@PathVariable Long customerId) {
        return ResponseEntity.ok(BaseResponse.<List<CustomerVoucherResponse>>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(customerVoucherService.getMyVouchers(customerId))
                .build());
    }



    @GetMapping("/available/with-status")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<SearchVoucherResponse>> getAvailableVouchersWithStatus(
            @RequestParam Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(BaseResponse.<SearchVoucherResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(customerVoucherService.getAvailableVouchersWithCollectedStatus(customerId, page, size))
                .build());
    }

    @PostMapping("/collect/{voucherId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<String>> collectVoucher(
            @PathVariable Long voucherId,
            @RequestParam Long customerId) {
        customerVoucherService.collectVoucher(customerId, voucherId);
        return ResponseEntity.ok(BaseResponse.<String>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message("Voucher collected successfully")
                .data("Voucher has been added to your collection")
                .build());
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<CustomerVoucherListResponse>> getCustomerVouchers(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long voucherId,
            @RequestParam(required = false) CustomerVoucherStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "obtainedAt,desc") String sort) {

        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1]) 
            ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        CustomerVoucherListResponse response = customerVoucherService.getCustomerVouchers(
                customerId, voucherId, status, pageable);

        return ResponseEntity.ok(BaseResponse.<CustomerVoucherListResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(response)
                .build());
    }
}
