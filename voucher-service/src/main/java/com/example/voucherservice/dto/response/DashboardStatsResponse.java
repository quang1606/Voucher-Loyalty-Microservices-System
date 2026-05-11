package com.example.voucherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Long totalRequests;
    private Long completedRequests;
    private Long incompleteRequests;
}