package com.example.voucherservice.service;

import com.example.voucherservice.dto.request.CreateMockInvoiceRequest;
import com.example.voucherservice.dto.response.MockInvoiceListResponse;
import com.example.voucherservice.dto.response.MockInvoiceResponse;
import org.springframework.data.domain.Pageable;

public interface MockInvoiceService {
    MockInvoiceResponse createInvoice(CreateMockInvoiceRequest request);
    MockInvoiceListResponse getInvoices(String nameStore, String title, Pageable pageable);
    MockInvoiceResponse getInvoiceById(Long id);
}