package com.example.customerservice.service;

import com.example.customerservice.dto.request.PaymentRequest;
import com.example.customerservice.dto.response.PaymentResponse;

public interface PaymentService {
    
    PaymentResponse processPayment(PaymentRequest request);
    
}