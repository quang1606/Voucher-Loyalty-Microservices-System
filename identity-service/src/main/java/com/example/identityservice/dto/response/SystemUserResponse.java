package com.example.identityservice.dto.response;

import com.example.identityservice.constant.PartnerCategory;
import com.example.identityservice.constant.Partner;
import com.example.identityservice.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Role role;
    // Partner
    private String storeName;
    private String phone;
    private PartnerCategory category;
    private Partner status;
}
