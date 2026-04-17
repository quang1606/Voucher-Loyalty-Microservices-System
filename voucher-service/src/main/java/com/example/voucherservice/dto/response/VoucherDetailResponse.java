package com.example.voucherservice.dto.response;

import com.example.voucherservice.constant.CustomerTier;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDetailResponse {

    private Long id;
    private String voucherCode;
    private String requestId;
    private String voucherName;
    private String description;
    private CustomerTier customerTier;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscount;
    private BigDecimal minOrderValue;
    private Integer totalStock;
    private Integer availableStock;
    private RequestStatus requestStatus;
    private Integer maxCollect;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private VoucherStatus status;
    private String errorMessage;
    private LocalDateTime createdAt;
}
