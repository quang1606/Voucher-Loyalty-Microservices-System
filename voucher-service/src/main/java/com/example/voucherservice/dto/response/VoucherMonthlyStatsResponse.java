package com.example.voucherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherMonthlyStatsResponse {
    private Integer month;
    private Long total;
}