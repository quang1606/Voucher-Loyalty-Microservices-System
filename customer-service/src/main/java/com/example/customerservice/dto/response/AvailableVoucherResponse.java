package com.example.customerservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableVoucherResponse {
    private Long id;
    private String voucherCode;
    private String voucherName;
    private String description;
    private String customerTier;
    private String discountType;
    private String discountValue;
    private String maxDiscount;
    private String minOrderValue;
    private Integer totalStock;
    private Integer availableStock;
    private Integer maxCollect;
    private Long startDate;
    private Long endDate;
    private String status;
    private Long createdAt;
    private boolean collected;
}
