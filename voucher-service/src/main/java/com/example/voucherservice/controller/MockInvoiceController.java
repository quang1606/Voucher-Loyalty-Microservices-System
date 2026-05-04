package com.example.voucherservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.voucherservice.dto.request.CreateMockInvoiceRequest;
import com.example.voucherservice.dto.response.MockInvoiceListResponse;
import com.example.voucherservice.dto.response.MockInvoiceResponse;
import com.example.voucherservice.service.MockInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class MockInvoiceController {

    private final MockInvoiceService mockInvoiceService;

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<BaseResponse<MockInvoiceResponse>> createInvoice(
            @Valid @RequestBody CreateMockInvoiceRequest request) {
        MockInvoiceResponse response = mockInvoiceService.createInvoice(request);
        return ResponseEntity.ok(BaseResponse.<MockInvoiceResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message("Invoice created successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<MockInvoiceListResponse>> getInvoices(
            @RequestParam(required = false) String nameStore,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1]) 
            ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        MockInvoiceListResponse response = mockInvoiceService.getInvoices(nameStore, title, pageable);

        return ResponseEntity.ok(BaseResponse.<MockInvoiceListResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<MockInvoiceResponse>> getInvoiceById(@PathVariable Long id) {
        MockInvoiceResponse response = mockInvoiceService.getInvoiceById(id);
        return ResponseEntity.ok(BaseResponse.<MockInvoiceResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(response)
                .build());
    }
}