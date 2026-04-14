package com.example.identityservice.dto.request;

import com.example.identityservice.constant.PartnerCategory;
import com.example.identityservice.constant.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    private Boolean enabled = true;
    @NotNull(message = "Role is required")
    private Role role;
    // Merchant fields
    private String storeName;
    private String phone;
    @NotNull(message = "Request type is required")
    private PartnerCategory category;
}
