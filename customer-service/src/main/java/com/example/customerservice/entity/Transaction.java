package com.example.customerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "voucher_id")
    private Long voucherId;

    @Column(name = "voucher_code")
    private String voucherCode;

    @Column(name = "request_id", length = 64)
    private String requestId;

    @Column(name = "original_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal originalAmount;

    @Column(name = "discount_amount", precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "final_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal finalAmount;

    @Column(name = "points_earned")
    private Integer pointsEarned;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum TransactionStatus {
        PENDING, SUCCESS, FAILED
    }
}