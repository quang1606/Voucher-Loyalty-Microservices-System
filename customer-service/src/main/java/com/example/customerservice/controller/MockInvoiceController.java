package com.example.customerservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.customerservice.dto.response.MockInvoiceListResponse;
import com.example.customerservice.service.MockInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/invoices")
@RequiredArgsConstructor
public class  MockInvoiceController {

    private final MockInvoiceService mockInvoiceService;

    @GetMapping
    public ResponseEntity<BaseResponse<MockInvoiceListResponse>> getMockInvoices(
            @RequestParam(required = false) String nameStore,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(BaseResponse.<MockInvoiceListResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(mockInvoiceService.getMockInvoices(nameStore, title, page, size))
                .build());
    }
}
