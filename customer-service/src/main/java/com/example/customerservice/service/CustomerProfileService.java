package com.example.customerservice.service;

import com.example.customerservice.dto.response.CustomerProfileResponse;

import java.util.UUID;

public interface CustomerProfileService {
    CustomerProfileResponse getProfile(UUID customerId);
}
