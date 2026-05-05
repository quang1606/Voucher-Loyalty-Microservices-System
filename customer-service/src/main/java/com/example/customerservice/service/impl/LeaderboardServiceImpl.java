package com.example.customerservice.service.impl;

import com.example.common.BaseException;
import com.example.customerservice.dto.response.LeaderboardResponse;
import com.example.customerservice.entity.CustomerProfile;
import com.example.customerservice.repository.CustomerProfileRepository;
import com.example.customerservice.service.AuthorizationService;
import com.example.customerservice.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardServiceImpl implements LeaderboardService {

    private static final String LEADERBOARD_KEY = "customer:leaderboard";
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomerProfileRepository customerProfileRepository;
    private final AuthorizationService authorizationService;

    @Override
    public void updateCustomerPoints(Long customerId, Integer points) {
        try {
            // Add/update customer points in Redis ZSET
            redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, customerId.toString(), points);
            
            log.debug("Updated customer points in leaderboard - customerId: {}, points: {}", customerId, points);
        } catch (Exception ex) {
            log.error("Failed to update customer points in leaderboard - customerId: {}, points: {}: {}", 
                    customerId, points, ex.getMessage());
        }
    }

    @Override
    public LeaderboardResponse getLeaderboard() {
        String userId = authorizationService.getUserId();
        CustomerProfile currentProfile = customerProfileRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode("CUSTOMER_NOT_FOUND")
                        .description("Customer profile not found")
                        .build());

        // Get top 5 customers from Redis ZSET (highest scores first)
        Set<ZSetOperations.TypedTuple<Object>> topCustomersSet = redisTemplate.opsForZSet()
                .reverseRangeWithScores(LEADERBOARD_KEY, 0, 4);

        List<LeaderboardResponse.CustomerRank> topCustomers = new ArrayList<>();
        
        if (topCustomersSet != null) {
            int rank = 1;
            for (ZSetOperations.TypedTuple<Object> tuple : topCustomersSet) {
                Long customerId = Long.parseLong(tuple.getValue().toString());
                Integer points = tuple.getScore() != null ? tuple.getScore().intValue() : 0;
                
                Optional<CustomerProfile> profileOpt = customerProfileRepository.findById(customerId);
                if (profileOpt.isPresent()) {
                    CustomerProfile profile = profileOpt.get();
                    topCustomers.add(LeaderboardResponse.CustomerRank.builder()
                            .customerId(customerId)
                            .customerName(profile.getFullName())
                            .totalPoints(points)
                            .rank(rank++)
                            .build());
                }
            }
        }

        // Get current customer rank and points
        LeaderboardResponse.CustomerRank currentCustomer = getCurrentCustomerRank(currentProfile.getId());

        return LeaderboardResponse.builder()
                .topCustomers(topCustomers)
                .currentCustomer(currentCustomer)
                .build();
    }

    private LeaderboardResponse.CustomerRank getCurrentCustomerRank(Long customerId) {
        try {
            // Get customer's score from Redis
            Double score = redisTemplate.opsForZSet().score(LEADERBOARD_KEY, customerId.toString());
            Integer points = score != null ? score.intValue() : 0;
            
            // Get customer's rank (reverse rank because we want highest first)
            Long rank = redisTemplate.opsForZSet().reverseRank(LEADERBOARD_KEY, customerId.toString());
            Integer customerRank = rank != null ? rank.intValue() + 1 : null;
            
            CustomerProfile profile = customerProfileRepository.findById(customerId)
                    .orElse(null);
            
            return LeaderboardResponse.CustomerRank.builder()
                    .customerId(customerId)
                    .customerName(profile != null ? profile.getFullName() : "Unknown")
                    .totalPoints(points)
                    .rank(customerRank)
                    .build();
                    
        } catch (Exception ex) {
            log.error("Failed to get current customer rank - customerId: {}: {}", customerId, ex.getMessage());
            
            CustomerProfile profile = customerProfileRepository.findById(customerId).orElse(null);
            return LeaderboardResponse.CustomerRank.builder()
                    .customerId(customerId)
                    .customerName(profile != null ? profile.getFullName() : "Unknown")
                    .totalPoints(profile != null ? profile.getTotalPoints() : 0)
                    .rank(null)
                    .build();
        }
    }
    
    public void syncAllCustomersToRedis() {
        try {
            List<CustomerProfile> allCustomers = customerProfileRepository.findAll();
            
            for (CustomerProfile customer : allCustomers) {
                if (customer.getTotalPoints() > 0) {
                    redisTemplate.opsForZSet().add(LEADERBOARD_KEY, 
                            customer.getId().toString(), customer.getTotalPoints());
                }
            }
            
            log.info("Synced {} customers to Redis leaderboard", allCustomers.size());
        } catch (Exception ex) {
            log.error("Failed to sync customers to Redis: {}", ex.getMessage());
        }
    }
}