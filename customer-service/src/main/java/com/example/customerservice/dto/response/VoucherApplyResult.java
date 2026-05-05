package com.example.customerservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class VoucherApplyResult {
    
    private BigDecimal discountAmount;
    private String voucherCode;
    
}