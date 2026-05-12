package com.example.voucherservice.service.impl;

import com.example.voucherservice.dto.projection.VoucherMonthlyStatsProjection;
import com.example.voucherservice.dto.response.DashboardStatsResponse;
import com.example.voucherservice.dto.response.MissionStatsResponse;
import com.example.voucherservice.dto.response.VoucherMonthlyStatsResponse;
import com.example.voucherservice.repository.VoucherRequestRepository;
import com.example.voucherservice.service.DashboardService;
import com.example.voucherservice.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final VoucherRequestRepository voucherRequestRepository;
    private final MissionService missionService;

    @Override
    public List<VoucherMonthlyStatsResponse> getVoucherMonthlyStats(Integer year) {
        List<VoucherMonthlyStatsProjection> projections = voucherRequestRepository.getApprovedVouchersByMonth(year);
        return projections.stream()
                .map(p -> new VoucherMonthlyStatsResponse(p.getMonth(), p.getTotal()))
                .toList();
    }

    @Override
    public DashboardStatsResponse getVoucherRequestStats() {
        long totalRequests = voucherRequestRepository.count();
        long completedRequests = voucherRequestRepository.countCompletedRequests();
        long incompleteRequests = voucherRequestRepository.countIncompleteRequests();

        return new DashboardStatsResponse(totalRequests, completedRequests, incompleteRequests);

    }

    @Override
    public MissionStatsResponse getMissionStats() {
        return missionService.getMissionStats();
    }
}