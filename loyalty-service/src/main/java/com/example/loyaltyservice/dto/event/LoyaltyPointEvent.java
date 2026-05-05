package com.example.loyaltyservice.dto.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoyaltyPointEvent {
    
    private String transactionId;
    private Long customerId;
    private BigDecimal orderAmount;
    private Integer pointsEarned;
    
}