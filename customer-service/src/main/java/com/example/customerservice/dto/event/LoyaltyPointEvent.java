package com.example.customerservice.dto.event;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LoyaltyPointEvent {
    
    private String transactionId;
    private Long customerId;
    private BigDecimal orderAmount;
    private Integer pointsEarned;
    
}