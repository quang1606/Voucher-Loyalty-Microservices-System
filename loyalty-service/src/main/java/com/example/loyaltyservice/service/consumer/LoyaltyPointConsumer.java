package com.example.loyaltyservice.service.consumer;

import com.example.common.BaseException;
import com.example.loyaltyservice.constant.CustomerTier;
import com.example.loyaltyservice.dto.event.LoyaltyPointEvent;
import com.example.loyaltyservice.dto.event.TierUpgradeEvent;
import com.example.loyaltyservice.entity.CustomerEntity;
import com.example.loyaltyservice.repository.UserPointRepository;
import com.example.loyaltyservice.service.KafkaService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class LoyaltyPointConsumer {

    private final UserPointRepository userPointRepository;
    private final ObjectMapper objectMapper;
    private final KafkaService kafkaService;

    @KafkaListener(
            topics = "loyalty-point-topic",
            groupId = "loyalty-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handleLoyaltyPoint(String message) {
        try {
            log.info("Received loyalty point event: {}", message);
            
            LoyaltyPointEvent event = objectMapper.readValue(message, LoyaltyPointEvent.class);
            
            CustomerEntity userPoint = userPointRepository.findById(event.getCustomerId())
                    .orElse(new CustomerEntity());
            
            if (userPoint.getCustomerId() == null) {
                userPoint.setCustomerId(event.getCustomerId());
                userPoint.setTotalPoints(0L);
                userPoint.setCurrentTier(CustomerTier.SILVER);
            }
            
            long oldPoints = userPoint.getTotalPoints();
            long newPoints = oldPoints + event.getPointsEarned();
            userPoint.setTotalPoints(newPoints);
            
            CustomerTier newTier = calculateTier(newPoints);
            CustomerTier oldTier = userPoint.getCurrentTier();
            userPoint.setCurrentTier(newTier);
            
            userPointRepository.save(userPoint);
            
            log.info("Updated user points - customerId: {}, oldPoints: {}, newPoints: {}, oldTier: {}, newTier: {}", 
                    event.getCustomerId(), oldPoints, newPoints, oldTier, newTier);
            
            if (!oldTier.equals(newTier)) {
                log.info("Customer tier upgraded - customerId: {}, from {} to {}", 
                        event.getCustomerId(), oldTier, newTier);
                
                // Send tier upgrade event
                sendTierUpgradeEvent(event.getTransactionId(), event.getCustomerId(), 
                        oldTier.name(), newTier.name(), newPoints);
            }
            
        } catch (Exception ex) {
            log.error("Failed to process loyalty point event: {}", message, ex);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode("LOYALTY_EVENT_PROCESSING_FAILED")
                    .description("Failed to process loyalty point event: " + ex.getMessage())
                    .build();
        }
    }
    
    private CustomerTier calculateTier(long totalPoints) {
        if (totalPoints >= 10000) {
            return CustomerTier.DIAMOND;
        } else if (totalPoints >= 5000) {
            return CustomerTier.GOLD;
        } else {
            return CustomerTier.SILVER;
        }
    }
    
    private void sendTierUpgradeEvent(String transactionId, Long customerId, 
                                    String oldTier, String newTier, Long totalPoints) {
        try {
            TierUpgradeEvent event = TierUpgradeEvent.builder()
                    .transactionId(transactionId)
                    .customerId(customerId)
                    .oldTier(oldTier)
                    .newTier(newTier)
                    .totalPoints(totalPoints)
                    .build();
            
            String payload = objectMapper.writeValueAsString(event);
            kafkaService.send("tier-upgrade-topic", customerId.toString(), payload);
            
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize tier upgrade event: {}", ex.getMessage());
        }
    }
}