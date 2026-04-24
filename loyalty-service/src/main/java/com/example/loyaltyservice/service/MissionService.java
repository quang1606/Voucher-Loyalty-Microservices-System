package com.example.loyaltyservice.service;

import com.example.loyaltyservice.entity.MissionEntity;
import org.springframework.data.domain.Page;
import vn.com.grpc.loyalty.entity.CreateMissionRequestGrpc;
import vn.com.grpc.loyalty.entity.SearchMissionRequest;

public interface MissionService {

    Long createMission(CreateMissionRequestGrpc request);

    MissionEntity getMissionById(Long id);

    Page<MissionEntity> searchMission(SearchMissionRequest request);
}
