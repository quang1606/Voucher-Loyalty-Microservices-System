package com.example.voucherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockInvoiceListResponse {
    private List<MockInvoiceResponse> data;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}