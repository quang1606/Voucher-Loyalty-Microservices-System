package com.example.voucherservice.entity;


import com.example.voucherservice.constant.CustomerTier;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDetailEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "voucher_code", unique = true, nullable = false, length = 16)
  private String voucherCode;

  @Column(name = "request_id", nullable = false, length = 64)
  private String requestId;

  @Column(name = "voucher_name")
  private String voucherName;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "customer_tier")
  private CustomerTier customerTier;

  @Enumerated(EnumType.STRING)
  @Column(name = "discount_type")
  private DiscountType discountType;

  @Column(name = "discount_value", precision = 19, scale = 4)
  private BigDecimal discountValue;

  @Column(name = "max_discount", precision = 19, scale = 4)
  private BigDecimal maxDiscount;

  @Column(name = "min_order_value", precision = 19, scale = 4)
  private BigDecimal minOrderValue;

  @Column(name = "total_stock")
  private Integer totalStock;

  @Column(name = "available_stock")
  private Integer availableStock;

  @Enumerated(EnumType.STRING)
  @Column(name = "request_tatus")
  private RequestStatus requestStatus;

  @Column(name = "max_collect")
  private Integer maxCollect;

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private VoucherStatus status;

  @Column(name = "error_message")
  private String errorMessage;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  private void generateVoucherCode() {
    if (this.voucherCode == null) {
      this.voucherCode = "VCH-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
  }
}