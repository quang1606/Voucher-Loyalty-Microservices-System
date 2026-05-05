package com.example.customerservice.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    
    private Long invoiceId;
    private Long voucherId;
    private BigDecimal orderAmount;
    
}