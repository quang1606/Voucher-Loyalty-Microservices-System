package com.example.voucherservice.dto.request;

import com.example.voucherservice.constant.CustomerTier;
import com.example.voucherservice.constant.DiscountType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateVoucherRequest {

    @NotBlank(message = "Voucher name is required")
    private String voucherName;

    @NotBlank(message = "Description is required")
    private String description;

    private CustomerTier customerTier;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    private BigDecimal discountValue;

    // PERCENT only
    private BigDecimal maxDiscount;

    // FIXED only
    private BigDecimal minOrderValue;

    @NotNull(message = "Total stock is required")
    @Positive(message = "Total stock must be positive")
    private Integer totalStock;

    private Integer maxCollect;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;
}
