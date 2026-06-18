package com.example.voucherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherUsageStatsResponse {
    private String requestId;
    private String voucherCode;
    private String voucherName;
    private String storeName;
    private Long usedCount;
    private BigDecimal totalDiscountAmount;
}
