package com.example.customerservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherRequestStatsResponse {
    private Long usedVoucherCount;
    private BigDecimal totalDiscountAmount;
}
