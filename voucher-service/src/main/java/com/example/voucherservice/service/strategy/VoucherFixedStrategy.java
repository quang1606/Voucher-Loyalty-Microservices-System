package com.example.voucherservice.service.strategy;

import com.example.common.BaseException;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class VoucherFixedStrategy extends VoucherRequestStrategy {

    @Override
    public boolean support(DiscountType type) {
        return DiscountType.FIXED == type;
    }

    @Override
    public void validateRequest(CreateVoucherRequest request) {
        if (request.getMinOrderValue() == null) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("INVALID_FIXED_VOUCHER")
                    .description("Min order value is required for FIXED discount")
                    .build();
        }
        if (request.getDiscountValue().compareTo(request.getMinOrderValue()) > 0) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("INVALID_FIXED_VOUCHER")
                    .description("Discount value must not exceed min order value")
                    .build();
        }
    }
}
