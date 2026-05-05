package com.example.customerservice.service;

import com.example.customerservice.dto.request.ClaimMissionRewardResponse;
import com.example.customerservice.dto.response.MissionResponse;

public interface CustomerMissionService {
    
    MissionResponse getCustomerMissions(int page, int size);
    
    ClaimMissionRewardResponse claimMissionReward(Long missionId);
    
}