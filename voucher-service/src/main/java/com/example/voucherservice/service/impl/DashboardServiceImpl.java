package com.example.voucherservice.service.impl;

import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.dto.projection.VoucherMonthlyStatsProjection;
import com.example.voucherservice.dto.response.DashboardStatsResponse;
import com.example.voucherservice.dto.response.MissionStatsResponse;
import com.example.voucherservice.dto.response.VoucherMonthlyStatsResponse;
import com.example.voucherservice.dto.response.VoucherRequestStatusStatsResponse;
import com.example.voucherservice.dto.response.VoucherUsageStatsPageResponse;
import com.example.voucherservice.dto.response.VoucherUsageStatsResponse;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.entity.VoucherRequestEntity;
import com.example.voucherservice.grpc.CustomerGrpcClient;
import com.example.voucherservice.grpc.IdentityGrpcClient;
import com.example.voucherservice.grpc.MissionGrpcClient;
import com.example.voucherservice.repository.VoucherRepository;
import com.example.voucherservice.repository.VoucherRequestRepository;
import com.example.voucherservice.service.AuthorizationService;
import com.example.voucherservice.service.DashboardService;
import com.example.voucherservice.service.MissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.grpc.customer.entity.GetVoucherUsageStatsResponse;
import vn.com.grpc.customer.entity.VoucherUsageStatsItem;
import vn.com.grpc.loyalty.entity.GetMissionMonthlyStatsResponse;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final VoucherRequestRepository voucherRequestRepository;
    private final VoucherRepository voucherRepository;
    private final MissionService missionService;
    private final MissionGrpcClient missionGrpcClient;
    private final AuthorizationService authorizationService;
    private final IdentityGrpcClient identityGrpcClient;
    private final CustomerGrpcClient customerGrpcClient;

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
        log.info("totalrequest:{}, completedRequests :{}, incompleteRequests :{}",totalRequests,completedRequests,incompleteRequests);

        return new DashboardStatsResponse(totalRequests, completedRequests, incompleteRequests);
    }

    @Override
    public MissionStatsResponse getMissionStats() {
        return missionService.getMissionStats();
    }

    @Override
    public List<VoucherMonthlyStatsResponse> getMissionMonthlyStats(Integer year) {
        Long partnerId = getPartnerId();

        GetMissionMonthlyStatsResponse response = missionGrpcClient.getMissionMonthlyStats(year, partnerId);

        return response.getStatsList().stream()
                .map(s -> new VoucherMonthlyStatsResponse(s.getMonth(), s.getTotal()))
                .toList();
    }

    @Override
    public VoucherRequestStatusStatsResponse getVoucherRequestStatusStats() {
        String storeName = getPartnerStoreName();

        if (storeName != null) {
            return new VoucherRequestStatusStatsResponse(
                    voucherRequestRepository.countByStatusDraftAndStore(storeName),
                    voucherRequestRepository.countByStatusCancelledAndStore(storeName),
                    voucherRequestRepository.countByStatusPendingApproveAndStore(storeName),
                    voucherRequestRepository.countByStatusFinishAndStore(storeName),
                    voucherRequestRepository.countByStatusRejectedAndStore(storeName),
                    voucherRequestRepository.countByStatusFailedAndStore(storeName)
            );
        }

        return new VoucherRequestStatusStatsResponse(
                voucherRequestRepository.countByStatusDraft(),
                voucherRequestRepository.countByStatusCancelled(),
                voucherRequestRepository.countByStatusPendingApprove(),
                voucherRequestRepository.countByStatusFinish(),
                voucherRequestRepository.countByStatusRejected(),
                voucherRequestRepository.countByStatusFailed()
        );
    }

    @Override
    public VoucherUsageStatsPageResponse getVoucherUsageStats(Integer month, Integer year, String nameStore, String requestIdFilter) {
        String storeName = getPartnerStoreName();
        log.info("getVoucherUsageStats - isPartner={}, storeName={}, nameStore={}, requestIdFilter={}",
                authorizationService.isPartner(), storeName, nameStore, requestIdFilter);

        List<VoucherRequestEntity> finishedRequests;

        if (requestIdFilter != null && !requestIdFilter.isEmpty()) {
            // Filter by specific requestId
            VoucherRequestEntity entity = voucherRequestRepository.findByRequestId(requestIdFilter).orElse(null);
            if (entity == null || entity.getStatus() != RequestStatus.FINISH) {
                return VoucherUsageStatsPageResponse.builder()
                        .totalDiscountAmount(BigDecimal.ZERO)
                        .totalRequestCount(0L)
                        .totalVoucherUsed(0L)
                        .details(Collections.emptyList())
                        .build();
            }
            // Partner can only access their own store's data
            if (storeName != null && !storeName.equals(entity.getStoreName())) {
                return VoucherUsageStatsPageResponse.builder()
                        .totalDiscountAmount(BigDecimal.ZERO)
                        .totalRequestCount(0L)
                        .totalVoucherUsed(0L)
                        .details(Collections.emptyList())
                        .build();
            }
            finishedRequests = List.of(entity);
        } else if (storeName != null) {
            // Partner: always filter by their own store
            finishedRequests = voucherRequestRepository.findByStatusAndStoreName(RequestStatus.FINISH, storeName);
        } else if (nameStore != null && !nameStore.isEmpty()) {
            // Admin: filter by specified store
            finishedRequests = voucherRequestRepository.findByStatusAndStoreName(RequestStatus.FINISH, nameStore);
        } else {
            // Admin: all data
            finishedRequests = voucherRequestRepository.findByStatus(RequestStatus.FINISH);
        }

        if (finishedRequests.isEmpty()) {
            return VoucherUsageStatsPageResponse.builder()
                    .totalDiscountAmount(BigDecimal.ZERO)
                    .totalRequestCount(0L)
                    .totalVoucherUsed(0L)
                    .details(Collections.emptyList())
                    .build();
        }

        List<String> requestIds = finishedRequests.stream()
                .map(VoucherRequestEntity::getRequestId)
                .toList();

        log.info("getVoucherUsageStats - month={}, year={}, nameStore={}, requestIdFilter={}, requestIds={}",
                month, year, nameStore, requestIdFilter, requestIds);

        GetVoucherUsageStatsResponse grpcResponse = customerGrpcClient.getVoucherUsageStats(requestIds, month, year, requestIdFilter);

        log.info("getVoucherUsageStats - gRPC response statsCount={}, stats={}",
                grpcResponse.getStatsCount(),
                grpcResponse.getStatsList().stream()
                        .map(s -> String.format("{requestId=%s, voucherCode=%s, usedCount=%d, totalDiscount=%s}",
                                s.getRequestId(), s.getVoucherCode(), s.getUsedCount(), s.getTotalDiscountAmount()))
                        .toList());

        // Build a map of voucherCode -> voucherName from voucher details
        Map<String, String> voucherCodeToNameMap = requestIds.stream()
                .flatMap(reqId -> voucherRepository.findByRequestId(reqId).stream())
                .collect(Collectors.toMap(
                        VoucherDetailEntity::getVoucherCode,
                        v -> v.getVoucherName() != null ? v.getVoucherName() : "",
                        (existing, replacement) -> existing
                ));

        Map<String, VoucherRequestEntity> requestMap = finishedRequests.stream()
                .collect(Collectors.toMap(VoucherRequestEntity::getRequestId, r -> r));

        List<VoucherUsageStatsResponse> details = grpcResponse.getStatsList().stream()
                .map(stat -> {
                    VoucherRequestEntity req = requestMap.get(stat.getRequestId());
                    String voucherName = voucherCodeToNameMap.getOrDefault(stat.getVoucherCode(), "");
                    String resolvedStoreName = (req != null && req.getStoreName() != null) ? req.getStoreName() : "Hệ thống";
                    return VoucherUsageStatsResponse.builder()
                            .requestId(stat.getRequestId())
                            .voucherCode(stat.getVoucherCode())
                            .voucherName(voucherName)
                            .storeName(resolvedStoreName)
                            .usedCount(stat.getUsedCount())
                            .totalDiscountAmount(new BigDecimal(stat.getTotalDiscountAmount()))
                            .build();
                })
                .toList();

        BigDecimal totalDiscount = details.stream()
                .map(VoucherUsageStatsResponse::getTotalDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalVoucherUsed = details.stream()
                .mapToLong(VoucherUsageStatsResponse::getUsedCount)
                .sum();
        long totalRequestCount = details.stream()
                .map(VoucherUsageStatsResponse::getRequestId)
                .distinct()
                .count();

        return VoucherUsageStatsPageResponse.builder()
                .totalDiscountAmount(totalDiscount)
                .totalRequestCount(totalRequestCount)
                .totalVoucherUsed(totalVoucherUsed)
                .details(details)
                .build();
    }

    private String getPartnerStoreName() {
        if (authorizationService.isPartner()) {
            String userId = authorizationService.getUserId();
            return identityGrpcClient.getNameStore(userId);
        }
        return null;
    }

    private Long getPartnerId() {
        if (authorizationService.isPartner()) {
            return identityGrpcClient.getPartner(authorizationService.getUserId()).getId();
        }
        return null;
    }
}
