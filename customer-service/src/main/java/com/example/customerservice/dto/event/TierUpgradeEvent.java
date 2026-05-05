package com.example.customerservice.dto.event;

import lombok.Data;

@Data
public class TierUpgradeEvent {
    
    private String transactionId;
    private Long customerId;
    private String oldTier;
    private String newTier;
    private Long totalPoints;
    
}