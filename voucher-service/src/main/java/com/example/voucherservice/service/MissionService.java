package com.example.voucherservice.service;

import com.example.voucherservice.constant.MissionStatus;
import com.example.voucherservice.constant.RewardType;
import com.example.voucherservice.constant.TaskStatus;
import com.example.voucherservice.dto.request.ConfirmVoucherRequest;
import com.example.voucherservice.dto.request.CreateMissionRequest;
import com.example.voucherservice.dto.response.MissionDetailResponse;
import com.example.voucherservice.dto.response.MissionResponse;
import com.example.voucherservice.dto.response.MissionStatsResponse;
import org.springframework.data.domain.Pageable;
import vn.com.grpc.loyalty.entity.SearchMissionResponse;

public interface MissionService {
  void createMission(CreateMissionRequest request);

  void submitMission(Long id);

  void cancelMission(Long id);

  void confirmMission(Long id, ConfirmVoucherRequest request);

  MissionResponse searchMissions(String nameStore, RewardType rewardType, TaskStatus taskStatus, MissionStatus missionStatus, Pageable pageable);

  MissionDetailResponse getMissionDetail(Long missionId);

  MissionStatsResponse getMissionStats();
}
