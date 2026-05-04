package com.example.customerservice.dto.response;

import com.example.customerservice.constant.CreatorType;
import com.example.customerservice.constant.CustomerVoucherStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerVoucherResponse {
    private Long id;
    private Long customerId;
    private Long voucherId;
    private Integer availableUsage;
    private String voucherCode;
    private String nameStore;
    private CreatorType creatorType;
    private CustomerVoucherStatus status;
    private LocalDateTime obtainedAt;
    private LocalDateTime usedAt;
    private LocalDateTime expiredAt;
    private Boolean isCollected; // Trường mới để kiểm tra đã lấy voucher hay chưa
}