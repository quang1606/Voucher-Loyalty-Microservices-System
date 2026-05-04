package com.example.customerservice.service;

import com.example.customerservice.dto.response.MockInvoiceListResponse;

public interface MockInvoiceService {
    MockInvoiceListResponse getMockInvoices(String nameStore, String title, int page, int size);
}
