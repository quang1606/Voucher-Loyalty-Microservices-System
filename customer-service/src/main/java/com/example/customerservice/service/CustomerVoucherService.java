package com.example.customerservice.service;

import com.example.customerservice.entity.CustomerVoucher;
import vn.com.grpc.voucher.entity.SearchVoucherResponse;

import java.util.List;

public interface CustomerVoucherService {
    List<CustomerVoucher> getMyVouchers(Long customerId);
    SearchVoucherResponse getAvailableVouchers(int page, int size);
}
