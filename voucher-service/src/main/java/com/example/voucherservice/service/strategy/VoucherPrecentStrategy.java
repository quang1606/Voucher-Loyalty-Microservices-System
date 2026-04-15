package com.example.voucherservice.service.strategy;

import com.example.common.BaseException;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class VoucherPrecentStrategy extends VoucherRequestStrategy {

    @Override
    public boolean support(DiscountType type) {
        return DiscountType.PERCENT == type;
    }

    @Override
    public void validateRequest(CreateVoucherRequest request) {
        if (request.getMaxDiscount() == null) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("INVALID_PERCENT_VOUCHER")
                    .description("Max discount is required for PERCENT discount")
                    .build();
        }
        if (request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("INVALID_PERCENT_VOUCHER")
                    .description("Discount percentage must not exceed 100")
                    .build();
        }
    }
}
