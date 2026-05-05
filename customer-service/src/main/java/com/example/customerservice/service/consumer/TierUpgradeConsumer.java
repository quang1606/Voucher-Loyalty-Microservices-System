package com.example.customerservice.service.consumer;

import com.example.common.BaseException;
import com.example.customerservice.constant.CustomerTier;
import com.example.customerservice.dto.event.TierUpgradeEvent;
import com.example.customerservice.entity.CustomerProfile;
import com.example.customerservice.repository.CustomerProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TierUpgradeConsumer {

    private final CustomerProfileRepository customerProfileRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "tier-upgrade-topic",
            groupId = "customer-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handleTierUpgrade(String message) {
        try {
            log.info("Received tier upgrade event: {}", message);
            
            TierUpgradeEvent event = objectMapper.readValue(message, TierUpgradeEvent.class);
            
            CustomerProfile profile = customerProfileRepository.findById(event.getCustomerId())
                    .orElseThrow(() -> BaseException.builder()
                            .httpStatus(HttpStatus.NOT_FOUND)
                            .errorCode("CUSTOMER_NOT_FOUND")
                            .description("Customer profile not found: " + event.getCustomerId())
                            .build());
            
            // Update tier in customer profile
            CustomerTier oldTier = profile.getTier();
            CustomerTier newTier = CustomerTier.valueOf(event.getNewTier());
            
            profile.setTier(newTier);
            customerProfileRepository.save(profile);
            
            log.info("Updated customer tier - customerId: {}, oldTier: {}, newTier: {}, totalPoints: {}", 
                    event.getCustomerId(), oldTier, newTier, event.getTotalPoints());
            
        } catch (Exception ex) {
            log.error("Failed to process tier upgrade event: {}", message, ex);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode("TIER_UPGRADE_PROCESSING_FAILED")
                    .description("Failed to process tier upgrade event: " + ex.getMessage())
                    .build();
        }
    }
}