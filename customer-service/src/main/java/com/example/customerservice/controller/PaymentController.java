package com.example.customerservice.controller;

import com.example.customerservice.dto.request.PaymentRequest;
import com.example.customerservice.dto.response.PaymentResponse;
import com.example.customerservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        log.info("Processing payment - invoiceId: {}, voucherId: {}, amount: {}", 
                request.getInvoiceId(), request.getVoucherId(), request.getOrderAmount());
        
        PaymentResponse response = paymentService.processPayment(request);
        
        return ResponseEntity.ok(response);
    }
}