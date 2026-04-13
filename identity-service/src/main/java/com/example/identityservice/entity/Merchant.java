package com.example.identityservice.entity;

import com.example.identityservice.entity.enums.MerchantCategory;
import com.example.identityservice.entity.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "merchants")
@Data
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(unique = true, nullable = false)
    private String storeName;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MerchantCategory category;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MerchantStatus status = MerchantStatus.ACTIVE;

    private String createdBy;

    @Column(name = "created_at")
    @UpdateTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
