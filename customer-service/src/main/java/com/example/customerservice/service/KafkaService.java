package com.example.customerservice.service;

import com.example.customerservice.dto.request.KafkaRequest;
import com.example.customerservice.entity.RetryableKafkaMessage;
import com.example.customerservice.repository.RetryableKafkaMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RetryableKafkaMessageRepository retryableKafkaMessageRepository;

    public void send(KafkaRequest request) {
      String messageSn = UUID.randomUUID().toString().substring(0, 8);

      kafkaTemplate.send(request.getTopic(), request.getKey(), request.getPayload())
          .whenComplete((result, ex) -> {
            if (ex == null) {
              log.info("[{}] Send success", messageSn);
            } else {
              log.error("[{}] Send failed", messageSn, ex);

              if (request.isRetryable()) {
                saveRetryableMessage(request, messageSn);
              }
            }
                });
    }
  public boolean sendSync(KafkaRequest request) {

    try {
      kafkaTemplate
          .send(request.getTopic(), request.getKey(), request.getPayload())
          .get();

      log.info("[{}] Retry send success", request.getMessageId());
      return true;

    } catch (Exception ex) {
      log.error("[{}] Retry send failed", request.getMessageId(), ex);
      return false;
    }
  }
  
  private void saveRetryableMessage(KafkaRequest request, String messageSn) {
    RetryableKafkaMessage retryableMessage = new RetryableKafkaMessage();
    retryableMessage.setMessageId(messageSn);
    retryableMessage.setTopic(request.getTopic());
    retryableMessage.setMessageKey(request.getKey());
    retryableMessage.setPayload(request.getPayload());
    retryableMessage.setRetryCount(0);
    retryableMessage.setMaxRetry(5);
    retryableMessage.setStatus(RetryableKafkaMessage.MessageStatus.PROCESSING);
    retryableMessage.setNextRetryAt(LocalDateTime.now().plusMinutes(5));
    
    retryableKafkaMessageRepository.save(retryableMessage);
    log.info("[{}] Saved retryable message", messageSn);
  }
  

}