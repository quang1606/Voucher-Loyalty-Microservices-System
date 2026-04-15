package com.example.voucherservice.service;

import com.example.voucherservice.dto.request.CreateVoucherRequest;

public interface VoucherService {

    void createVoucher(CreateVoucherRequest request);

    void submitVoucher(Long id);

    void cancelVoucher(Long id);

    void confirmVoucher(Long id, String action);
}
