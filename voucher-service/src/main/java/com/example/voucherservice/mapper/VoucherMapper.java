package com.example.voucherservice.mapper;

import com.example.voucherservice.dto.response.VoucherDetailResponse;
import com.example.voucherservice.dto.response.VoucherRequestResponse;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.entity.VoucherRequestEntity;
import java.util.Collections;
import java.util.List;
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
                .build();
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
                .partnerId(entity.getPartnerId())
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
