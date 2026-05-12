package com.example.customerservice.service;

import com.example.customerservice.constant.CustomerVoucherStatus;
import com.example.customerservice.dto.response.ApplicableVoucherListResponse;
import com.example.customerservice.dto.response.AvailableVoucherListResponse;
import com.example.customerservice.dto.response.CustomerVoucherListResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CustomerVoucherService {
    AvailableVoucherListResponse getAvailableVouchersWithCollectedStatus(Long customerId, int page, int size);
    AvailableVoucherListResponse getCustomerVouchers(Long customerId, Long voucherId, 
                                                   CustomerVoucherStatus status,
                                                  Pageable pageable);
    void collectVoucher(Long customerId, Long voucherId);
    ApplicableVoucherListResponse getApplicableVouchers(Long customerId, String nameStore, BigDecimal orderAmount);
}
