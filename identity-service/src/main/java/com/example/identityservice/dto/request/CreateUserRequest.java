package com.example.identityservice.dto.request;

import com.example.identityservice.entity.enums.MerchantCategory;
import com.example.identityservice.entity.enums.Role;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean enabled = true;
    private Role role;
    // Merchant fields
    private String storeName;
    private String phone;
    private MerchantCategory category;
}
