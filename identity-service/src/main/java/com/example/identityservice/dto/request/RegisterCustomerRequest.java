package com.example.identityservice.dto.request;

import lombok.Data;

@Data
public class RegisterCustomerRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
}
