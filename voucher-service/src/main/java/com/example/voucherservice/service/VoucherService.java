package com.example.voucherservice.service;

import com.example.voucherservice.dto.request.CreateVoucherRequest;
import com.example.voucherservice.dto.request.CreateVoucherExcelRequest;
import com.example.voucherservice.dto.response.VoucherRequestResponse;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestStatus;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherService {

    void createVoucher(CreateVoucherRequest request);

    void createVoucherByExcel(CreateVoucherExcelRequest request);

    Page<VoucherRequestResponse> getVouchers(RequestStatus status, LocalDateTime fromDate,
            LocalDateTime toDate, String partnerId, DiscountType discountType, Pageable pageable);

    void submitVoucher(Long id);

    void cancelVoucher(Long id);

    void confirmVoucher(Long id, String action);
}
