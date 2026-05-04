package com.example.customerservice.service;

import com.example.customerservice.constant.CustomerVoucherStatus;
import com.example.customerservice.dto.response.CustomerVoucherListResponse;
import com.example.customerservice.dto.response.CustomerVoucherResponse;
import org.springframework.data.domain.Pageable;
import vn.com.grpc.voucher.entity.SearchVoucherResponse;

import java.util.List;

public interface CustomerVoucherService {
    List<CustomerVoucherResponse> getMyVouchers(Long customerId);
    SearchVoucherResponse getAvailableVouchersWithCollectedStatus(Long customerId, int page, int size);
    CustomerVoucherListResponse getCustomerVouchers(Long customerId, Long voucherId, 
                                                   CustomerVoucherStatus status,
                                                  Pageable pageable);
    void collectVoucher(Long customerId, Long voucherId);
}
