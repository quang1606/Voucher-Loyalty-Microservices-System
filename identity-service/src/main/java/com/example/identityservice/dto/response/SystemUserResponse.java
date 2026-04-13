package com.example.identityservice.dto.response;

import com.example.identityservice.entity.enums.CustomerTier;
import com.example.identityservice.entity.enums.MerchantCategory;
import com.example.identityservice.entity.enums.MerchantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemUserResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    // Merchant
    private String storeName;
    private String phone;
    private MerchantCategory category;
    private MerchantStatus status;
    // Customer
    private BigDecimal balance;
    private CustomerTier tier;
    private Long point;
}
