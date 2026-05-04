package com.example.customerservice.dto.response;

import com.example.customerservice.constant.CustomerMissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMissionResponse {
    private Long id;
    private Long customerId;
    private Long missionId;
    private Integer currentProgress;
    private Integer targetValue;
    private CustomerMissionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime updatedAt;
}