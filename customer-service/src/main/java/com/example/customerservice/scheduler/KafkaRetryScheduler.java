package com.example.customerservice.scheduler;

import com.example.customerservice.dto.request.KafkaRequest;
import com.example.customerservice.entity.RetryableKafkaMessage;
import com.example.customerservice.repository.RetryableKafkaMessageRepository;
import com.example.customerservice.service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaRetryScheduler {

    private final RetryableKafkaMessageRepository retryableKafkaMessageRepository;
    private final KafkaService kafkaService;

    @Scheduled(cron = "0 */5 * * * *") // Chạy mỗi 5 phút
    public void processRetryableMessages() {
        log.info("Starting retry process for failed Kafka messages");
        
        int batchSize = 100;
        long nextId = 0;
        int batchNumber = 0;
        int totalProcessed = 0;
        
        while (true) {
            List<RetryableKafkaMessage> messages = retryableKafkaMessageRepository
                .findByIdGreaterThanAndStatus(
                    nextId,
                    RetryableKafkaMessage.MessageStatus.PROCESSING,
                    PageRequest.of(0, batchSize)
                );
            
            if (messages.isEmpty()) {
                log.warn("No retryable messages found, stopping retry process");
                break;
            }
            
            batchNumber++;
            totalProcessed += messages.size();
            nextId = messages.get(messages.size() - 1).getId();
            
            log.info("Processing batch {} with {} messages, total processed: {}", 
                    batchNumber, messages.size(), totalProcessed);
            
            for (RetryableKafkaMessage message : messages) {
                try {
                    KafkaRequest request = KafkaRequest.builder()
                        .topic(message.getTopic())
                        .key(message.getMessageKey())
                        .payload(message.getPayload())
                        .messageId(message.getMessageId())
                        .build();
                    
                    if (kafkaService.sendSync(request)) {
                        retryableKafkaMessageRepository.delete(message);
                        log.info("Successfully sent and deleted message: {}", message.getMessageId());
                    }
                } catch (Exception ex) {
                    log.error("Failed to send message: {}", message.getMessageId(), ex);
                    message.setRetryCount(message.getRetryCount() + 1);
                    
                    if (message.getRetryCount() > message.getMaxRetry()) {
                        message.setStatus(RetryableKafkaMessage.MessageStatus.FAILED);
                        log.warn("Message {} exceeded max retry count, marking as failed", message.getMessageId());
                    }
                    
                    retryableKafkaMessageRepository.save(message);
                }
            }
            
            // Nếu batch này nhỏ hơn batchSize thì đã hết data
            if (messages.size() < batchSize) {
                break;
            }
        }
        
        log.info("Completed retry process for failed Kafka messages. Total processed: {}", totalProcessed);
    }
}