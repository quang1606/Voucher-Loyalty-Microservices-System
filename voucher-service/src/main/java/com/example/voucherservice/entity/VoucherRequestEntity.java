package com.example.voucherservice.entity;

import com.example.voucherservice.constant.CreatorType;
import com.example.voucherservice.constant.RequestMode;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherPurpose;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "voucher_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherRequestEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "request_id", unique = true, nullable = false, length = 64)
  private String requestId;

  @Enumerated(EnumType.STRING)
  @Column(name = "request_mode")
  private RequestMode requestMode;

  @Enumerated(EnumType.STRING)
  @Column(name = "creator_type")
  private CreatorType creatorType;

  @Enumerated(EnumType.STRING)
  @Column(name = "voucher_purpose")
  private VoucherPurpose voucherPurpose;

  @Column(name = "file_name")
  private String fileName;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private RequestStatus status;

  @Column(name = "reason")
  private String reason;

  @CreationTimestamp
  @Column(name = "created_time", updatable = false)
  private LocalDateTime createdTime;

  @Column(name = "created_by")
  private String createdBy;

  @UpdateTimestamp
  @Column(name = "updated_time")
  private LocalDateTime updatedTime;

  @Column(name = "updated_by")
  private String updatedBy;

  @Column(name = "confirmed_time")
  private LocalDateTime confirmedTime;

  @Column(name = "confirmed_by")
  private String confirmedBy;

  @Column(name = "store_name")
  private String storeName;
}