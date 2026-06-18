package com.example.voucherservice.service;

import com.example.voucherservice.dto.projection.VoucherMonthlyStatsProjection;
import com.example.voucherservice.dto.response.DashboardStatsResponse;
import com.example.voucherservice.dto.response.MissionStatsResponse;
import com.example.voucherservice.dto.response.VoucherMonthlyStatsResponse;
import com.example.voucherservice.dto.response.VoucherRequestStatusStatsResponse;
import com.example.voucherservice.dto.response.VoucherUsageStatsPageResponse;
import com.example.voucherservice.dto.response.VoucherUsageStatsPageResponse;
import com.example.voucherservice.dto.response.VoucherUsageStatsResponse;

import java.util.List;

public interface DashboardService {
    List<VoucherMonthlyStatsResponse> getVoucherMonthlyStats(Integer year);
    DashboardStatsResponse getVoucherRequestStats();
    MissionStatsResponse getMissionStats();
    List<VoucherMonthlyStatsResponse> getMissionMonthlyStats(Integer year);
    VoucherRequestStatusStatsResponse getVoucherRequestStatusStats();
    VoucherUsageStatsPageResponse getVoucherUsageStats(Integer month, Integer year, String nameStore, String requestId);
}