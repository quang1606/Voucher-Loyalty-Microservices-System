package com.example.identityservice.dto.request;

import com.example.identityservice.constant.MerchantCategory;
import com.example.identityservice.constant.MerchantStatus;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    // Merchant fields
    private String storeName;
    private String phone;
    private MerchantCategory category;
    private MerchantStatus status;
}
