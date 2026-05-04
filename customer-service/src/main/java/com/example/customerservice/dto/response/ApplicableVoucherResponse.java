package com.example.customerservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicableVoucherResponse {
    private Long voucherId;
    private String voucherCode;
    private String voucherName;
    private String description;
    private String discountType;
    private String discountValue;
    private String maxDiscount;
    private String minOrderValue;
    private Integer availableStock;
    private String nameStore;
    private String creatorType;
    private boolean applicable;
    private String reason;
}
