package com.example.voucherservice.mapper;

import com.example.voucherservice.dto.response.MockInvoiceResponse;
import com.example.voucherservice.entity.MockInvoiceEntity;

import java.util.List;
import java.util.stream.Collectors;

public final class MockInvoiceMapper {

    private MockInvoiceMapper() {
    }

    public static MockInvoiceResponse toResponse(MockInvoiceEntity entity) {
        return MockInvoiceResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .nameStore(entity.getNameStore())
                .amount(entity.getAmount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static List<MockInvoiceResponse> toResponseList(List<MockInvoiceEntity> entities) {
        return entities.stream()
                .map(MockInvoiceMapper::toResponse)
                .collect(Collectors.toList());
    }
}