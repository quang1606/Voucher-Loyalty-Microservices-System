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
    public CustomerProfileResponse getProfile(UUID UerId) {
        CustomerProfile profile = customerProfileRepository.findByUserId(UerId)
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode(BaseErrorCode.NOT_FOUND.getErrorCode())
                        .description("User not found: " + UerId)
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

    @Override
    public void createCustomerProfile(UUID userId, String fullName) {
        if (customerProfileRepository.existsByUserId(userId)) {
            log.warn("CustomerProfile already exists for userId: {}", userId);
            return;
        }
        CustomerProfile profile = new CustomerProfile();
        profile.setUserId(userId);
        profile.setFullName(fullName);
        customerProfileRepository.save(profile);
        log.info("Created CustomerProfile for userId: {}", userId);
    }
}
