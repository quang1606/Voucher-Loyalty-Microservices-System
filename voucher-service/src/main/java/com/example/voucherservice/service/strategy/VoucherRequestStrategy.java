package com.example.voucherservice.service.strategy;

import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.dto.request.CreateVoucherRequest;

public abstract class VoucherRequestStrategy {

    public abstract boolean support(DiscountType type);

    public abstract void validateRequest(CreateVoucherRequest request);
}
