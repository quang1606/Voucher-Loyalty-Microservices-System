package com.example.loyaltyservice.service.impl;

import com.example.common.BaseException;
import com.example.loyaltyservice.constant.RewardType;
import com.example.loyaltyservice.constant.TaskStatus;
import com.example.loyaltyservice.entity.MissionEntity;
import com.example.loyaltyservice.repository.MissionRepository;
import com.example.loyaltyservice.service.MissionService;
import com.example.loyaltyservice.service.specification.MissionSpecification;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.loyalty.entity.CreateMissionRequestGrpc;
import vn.com.grpc.loyalty.entity.SearchMissionRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;
    private final MissionSpecification missionSpecification;

    @Override
    public Long createMission(CreateMissionRequestGrpc request) {
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
        entity.setStatus(TaskStatus.valueOf(request.getTaskStatus().name()));

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

    @Override
    public Page<MissionEntity> searchMission(SearchMissionRequest request) {
        log.info("Searching missions with filters - partnerId: {}, rewardType: {}, status: {}", 
            request.getNameStore(), request.getRewardType(), request.getTaskStatus());
        
        Specification<MissionEntity> spec = missionSpecification.createSpecification(request);
        Pageable pageable = createPageable(request.getPageable());
        
        return missionRepository.findAll(spec, pageable);
    }

    private Pageable createPageable(vn.com.grpc.loyalty.entity.Pageable pageableGrpc) {
        int page = Math.max(0, pageableGrpc.getPage());
        int size = pageableGrpc.getSize() > 0 ? 
            Math.min(pageableGrpc.getSize(), 100) : 20;
        
        if (pageableGrpc.getSort().isEmpty()) {
            return PageRequest.of(page, size, Sort.by("id").descending());
        }
        
        Sort sort = Sort.by(pageableGrpc.getSort());
        return PageRequest.of(page, size, sort);
    }



    private LocalDateTime toLocalDateTime(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }
}
