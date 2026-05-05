package com.example.loyaltyservice.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TierUpgradeEvent {
    
    private String transactionId;
    private Long customerId;
    private String oldTier;
    private String newTier;
    private Long totalPoints;
    
}