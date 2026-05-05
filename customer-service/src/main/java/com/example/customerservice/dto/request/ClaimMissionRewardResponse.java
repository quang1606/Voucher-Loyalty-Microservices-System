package com.example.customerservice.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimMissionRewardResponse {
    
    private String rewardType;
    private String rewardValue;
    private String message;
    
}