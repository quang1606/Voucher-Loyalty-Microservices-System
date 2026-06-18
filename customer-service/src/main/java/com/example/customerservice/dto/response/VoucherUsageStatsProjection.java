package com.example.customerservice.dto.response;

import java.math.BigDecimal;

public interface VoucherUsageStatsProjection {
    String getRequestId();
    String getVoucherCode();
    Long getUsedCount();
    BigDecimal getTotalDiscount();
}
