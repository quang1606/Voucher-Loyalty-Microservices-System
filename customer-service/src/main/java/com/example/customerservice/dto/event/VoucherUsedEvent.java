package com.example.customerservice.dto.event;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class VoucherUsedEvent {
    
    private String transactionId;
    private Long voucherId;
    private String voucherCode;
    private Long customerId;
    private BigDecimal orderAmount;
    private BigDecimal discountAmount;
    
}