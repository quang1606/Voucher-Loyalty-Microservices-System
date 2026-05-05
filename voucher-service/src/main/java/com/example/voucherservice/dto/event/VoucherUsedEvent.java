package com.example.voucherservice.dto.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VoucherUsedEvent {
    
    private String transactionId;
    private Long voucherId;
    private String voucherCode;
    private Long customerId;
    private BigDecimal orderAmount;
    private BigDecimal discountAmount;
    
}