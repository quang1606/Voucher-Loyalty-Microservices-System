package com.example.voucherservice.service;

import com.example.voucherservice.dto.projection.VoucherMonthlyStatsProjection;
import com.example.voucherservice.dto.response.DashboardStatsResponse;

import java.util.List;

public interface DashboardService {
    List<VoucherMonthlyStatsProjection> getVoucherMonthlyStats(Integer year);
    DashboardStatsResponse getVoucherRequestStats();
}