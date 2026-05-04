package com.example.customerservice.entity;

import com.example.customerservice.constant.CreatorType;
import com.example.customerservice.constant.CustomerVoucherStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "voucher_id", nullable = false)
    private Long voucherId;
    @Column(name = "available_usage", nullable = false)
    private Integer availableUsage;

    @Column(name = "voucher_code", nullable = false, unique = true)
    private String voucherCode;

    @Column(name = "name_store")
    private String nameStore;

    @Enumerated(EnumType.STRING)
    @Column(name = "creator_type")
    private CreatorType creatorType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerVoucherStatus status = CustomerVoucherStatus.AVAILABLE;

    @Column(name = "obtained_at", nullable = false)
    private LocalDateTime obtainedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @PrePersist
    private void prePersist() {
        if (obtainedAt == null) obtainedAt = LocalDateTime.now();
    }
}
