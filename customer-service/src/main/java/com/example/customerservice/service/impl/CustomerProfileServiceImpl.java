package com.example.customerservice.service.impl;

import com.example.common.BaseErrorCode;
import com.example.common.BaseException;
import com.example.customerservice.dto.response.CustomerProfileResponse;
import com.example.customerservice.entity.CustomerProfile;
import com.example.customerservice.repository.CustomerProfileRepository;
import com.example.customerservice.service.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private static final String LEADERBOARD_KEY = "customer:leaderboard";

    private final CustomerProfileRepository customerProfileRepository;
    private final RedisTemplate<String, Object> redisTemplate;

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
        profile.setBalance(BigDecimal.valueOf(100000000));
        customerProfileRepository.save(profile);
        log.info("Created CustomerProfile for userId: {}, customerId: {}", userId, profile.getId());

        // Add to Redis leaderboard with 0 points (member = customerId, score = 0)
        try {
            redisTemplate.opsForZSet().add(LEADERBOARD_KEY, profile.getId().toString(), 0);
            log.info("Added customer to leaderboard - customerId: {}", profile.getId());
        } catch (Exception ex) {
            log.warn("Failed to add customer to Redis leaderboard - customerId: {}: {}", profile.getId(), ex.getMessage());
        }
    }
}
