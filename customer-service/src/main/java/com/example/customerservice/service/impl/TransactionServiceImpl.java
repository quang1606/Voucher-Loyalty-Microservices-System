package com.example.customerservice.service.impl;

import com.example.common.BaseException;
import com.example.customerservice.dto.response.TransactionListResponse;
import com.example.customerservice.dto.response.TransactionResponse;
import com.example.customerservice.dto.response.VoucherRequestStatsResponse;
import com.example.customerservice.entity.Transaction;
import com.example.customerservice.repository.TransactionRepository;
import com.example.customerservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public TransactionListResponse getTransactions(Long customerId, Pageable pageable) {
        Page<Transaction> page = transactionRepository.findByCustomerId(customerId, pageable);

        return TransactionListResponse.builder()
                .data(page.getContent().stream().map(this::toResponse).toList())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .build();
    }

    @Override
    public TransactionResponse getTransactionDetail(Long id, Long customerId) {
        Transaction transaction = transactionRepository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode("TRANSACTION_NOT_FOUND")
                        .description("Transaction not found")
                        .build());

        return toResponse(transaction);
    }

    @Override
    public VoucherRequestStatsResponse getVoucherRequestStats(String requestId, Integer month, Integer year) {
        VoucherRequestStatsResponse stats;
        if (month != null && year != null) {
            stats = transactionRepository.getStatsByRequestIdAndStatusAndMonthAndYear(
                    requestId, Transaction.TransactionStatus.SUCCESS, month, year);
        } else if (year != null) {
            stats = transactionRepository.getStatsByRequestIdAndStatusAndYear(
                    requestId, Transaction.TransactionStatus.SUCCESS, year);
        } else {
            stats = transactionRepository.getStatsByRequestIdAndStatus(
                    requestId, Transaction.TransactionStatus.SUCCESS);
        }
        if (stats == null) {
            return new VoucherRequestStatsResponse(0L, java.math.BigDecimal.ZERO);
        }
        return stats;
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .transactionId(t.getTransactionId())
                .customerId(t.getCustomerId())
                .invoiceId(t.getInvoiceId())
                .voucherId(t.getVoucherId())
                .voucherCode(t.getVoucherCode())
                .originalAmount(t.getOriginalAmount())
                .discountAmount(t.getDiscountAmount())
                .finalAmount(t.getFinalAmount())
                .pointsEarned(t.getPointsEarned())
                .status(t.getStatus().name())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
