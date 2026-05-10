package com.example.voucherservice.service.impl;

import com.example.common.BaseException;
import com.example.voucherservice.constant.*;
import com.example.voucherservice.dto.request.ConfirmVoucherRequest;
import com.example.voucherservice.dto.request.CreateMissionRequest;
import com.example.voucherservice.dto.response.MissionDetailResponse;
import com.example.voucherservice.dto.response.MissionResponse;
import com.example.voucherservice.dto.response.MissionResponseDetail;
import com.example.voucherservice.dto.response.VoucherDetailResponse;
import com.example.voucherservice.entity.VoucherRequestEntity;
import com.example.voucherservice.grpc.IdentityGrpcClient;
import com.example.voucherservice.grpc.MissionGrpcClient;
import com.example.voucherservice.mapper.MissionMapper;
import com.example.voucherservice.mapper.VoucherMapper;
import com.example.voucherservice.repository.VoucherRepository;
import com.example.voucherservice.repository.VoucherRequestRepository;
import com.example.voucherservice.service.AuthorizationService;
import com.example.voucherservice.service.MissionService;
import com.example.voucherservice.service.helper.VoucherServiceHelper;
import com.example.voucherservice.service.strategy.VoucherRequestStrategyFactory;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.loyalty.entity.GetMissionByIdResponse;
import vn.com.grpc.loyalty.entity.SearchMissionResponse;
import vn.com.grpc.loyalty.entity.UpdateMissionStatusResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionServiceImpl implements MissionService {

  private final VoucherRequestStrategyFactory strategyFactory;
  private final VoucherServiceHelper voucherServiceHelper;
  private final AuthorizationService authorizationService;
  private final VoucherRequestRepository voucherRequestRepository;
  private final VoucherRepository voucherRepository;
  private final MissionGrpcClient missionGrpcClient;
  private final VoucherServiceImpl voucherService;
  private final IdentityGrpcClient identityGrpcClient;

  @Override
  public void createMission(CreateMissionRequest request) {
    validateMissionRequest(request);
    try {
      String requestId = "VOUCHER_" + System.currentTimeMillis();
      request.setRequestId(requestId);
      request.setTaskStatus(RequestStatus.INIT);
      log.info("Request: {}", requestId);
      if (request.getRewardType() != RewardType.POINT) {
        request.setVoucherPurpose(VoucherPurpose.REWARD);
      }
      voucherService.createVoucher(request);

      missionGrpcClient.createMission(request);

      log.info("Mission created - name: {}, target: {}, reward: {} {}",
          request.getMissionName(), request.getTargetValue(),
          request.getRewardType(), request.getRewardValue());
    } catch (BaseException e) {
      log.error("Create mission BaseException - name: {}, errorCode: {}, message: {}",
          request.getMissionName(), e.getErrorCode(), e.getDescription());
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("BAD_REQUEST")
          .description("Failed to create mission: " + e.getDescription())
          .build();
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
    log.info("Submitting mission - id: {}", id);
    try {

      UpdateMissionStatusResponse response= missionGrpcClient.updateMissionStatus(id, RequestStatus.PENDING_APPROVE);
      VoucherRequestEntity entity = voucherServiceHelper.findRequestByIdAndStatus(response.getMissions().getRequestId(),
              RequestStatus.INIT);
      entity.setStatus(RequestStatus.PENDING_APPROVE);
      entity.setUpdatedBy(authorizationService.getName());
      voucherRequestRepository.save(entity);

      log.info("Mission submitted - id: {}", id);
    } catch (BaseException e) {
      log.error("Submit mission BaseException - id: {}, errorCode: {}, message: {}",
          id, e.getErrorCode(), e.getDescription());
      throw BaseException.builder()
          .httpStatus(e.getHttpStatus())
          .errorCode(e.getErrorCode())
          .description(e.getDescription())
          .build();
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

      UpdateMissionStatusResponse response= missionGrpcClient.updateMissionStatus(id, RequestStatus.CANCELLED);
      VoucherRequestEntity entity = voucherServiceHelper.findRequestByIdAndStatus(response.getMissions().getRequestId(),
              RequestStatus.INIT);
      entity.setStatus(RequestStatus.CANCELLED);
      entity.setUpdatedBy(authorizationService.getName());
      voucherRequestRepository.save(entity);

      log.info("Mission cancelled - id: {}", id);
    } catch (BaseException e) {
      log.error("Cancel mission BaseException - id: {}, errorCode: {}, message: {}",
          id, e.getErrorCode(), e.getDescription());
      throw BaseException.builder()
          .httpStatus(e.getHttpStatus())
          .errorCode(e.getErrorCode())
          .description(e.getDescription())
          .build();
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



      RequestStatus newStatus;
      if (request.getAction() == ConfirmAction.REJECTED) {
        newStatus = RequestStatus.REJECTED;
      } else {
        newStatus = RequestStatus.APPROVED;
      }

      UpdateMissionStatusResponse response= missionGrpcClient.updateMissionStatus(id, newStatus);
      VoucherRequestEntity entity = voucherServiceHelper.findRequestByIdAndStatus(response.getMissions().getRequestId(),
              RequestStatus.PENDING_APPROVE);
      if (request.getAction() == ConfirmAction.REJECTED) {
        voucherService.handleRejected(entity, request.getReason());
      } else {
        voucherService.handleApproved(entity);
      }

      log.info("Mission confirmed - id: {}, action: {}", id, request.getAction());
    } catch (BaseException e) {
      log.error("Confirm mission BaseException - id: {}, errorCode: {}, message: {}",
          id, e.getErrorCode(), e.getDescription());
      throw BaseException.builder()
          .httpStatus(e.getHttpStatus())
          .errorCode(e.getErrorCode())
          .description(e.getDescription())
          .build();
    } catch (Exception e) {
      log.error("Confirm mission Exception - id: {}, error: {}", id, e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("CONFIRM_MISSION_ERROR")
          .description("Failed to confirm mission: " + id)
          .build();
    }
  }

  @Override
  public MissionResponse searchMissions(String nameStore, RewardType rewardType,
      TaskStatus taskStatus, MissionStatus missionStatus, Pageable pageable) {

    if (authorizationService.isCheckerRole()) {
      List<String> allowedStatuses = List.of(
          TaskStatus.PENDING_APPROVE.name(), TaskStatus.APPROVED.name(),
          TaskStatus.FAILED.name(), TaskStatus.FINISH.name(), TaskStatus.REJECTED.name());
      if (taskStatus != null && !allowedStatuses.contains(taskStatus.name())) {
        return emptyMissionResponse(pageable);
      }
    }

    Long partnerId = null;

    if (authorizationService.isPartner()) {
      partnerId = identityGrpcClient.getPartner(authorizationService.getUserId()).getId();
    } else {
      if (nameStore != null && !nameStore.isBlank()) {
        try {
          partnerId = identityGrpcClient.getPartnerByStoreName(nameStore).getId();
        } catch (BaseException e) {
          log.info("Partner not found for nameStore: {}", nameStore);
          return emptyMissionResponse(pageable);
        }
      }
    }

    SearchMissionResponse response = missionGrpcClient.searchMissions(
        partnerId, rewardType, taskStatus, missionStatus, pageable);

    return MissionMapper.toMissionResponse(response, pageable.getPageNumber(), pageable.getPageSize());
  }

  @Override
  public MissionDetailResponse getMissionDetail(Long missionId) {
    GetMissionByIdResponse grpcResponse = missionGrpcClient.getMissionById(missionId);

    MissionResponseDetail missionDetail = MissionMapper.toMissionResponseDetail(grpcResponse);
    missionDetail.setMissionId(missionId);

    VoucherDetailResponse voucherDetail = null;
    if (missionDetail.getRequestId() != null) {
      voucherDetail = voucherRepository.findFirstByRequestId(missionDetail.getRequestId())
          .map(VoucherMapper::toDetailResponse)
          .orElse(null);
    }

    return MissionDetailResponse.builder()
        .mission(missionDetail)
        .voucherDetail(voucherDetail)
        .build();
  }

  private MissionResponse emptyMissionResponse(Pageable pageable) {
    return MissionResponse.builder()
        .data(Collections.emptyList())
        .totalElements(0)
        .totalPages(0)
        .page(pageable.getPageNumber())
        .size(pageable.getPageSize())
        .build();
  }

  private void validateMissionRequest(CreateMissionRequest request) {
    if (request.getRewardType() == RewardType.POINT) {
      try {
        long points = Long.parseLong(request.getRewardValue());
        if (points <= 0) {
          throw new NumberFormatException();
        }if (request.getRewardValue().isBlank()){
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
