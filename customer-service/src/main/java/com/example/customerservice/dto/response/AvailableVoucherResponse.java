package com.example.customerservice.dto.response;

import com.example.customerservice.constant.CustomerVoucherStatus;
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
    private CustomerVoucherStatus voucherStatus;
    private int availableUsage;
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
    private String startDate;
    private String endDate;
    private String status;
    private String createdAt;
    private boolean collected;
}
