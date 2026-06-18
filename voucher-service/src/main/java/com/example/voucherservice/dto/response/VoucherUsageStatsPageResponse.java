package com.example.voucherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherUsageStatsPageResponse {
    private BigDecimal totalDiscountAmount;
    private Long totalRequestCount;
    private Long totalVoucherUsed;
    private List<VoucherUsageStatsResponse> details;
}
