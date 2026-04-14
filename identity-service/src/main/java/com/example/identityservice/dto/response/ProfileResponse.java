package com.example.identityservice.dto.response;

import com.example.identityservice.constant.CustomerTier;
import com.example.identityservice.constant.PartnerCategory;
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
public class ProfileResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    // Merchant
    private String storeName;
    private String phone;
    private PartnerCategory category;
    // Customer
    private BigDecimal balance;
    private CustomerTier tier;
    private Long point;
}
