package com.example.loyaltyservice.service;

import com.example.loyaltyservice.entity.MissionEntity;
import vn.com.grpc.loyalty.entity.CreateMissionRequest;

public interface MissionService {

    Long createMission(CreateMissionRequest request);

    MissionEntity getMissionById(Long id);
}
