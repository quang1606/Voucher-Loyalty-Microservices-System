package com.example.customerservice.service;

import com.example.customerservice.dto.response.LeaderboardResponse;

public interface LeaderboardService {
    
    void updateCustomerPoints(Long customerId, Integer points);
    
    LeaderboardResponse getLeaderboard();
    
}