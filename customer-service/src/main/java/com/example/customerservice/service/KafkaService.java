package com.example.customerservice.service;

import com.example.customerservice.dto.request.KafkaRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(KafkaRequest request) {
        String messageSn = UUID.randomUUID().toString().substring(0, 8);
        
        log.info("[{}] Sending to {}: {}", messageSn, request.getTopic(), request.getPayload());
        
        kafkaTemplate.send(request.getTopic(), request.getKey(), request.getPayload())
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("[{}] Send success", messageSn);
                    } else {
                        log.error("[{}] Send failure", messageSn, ex);

                    }
                });
    }
}