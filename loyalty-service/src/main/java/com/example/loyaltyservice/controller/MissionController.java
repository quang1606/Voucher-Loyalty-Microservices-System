package com.example.loyaltyservice.controller;

import com.example.loyaltyservice.dto.SearchMissionRequestDto;
import com.example.loyaltyservice.dto.SearchMissionResponseDto;
import com.example.loyaltyservice.service.MissionService;
import com.example.loyaltyservice.entity.MissionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.grpc.loyalty.entity.SearchMissionRequest;
import vn.com.grpc.loyalty.entity.Pageable;
import vn.com.grpc.base.entity.RequestInfo;

import java.util.UUID;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@Slf4j
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/search")
    public ResponseEntity<SearchMissionResponseDto> searchMissions(
            @RequestParam(required = false, defaultValue = "0") Long partnerId,
            @RequestParam(required = false) String rewardType,
            @RequestParam(required = false) String taskStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort) {
        
        log.info("REST searchMissions - partnerId: {}, rewardType: {}, taskStatus: {}, page: {}, size: {}", 
            partnerId, rewardType, taskStatus, page, size);

        // Build gRPC request
        SearchMissionRequest.Builder requestBuilder = SearchMissionRequest.newBuilder()
            .setRequestInfo(RequestInfo.newBuilder()
                .setRequestId(UUID.randomUUID().toString())
                .build())
            .setPartnerId(partnerId)
            .setPageable(Pageable.newBuilder()
                .setPage(page)
                .setSize(size)
                .setSort(sort)
                .build());

        // Set reward type if provided
        if (rewardType != null && !rewardType.isEmpty()) {
            try {
                requestBuilder.setRewardType(vn.com.grpc.loyalty.entity.RewardType.valueOf(rewardType.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid reward type: {}", rewardType);
            }
        }

        // Set task status if provided
        if (taskStatus != null && !taskStatus.isEmpty()) {
            try {
                requestBuilder.setTaskStatus(vn.com.grpc.loyalty.entity.TaskStatus.valueOf(taskStatus.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid task status: {}", taskStatus);
            }
        }

        Page<MissionEntity> result = missionService.searchMission(requestBuilder.build());
        
        SearchMissionResponseDto response = SearchMissionResponseDto.fromPage(result);
        
        return ResponseEntity.ok(response);
    }
}