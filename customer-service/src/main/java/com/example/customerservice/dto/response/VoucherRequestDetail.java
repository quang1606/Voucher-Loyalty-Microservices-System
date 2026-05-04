package com.example.customerservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherRequestDetail {
    private String voucherCode;
    private String voucherName;
    private String description;
    private String discountType;
    private String discountValue;
    private String maxDiscount;
    private String minOrderValue;
    private Integer totalStock;
    private Integer availableStock;
    private Long startDate;
    private Long endDate;
    private String voucherStatus;
    private String nameStore;
}