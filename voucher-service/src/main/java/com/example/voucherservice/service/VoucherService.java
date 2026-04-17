package com.example.voucherservice.service;

import com.example.voucherservice.constant.CreatorType;
import com.example.voucherservice.constant.CustomerTier;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestMode;
import com.example.voucherservice.constant.VoucherPurpose;
import com.example.voucherservice.constant.VoucherStatus;
import com.example.voucherservice.dto.request.ConfirmVoucherRequest;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
import com.example.voucherservice.dto.request.CreateVoucherExcelRequest;
import com.example.voucherservice.dto.response.VoucherDetailResponsePage;
import com.example.voucherservice.dto.response.VoucherRequestResponse;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.dto.response.VoucherRequestResponsePage;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

public interface VoucherService {

    void createVoucher(CreateVoucherRequest request);

    void createVoucherByExcel(CreateVoucherExcelRequest request);

    VoucherRequestResponsePage getVouchers(RequestStatus status, RequestMode requestMode,
            CreatorType creatorType, VoucherPurpose voucherPurpose, String storeName,
            LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    VoucherDetailResponsePage getAllVoucherDetails(CreatorType creatorType, CustomerTier customerTier,
            DiscountType discountType, VoucherPurpose voucherPurpose, VoucherStatus voucherStatus,
            String storeName, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    void submitVoucher(Long id);

    void cancelVoucher(Long id);

    void confirmVoucher(Long id, ConfirmVoucherRequest request);

    VoucherRequestResponse getVoucherById(Long id, String voucherName, RequestStatus status,
        Pageable pageable);
}
