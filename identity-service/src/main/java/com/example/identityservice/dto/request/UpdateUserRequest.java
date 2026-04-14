package com.example.identityservice.dto.request;

import com.example.identityservice.constant.PartnerCategory;
import com.example.identityservice.constant.Partner;
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
    private PartnerCategory category;
    private Partner status;
}
