package com.example.customerservice.mapper;

import com.example.customerservice.dto.response.CustomerMissionResponse;
import com.example.customerservice.entity.CustomerMission;

import java.util.List;
import java.util.stream.Collectors;

public final class CustomerMissionMapper {

    private CustomerMissionMapper() {
    }

    public static CustomerMissionResponse toResponse(CustomerMission entity) {
        return CustomerMissionResponse.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .missionId(entity.getMissionId())
                .currentProgress(entity.getCurrentProgress())
                .targetValue(entity.getTargetValue())
                .status(entity.getStatus())
                .startedAt(entity.getStartedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static List<CustomerMissionResponse> toResponseList(List<CustomerMission> entities) {
        return entities.stream()
                .map(CustomerMissionMapper::toResponse)
                .collect(Collectors.toList());
    }
}