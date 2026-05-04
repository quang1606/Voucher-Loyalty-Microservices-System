package com.example.voucherservice.service.impl;

import com.example.common.BaseException;
import com.example.voucherservice.dto.request.CreateMockInvoiceRequest;
import com.example.voucherservice.dto.response.MockInvoiceListResponse;
import com.example.voucherservice.dto.response.MockInvoiceResponse;
import com.example.voucherservice.entity.MockInvoiceEntity;
import com.example.voucherservice.mapper.MockInvoiceMapper;
import com.example.voucherservice.repository.MockInvoiceRepository;
import com.example.voucherservice.service.MockInvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockInvoiceServiceImpl implements MockInvoiceService {

    private final MockInvoiceRepository mockInvoiceRepository;

    @Override
    public MockInvoiceResponse createInvoice(CreateMockInvoiceRequest request) {
        log.info("Creating mock invoice - title: {}, nameStore: {}, amount: {}", 
                request.getTitle(), request.getNameStore(), request.getAmount());

        MockInvoiceEntity entity = new MockInvoiceEntity();
        entity.setTitle(request.getTitle());
        entity.setNameStore(request.getNameStore());
        entity.setAmount(request.getAmount());

        MockInvoiceEntity savedEntity = mockInvoiceRepository.save(entity);
        log.info("Mock invoice created successfully - id: {}", savedEntity.getId());

        return MockInvoiceMapper.toResponse(savedEntity);
    }

    @Override
    public MockInvoiceListResponse getInvoices(String nameStore, String title, Pageable pageable) {
        log.info("Getting invoices - nameStore: {}, title: {}, page: {}, size: {}", 
                nameStore, title, pageable.getPageNumber(), pageable.getPageSize());

        Page<MockInvoiceEntity> invoicePage = mockInvoiceRepository.findByFilters(nameStore, title, pageable);

        log.info("Found {} invoices", invoicePage.getTotalElements());

        return MockInvoiceListResponse.builder()
                .data(MockInvoiceMapper.toResponseList(invoicePage.getContent()))
                .totalElements((int) invoicePage.getTotalElements())
                .totalPages(invoicePage.getTotalPages())
                .currentPage(invoicePage.getNumber())
                .pageSize(invoicePage.getSize())
                .build();
    }

    @Override
    public MockInvoiceResponse getInvoiceById(Long id) {
        log.info("Getting invoice by id: {}", id);

        MockInvoiceEntity entity = mockInvoiceRepository.findById(id)
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode("INVOICE_NOT_FOUND")
                        .description("Invoice not found with id: " + id)
                        .build());

        return MockInvoiceMapper.toResponse(entity);
    }
}