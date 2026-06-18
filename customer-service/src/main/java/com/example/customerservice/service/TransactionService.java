package com.example.customerservice.service;

import com.example.customerservice.dto.response.TransactionListResponse;
import com.example.customerservice.dto.response.TransactionResponse;
import com.example.customerservice.dto.response.VoucherRequestStatsResponse;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    TransactionListResponse getTransactions(Long customerId, Pageable pageable);
    TransactionResponse getTransactionDetail(Long id, Long customerId);
    VoucherRequestStatsResponse getVoucherRequestStats(String requestId, Integer month, Integer year);
}
