package com.example.voucherservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.voucherservice.constant.RewardType;
import com.example.voucherservice.constant.TaskStatus;
import com.example.voucherservice.dto.request.ConfirmVoucherRequest;
import com.example.voucherservice.dto.request.CreateMissionRequest;
import com.example.voucherservice.dto.response.MissionResponse;
import com.example.voucherservice.service.MissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.grpc.loyalty.entity.SearchMissionResponse;

@RestController
@RequestMapping("/api/v1/missions")
@RequiredArgsConstructor
public class MissionController {
  private final MissionService missionService;
  @PostMapping("/missions")
  @PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<Void>> createMission(@Valid @RequestBody CreateMissionRequest request) {
    missionService.createMission(request);
    return ResponseEntity.ok(BaseResponse.<Void>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription()).build());
  }

  @PutMapping("/missions/{id}/submit")
  @PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<Void>> submitMission(@PathVariable Long id) {
    missionService.submitMission(id);
    return ResponseEntity.ok(BaseResponse.<Void>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription()).build());
  }

  @PutMapping("/missions/{id}/cancel")
  @PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<Void>> cancelMission(@PathVariable Long id) {
    missionService.cancelMission(id);
    return ResponseEntity.ok(BaseResponse.<Void>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription()).build());
  }

  @PutMapping("/missions/{id}/confirm")
  @PreAuthorize("hasRole('CHECKER')")
  public ResponseEntity<BaseResponse<Void>> confirmMission(@PathVariable Long id,
      @Valid @RequestBody ConfirmVoucherRequest request) {
    missionService.confirmMission(id, request);
    return ResponseEntity.ok(BaseResponse.<Void>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription()).build());
  }
  @GetMapping("/search")
  @PreAuthorize("hasAnyRole('MAKER', 'CHECKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<MissionResponse>> searchMissions(
      @RequestParam(required = false) String nameStore,
      @RequestParam(required = false) RewardType rewardType,
      @RequestParam(required = false) TaskStatus taskStatus,
      @PageableDefault(size = 20) Pageable pageable) {

    MissionResponse result = missionService.searchMissions(nameStore, rewardType, taskStatus, pageable);

    return ResponseEntity.ok(BaseResponse.<MissionResponse>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription())
        .data(result)
        .build());
  }

}
