package com.example.customerservice.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KafkaRequest {
    
    private String topic;
    private String key;
    private String payload;
    
}