package com.example.customerservice.dto.response;

import com.example.customerservice.constant.CustomerStatus;
import com.example.customerservice.constant.CustomerTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileResponse {
    private Long id;
    private UUID userId;
    private String fullName;
    private BigDecimal balance;
    private Integer totalPoints;
    private CustomerTier tier;
    private CustomerStatus status;
    private LocalDateTime createdAt;
}
