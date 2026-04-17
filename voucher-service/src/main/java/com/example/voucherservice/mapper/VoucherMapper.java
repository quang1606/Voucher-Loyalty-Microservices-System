package com.example.voucherservice.mapper;

import com.example.voucherservice.dto.projection.ProjectionStatus;
import com.example.voucherservice.dto.projection.ProjectionTotalVoucher;
import com.example.voucherservice.dto.response.VoucherDetailResponse;
import com.example.voucherservice.dto.response.VoucherRequestResponse;
import com.example.voucherservice.dto.response.VoucherRequestResponse.StatusCount;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.entity.VoucherRequestEntity;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class VoucherMapper {

    private VoucherMapper() {
    }

    public static VoucherRequestResponse toRequestResponse(VoucherRequestEntity entity) {
        if (entity == null) {
            return null;
        }
        return VoucherRequestResponse.builder()
                .id(entity.getId())
                .requestId(entity.getRequestId())
                .requestMode(entity.getRequestMode())
                .creatorType(entity.getCreatorType())
                .voucherPurpose(entity.getVoucherPurpose())
                .fileName(entity.getFileName())
                .status(entity.getStatus())
                .reason(entity.getReason())
                .createdTime(entity.getCreatedTime())
                .createdBy(entity.getCreatedBy())
                .updatedTime(entity.getUpdatedTime())
                .updatedBy(entity.getUpdatedBy())
                .confirmedTime(entity.getConfirmedTime())
                .confirmedBy(entity.getConfirmedBy())
                .storeName(entity.getStoreName())
                .build();
    }

    public static List<VoucherRequestResponse> toRequestResponseList(
            List<VoucherRequestEntity> entities,
            List<ProjectionTotalVoucher> totals,
            List<ProjectionStatus> statuses) {

        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Long> totalMap = totals.stream()
                .collect(Collectors.toMap(ProjectionTotalVoucher::getRequestId,
                        ProjectionTotalVoucher::getTotalVoucher));

        Map<String, List<StatusCount>> statusMap = statuses.stream()
                .collect(Collectors.groupingBy(ProjectionStatus::getRequestId,
                        Collectors.mapping(
                                s -> StatusCount.builder()
                                        .requestStatus(s.getRequestStatus())
                                        .count(s.getCount())
                                        .build(),
                                Collectors.toList())));

        return entities.stream().map(entity -> {
            VoucherRequestResponse response = toRequestResponse(entity);
            response.setTotalVoucher(totalMap.getOrDefault(entity.getRequestId(), 0L));
            response.setStatusCounts(statusMap.getOrDefault(entity.getRequestId(), Collections.emptyList()));
            return response;
        }).collect(Collectors.toList());
    }

    public static List<VoucherRequestResponse> toRequestResponseList(List<VoucherRequestEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(VoucherMapper::toRequestResponse)
                .collect(Collectors.toList());
    }

    public static VoucherDetailResponse toDetailResponse(VoucherDetailEntity entity) {
        if (entity == null) {
            return null;
        }
        return VoucherDetailResponse.builder()
                .id(entity.getId())
                .voucherCode(entity.getVoucherCode())
                .requestId(entity.getRequestId())
                .voucherName(entity.getVoucherName())
                .description(entity.getDescription())
                .customerTier(entity.getCustomerTier())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .maxDiscount(entity.getMaxDiscount())
                .minOrderValue(entity.getMinOrderValue())
                .totalStock(entity.getTotalStock())
                .availableStock(entity.getAvailableStock())
                .requestStatus(entity.getRequestStatus())
                .maxCollect(entity.getMaxCollect())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static List<VoucherDetailResponse> toDetailResponseList(List<VoucherDetailEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(VoucherMapper::toDetailResponse)
                .collect(Collectors.toList());
    }
}
