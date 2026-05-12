package com.example.voucherservice.service;

import com.example.voucherservice.dto.projection.VoucherMonthlyStatsProjection;
import com.example.voucherservice.dto.response.DashboardStatsResponse;
import com.example.voucherservice.dto.response.MissionStatsResponse;
import com.example.voucherservice.dto.response.VoucherMonthlyStatsResponse;

import java.util.List;

public interface DashboardService {
    List<VoucherMonthlyStatsResponse> getVoucherMonthlyStats(Integer year);
    DashboardStatsResponse getVoucherRequestStats();
    MissionStatsResponse getMissionStats();
}