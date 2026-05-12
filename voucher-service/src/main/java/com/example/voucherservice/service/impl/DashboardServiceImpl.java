package com.example.voucherservice.service.impl;

import com.example.voucherservice.dto.projection.VoucherMonthlyStatsProjection;
import com.example.voucherservice.dto.response.DashboardStatsResponse;
import com.example.voucherservice.dto.response.MissionStatsResponse;
import com.example.voucherservice.dto.response.VoucherMonthlyStatsResponse;
import com.example.voucherservice.grpc.IdentityGrpcClient;
import com.example.voucherservice.repository.VoucherRequestRepository;
import com.example.voucherservice.service.AuthorizationService;
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
    private final AuthorizationService authorizationService;
    private final IdentityGrpcClient identityGrpcClient;

    @Override
    public List<VoucherMonthlyStatsResponse> getVoucherMonthlyStats(Integer year) {
        String storeName = getPartnerStoreName();

        List<VoucherMonthlyStatsProjection> projections;
        if (storeName != null) {
            projections = voucherRequestRepository.getApprovedVouchersByMonthAndStore(year, storeName);
        } else {
            projections = voucherRequestRepository.getApprovedVouchersByMonth(year);
        }

        return projections.stream()
                .map(p -> new VoucherMonthlyStatsResponse(p.getMonth(), p.getTotal()))
                .toList();
    }


    @Override
    public DashboardStatsResponse getVoucherRequestStats() {
        String storeName = getPartnerStoreName();

        long totalRequests;
        long completedRequests;
        long incompleteRequests;

        if (storeName != null) {
            totalRequests = voucherRequestRepository.countByStoreName(storeName);
            completedRequests = voucherRequestRepository.countCompletedRequestsByStore(storeName);
            incompleteRequests = voucherRequestRepository.countIncompleteRequestsByStore(storeName);
        } else {
            totalRequests = voucherRequestRepository.count();
            completedRequests = voucherRequestRepository.countCompletedRequests();
            incompleteRequests = voucherRequestRepository.countIncompleteRequests();
        }

        return new DashboardStatsResponse(totalRequests, completedRequests, incompleteRequests);
    }

    @Override
    public MissionStatsResponse getMissionStats() {
        return missionService.getMissionStats();
    }

    private String getPartnerStoreName() {
        if (authorizationService.isPartner()) {
            String userId = authorizationService.getUserId();
            return identityGrpcClient.getNameStore(userId);
        }
        return null;
    }
}
