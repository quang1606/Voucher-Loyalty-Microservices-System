package com.example.loyaltyservice.service.impl;

import com.example.common.BaseException;
import com.example.loyaltyservice.constant.RewardType;
import com.example.loyaltyservice.constant.TaskStatus;
import com.example.loyaltyservice.entity.MissionEntity;
import com.example.loyaltyservice.repository.MissionRepository;
import com.example.loyaltyservice.service.MissionService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.loyalty.entity.CreateMissionRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;

    @Override
    public Long createMission(CreateMissionRequest request) {
        log.info("Creating mission: {}", request.getMissionName());

        if (request.getMissionName().isBlank()) {
            throw BaseException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .errorCode("MISSING_MISSION_NAME")
                .description("Mission name is required")
                .build();
        }

        MissionEntity entity = new MissionEntity();
        entity.setRequestId(request.getRequestInfo().getRequestId());
        entity.setName(request.getMissionName());
        entity.setDescription(request.getMissionDescription());
        entity.setTargetValue(new BigDecimal(request.getTargetValue()));
        entity.setRewardType(RewardType.valueOf(request.getRewardType().name()));
        entity.setRewardValue(request.getRewardValue());
        entity.setStartDate(toLocalDateTime(request.getStartDate()));
        entity.setEndDate(toLocalDateTime(request.getEndDate()));
        entity.setStatus(mapTaskStatus(request.getTaskStatus()));

        if (request.getPartnerId() != 0) {
            entity.setPartnerId(request.getPartnerId());
        }

        MissionEntity saved = missionRepository.save(entity);
        log.info("Mission created successfully with id: {}", saved.getId());
        return saved.getId();
    }

    @Override
    public MissionEntity getMissionById(Long id) {
        return missionRepository.findById(id)
            .orElseThrow(() -> BaseException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .errorCode("MISSION_NOT_FOUND")
                .description("Mission not found with id: " + id)
                .build());
    }

    private TaskStatus mapTaskStatus(vn.com.grpc.loyalty.entity.TaskStatus status) {
        return switch (status) {
            case TASK_CANCELLED -> TaskStatus.CANCELLED;
            case TASK_PENDING_APPROVE -> TaskStatus.PENDING_APPROVE;
            case TASK_APPROVED -> TaskStatus.APPROVED;
            case TASK_REJECTED -> TaskStatus.REJECTED;
            case TASK_FAILED -> TaskStatus.FAILED;
            case TASK_FINISH -> TaskStatus.FINISH;
            default -> TaskStatus.INIT;
        };
    }

    private LocalDateTime toLocalDateTime(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }
}
