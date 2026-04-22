package com.example.voucherservice.service;

import com.example.voucherservice.dto.request.ConfirmVoucherRequest;
import com.example.voucherservice.dto.request.CreateMissionRequest;

public interface MissionService {
  void createMission(CreateMissionRequest request);

  void submitMission(Long id);

  void cancelMission(Long id);

  void confirmMission(Long id, ConfirmVoucherRequest request);
}
