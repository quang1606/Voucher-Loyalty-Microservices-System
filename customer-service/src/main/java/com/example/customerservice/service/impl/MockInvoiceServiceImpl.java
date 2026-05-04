package com.example.customerservice.service.impl;

import com.example.customerservice.dto.response.MockInvoiceListResponse;
import com.example.customerservice.dto.response.MockInvoiceResponse;
import com.example.customerservice.grpc.VoucherGrpcClient;
import com.example.customerservice.service.MockInvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.grpc.voucher.entity.GetMockInvoicesResponse;
import vn.com.grpc.voucher.entity.MockInvoiceInfo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockInvoiceServiceImpl implements MockInvoiceService {

    private final VoucherGrpcClient voucherGrpcClient;

    @Override
    public MockInvoiceListResponse getMockInvoices(String nameStore, String title, int page, int size) {
        log.info("Getting mock invoices - nameStore: {}, title: {}, page: {}, size: {}", nameStore, title, page, size);

        GetMockInvoicesResponse grpcResponse = voucherGrpcClient.getMockInvoices(nameStore, title, page, size);

        List<MockInvoiceResponse> invoices = grpcResponse.getInvoicesList().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return MockInvoiceListResponse.builder()
                .data(invoices)
                .totalElements(grpcResponse.getTotalElements())
                .totalPages(grpcResponse.getTotalPages())
                .build();
    }

    private MockInvoiceResponse toResponse(MockInvoiceInfo info) {
        return MockInvoiceResponse.builder()
                .id(info.getId())
                .title(info.getTitle())
                .nameStore(info.getNameStore())
                .amount(info.getAmount())
                .createdAt(info.getCreatedAt())
                .updatedAt(info.getUpdatedAt())
                .build();
    }
}
