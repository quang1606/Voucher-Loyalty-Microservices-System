package com.example.voucherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockInvoiceResponse {
    private Long id;
    private String title;
    private String nameStore;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockInvoiceListResponse {
    private java.util.List<MockInvoiceResponse> data;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}