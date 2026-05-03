package com.example.customerservice.service.impl;

import com.example.common.BaseErrorCode;
import com.example.common.BaseException;
import com.example.customerservice.dto.response.CustomerProfileResponse;
import com.example.customerservice.entity.CustomerProfile;
import com.example.customerservice.repository.CustomerProfileRepository;
import com.example.customerservice.service.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;

    @Override
    public CustomerProfileResponse getProfile(UUID customerId) {
        CustomerProfile profile = customerProfileRepository.findByUserId(customerId)
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode(BaseErrorCode.NOT_FOUND.getErrorCode())
                        .description("Customer not found: " + customerId)
                        .build());
        return CustomerProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .balance(profile.getBalance())
                .totalPoints(profile.getTotalPoints())
                .tier(profile.getTier())
                .status(profile.getStatus())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
