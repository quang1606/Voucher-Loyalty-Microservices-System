package com.example.voucherservice.service.strategy;

import com.example.common.BaseException;
import com.example.voucherservice.constant.DiscountType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoucherRequestStrategyFactory {

    private final List<VoucherRequestStrategy> strategies;

    public VoucherRequestStrategy getStrategy(DiscountType type) {
        return strategies.stream()
                .filter(s -> s.support(type))
                .findFirst()
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .errorCode("UNSUPPORTED_DISCOUNT_TYPE")
                        .description("No strategy found for discount type: " + type)
                        .build());
    }
}
