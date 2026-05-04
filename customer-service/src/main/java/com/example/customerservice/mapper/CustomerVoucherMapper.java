package com.example.customerservice.mapper;

import com.example.customerservice.dto.response.CustomerVoucherResponse;
import com.example.customerservice.entity.CustomerVoucher;

import java.util.List;
import java.util.stream.Collectors;

public final class CustomerVoucherMapper {

    private CustomerVoucherMapper() {
    }

    public static CustomerVoucherResponse toResponse(CustomerVoucher entity) {
        return CustomerVoucherResponse.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .voucherId(entity.getVoucherId())
                .availableUsage(entity.getAvailableUsage())
                .voucherCode(entity.getVoucherCode())
                .nameStore(entity.getNameStore())
                .creatorType(entity.getCreatorType())
                .status(entity.getStatus())
                .obtainedAt(entity.getObtainedAt())
                .usedAt(entity.getUsedAt())
                .expiredAt(entity.getExpiredAt())
                .isCollected(true)
                .build();
    }

    public static CustomerVoucherResponse toResponseWithCollectedStatus(CustomerVoucher entity, boolean isCollected) {
        return CustomerVoucherResponse.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .voucherId(entity.getVoucherId())
                .availableUsage(entity.getAvailableUsage())
                .voucherCode(entity.getVoucherCode())
                .nameStore(entity.getNameStore())
                .creatorType(entity.getCreatorType())
                .status(entity.getStatus())
                .obtainedAt(entity.getObtainedAt())
                .usedAt(entity.getUsedAt())
                .expiredAt(entity.getExpiredAt())
                .isCollected(isCollected)
                .build();
    }

    public static List<CustomerVoucherResponse> toResponseList(List<CustomerVoucher> entities) {
        return entities.stream()
                .map(CustomerVoucherMapper::toResponse)
                .collect(Collectors.toList());
    }
}