package com.example.customerservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LeaderboardResponse {
    
    private List<CustomerRank> topCustomers;
    private CustomerRank currentCustomer;
    
    @Data
    @Builder
    public static class CustomerRank {
        private Long customerId;
        private String customerName;
        private Integer totalPoints;
        private Integer rank;
    }
    
}