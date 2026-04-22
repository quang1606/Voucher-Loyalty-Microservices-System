package com.example.voucherservice.service.impl;

import com.example.common.BaseException;
import com.example.voucherservice.constant.ConfirmAction;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.RewardType;
import com.example.voucherservice.dto.request.ConfirmVoucherRequest;
import com.example.voucherservice.dto.request.CreateMissionRequest;
import com.example.voucherservice.entity.VoucherRequestEntity;
import com.example.voucherservice.grpc.IdentityGrpcClient;
import com.example.voucherservice.grpc.MissionGrpcClient;
import com.example.voucherservice.repository.VoucherRepository;
import com.example.voucherservice.repository.VoucherRequestRepository;
import com.example.voucherservice.service.AuthorizationService;
import com.example.voucherservice.service.MissionService;
import com.example.voucherservice.service.helper.VoucherServiceHelper;
import com.example.voucherservice.service.helper.excel.ExcelReaderHelper;
import com.example.voucherservice.service.strategy.VoucherRequestStrategyFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionServiceImpl implements MissionService {

  private final VoucherRequestStrategyFactory strategyFactory;
  private final VoucherServiceHelper voucherServiceHelper;
  private final AuthorizationService authorizationService;
  private final VoucherRequestRepository voucherRequestRepository;
  private final MissionGrpcClient missionGrpcClient;
  private final VoucherServiceImpl voucherService;
  @Override
  public void createMission(CreateMissionRequest request) {
    validateMissionRequest(request);
    try {
      request.setTaskStatus(RequestStatus.INIT);
      voucherService.createVoucher(request);
      missionGrpcClient.createMission(request);
      log.info("Mission created - name: {}, target: {}, reward: {} {}",
          request.getMissionName(), request.getTargetValue(),
          request.getRewardType(), request.getRewardValue());
    } catch (BaseException e) {
      log.error("Create mission BaseException - name: {}, errorCode: {}, message: {}",
          request.getMissionName(), e.getErrorCode(), e.getDescription());
      throw e;
    } catch (Exception e) {
      log.error("Create mission Exception - name: {}, error: {}",
          request.getMissionName(), e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("CREATE_MISSION_ERROR")
          .description("Failed to create mission: " + request.getMissionName())
          .build();
    }
  }

  @Override
  public void submitMission(Long id) {
    try {
      VoucherRequestEntity entity = voucherServiceHelper.findRequestByIdAndStatus(id,
          RequestStatus.INIT);
      entity.setStatus(RequestStatus.PENDING_APPROVE);
      entity.setUpdatedBy(authorizationService.getName());
      voucherRequestRepository.save(entity);

      CreateMissionRequest missionRequest = missionGrpcClient.getMissionById(id);
      missionRequest.setTaskStatus(RequestStatus.PENDING_APPROVE);
      missionGrpcClient.createMission(missionRequest);

      log.info("Mission submitted - id: {}", id);
    } catch (BaseException e) {
      log.error("Submit mission BaseException - id: {}, errorCode: {}, message: {}",
          id, e.getErrorCode(), e.getDescription());
      throw e;
    } catch (Exception e) {
      log.error("Submit mission Exception - id: {}, error: {}", id, e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("SUBMIT_MISSION_ERROR")
          .description("Failed to submit mission: " + id)
          .build();
    }
  }

  @Override
  public void cancelMission(Long id) {
    try {
      VoucherRequestEntity entity = voucherServiceHelper.findRequestByIdAndStatus(id,
          RequestStatus.INIT);
      entity.setStatus(RequestStatus.CANCELLED);
      entity.setUpdatedBy(authorizationService.getName());
      voucherRequestRepository.save(entity);

      CreateMissionRequest missionRequest = missionGrpcClient.getMissionById(id);
      missionRequest.setTaskStatus(RequestStatus.CANCELLED);
      missionGrpcClient.createMission(missionRequest);

      log.info("Mission cancelled - id: {}", id);
    } catch (BaseException e) {
      log.error("Cancel mission BaseException - id: {}, errorCode: {}, message: {}",
          id, e.getErrorCode(), e.getDescription());
      throw e;
    } catch (Exception e) {
      log.error("Cancel mission Exception - id: {}, error: {}", id, e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("CANCEL_MISSION_ERROR")
          .description("Failed to cancel mission: " + id)
          .build();
    }
  }

  @Override
  public void confirmMission(Long id, ConfirmVoucherRequest request) {
    try {
      if (request.getAction() == ConfirmAction.REJECTED
          && (request.getReason() == null || request.getReason().isBlank())) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .errorCode("MISSING_REASON")
            .description("Reason is required when rejecting")
            .build();
      }

      VoucherRequestEntity entity = voucherServiceHelper.findRequestByIdAndStatus(id,
          RequestStatus.PENDING_APPROVE);

      RequestStatus newStatus;
      if (request.getAction() == ConfirmAction.REJECTED) {
        voucherService.handleRejected(entity, request.getReason());
        newStatus = RequestStatus.REJECTED;
      } else {
        voucherService.handleApproved(entity);
        newStatus = RequestStatus.APPROVED;
      }

      CreateMissionRequest missionRequest = missionGrpcClient.getMissionById(id);
      missionRequest.setTaskStatus(newStatus);
      missionGrpcClient.createMission(missionRequest);

      log.info("Mission confirmed - id: {}, action: {}", id, request.getAction());
    } catch (BaseException e) {
      log.error("Confirm mission BaseException - id: {}, errorCode: {}, message: {}",
          id, e.getErrorCode(), e.getDescription());
      throw e;
    } catch (Exception e) {
      log.error("Confirm mission Exception - id: {}, error: {}", id, e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("CONFIRM_MISSION_ERROR")
          .description("Failed to confirm mission: " + id)
          .build();
    }
  }

  private void validateMissionRequest(CreateMissionRequest request) {
    if (request.getRewardType() == RewardType.POINT) {
      try {
        long points = Long.parseLong(request.getRewardValue());
        if (points <= 0) {
          throw new NumberFormatException();
        }
      } catch (NumberFormatException e) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .errorCode("INVALID_REWARD_POINT")
            .description("Reward value must be a positive number when reward type is POINT")
            .build();
      }
    }

    if (!request.getMissionEndDate().isAfter(request.getMissionStartDate())) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("INVALID_MISSION_DATE_RANGE")
          .description("Mission end date must be after start date")
          .build();
    }

    if (request.getMissionStartDate().isBefore(LocalDateTime.now())) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("INVALID_MISSION_START_DATE")
          .description("Mission start date must not be in the past")
          .build();
    }
  }
}
