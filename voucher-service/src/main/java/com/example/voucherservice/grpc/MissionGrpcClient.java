package com.example.voucherservice.grpc;

import com.example.common.BaseException;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.dto.request.CreateMissionRequest;
import com.example.voucherservice.utils.GrpcUtils;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.loyalty.entity.CreateMissionResponse;
import vn.com.grpc.loyalty.entity.GetMissionByIdRequest;
import vn.com.grpc.loyalty.entity.GetMissionByIdResponse;
import vn.com.grpc.loyalty.entity.RewardType;
import vn.com.grpc.loyalty.entity.TaskStatus;
import vn.com.grpc.loyalty.service.LoyaltyServiceGrpc;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionGrpcClient {

  private final GrpcUtils grpcUtils;

  @GrpcClient("loyalty-service")
  private LoyaltyServiceGrpc.LoyaltyServiceBlockingStub stub;

  public void createMission(CreateMissionRequest request) {
    vn.com.grpc.loyalty.entity.CreateMissionRequest grpcRequest =
        vn.com.grpc.loyalty.entity.CreateMissionRequest.newBuilder()
            .setRequestInfo(grpcUtils.builderRequestInfo())
            .setMissionName(request.getMissionName())
            .setMissionDescription(request.getMissionDescription())
            .setTargetValue(request.getTargetValue().doubleValue())
            .setRewardType(mapRewardType(request.getRewardType()))
            .setRewardValue(request.getRewardValue())
            .setPartnerId(request.getPartnerId() != null ? request.getPartnerId() : 0L)
            .setStartDate(request.getMissionStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            .setEndDate(request.getMissionEndDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            .setTaskStatus(mapTaskStatus(request.getTaskStatus()))
            .build();

    log.info("gRPC createMission request - missionName: {}, status: {}",
        request.getMissionName(), request.getTaskStatus());

    try {
      CreateMissionResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
          .createMission(grpcRequest);
      String errorCode = response.getResponseInfo().getErrorCode();
      if (!"success".equalsIgnoreCase(errorCode)) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .description(response.getResponseInfo().getMessage())
            .errorCode(errorCode)
            .build();
      }
      log.info("gRPC createMission success - missionName: {}", request.getMissionName());
    } catch (BaseException e) {
      log.error("gRPC createMission BaseException - missionName: {}, error: {}",
          request.getMissionName(), e.getDescription());
      throw e;
    } catch (Exception e) {
      log.error("gRPC createMission Exception - missionName: {}, error: {}",
          request.getMissionName(), e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to create mission: " + request.getMissionName())
          .build();
    }
  }

  public CreateMissionRequest getMissionById(Long missionId) {
    GetMissionByIdRequest grpcRequest = GetMissionByIdRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
        .setMissionId(missionId)
        .build();

    log.info("gRPC getMissionById request - missionId: {}", missionId);

    try {
      GetMissionByIdResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
          .getMissionById(grpcRequest);
      String errorCode = response.getResponseInfo().getErrorCode();
      if (!"success".equalsIgnoreCase(errorCode)) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .description(response.getResponseInfo().getMessage())
            .errorCode(errorCode)
            .build();
      }
      log.info("gRPC getMissionById success - missionId: {}", missionId);
      return mapToCreateMissionRequest(response);
    } catch (BaseException e) {
      log.error("gRPC getMissionById BaseException - missionId: {}, error: {}",
          missionId, e.getDescription());
      throw e;
    } catch (Exception e) {
      log.error("gRPC getMissionById Exception - missionId: {}, error: {}",
          missionId, e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to get mission: " + missionId)
          .build();
    }
  }

  private CreateMissionRequest mapToCreateMissionRequest(GetMissionByIdResponse response) {
    CreateMissionRequest request = new CreateMissionRequest();
    request.setMissionName(response.getMissionName());
    request.setMissionDescription(response.getMissionDescription());
    request.setTargetValue(BigDecimal.valueOf(response.getTargetValue()));
    request.setRewardType(response.getRewardType() == RewardType.POINT
        ? com.example.voucherservice.constant.RewardType.POINT
        : com.example.voucherservice.constant.RewardType.VOUCHER);
    request.setRewardValue(response.getRewardValue());
    request.setPartnerId(response.getPartnerId() != 0 ? response.getPartnerId() : null);
    request.setMissionStartDate(toLocalDateTime(response.getStartDate()));
    request.setMissionEndDate(toLocalDateTime(response.getEndDate()));
    return request;
  }

  private LocalDateTime toLocalDateTime(long epochMillis) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
  }

  private RewardType mapRewardType(com.example.voucherservice.constant.RewardType rewardType) {
    return rewardType == com.example.voucherservice.constant.RewardType.POINT
        ? RewardType.POINT : RewardType.VOUCHER;
  }

  private TaskStatus mapTaskStatus(RequestStatus status) {
    if (status == null) {
      return TaskStatus.TASK_INIT;
    }
    return switch (status) {
      case INIT -> TaskStatus.TASK_INIT;
      case PENDING_APPROVE -> TaskStatus.TASK_PENDING_APPROVE;
      case APPROVED -> TaskStatus.TASK_APPROVED;
      case REJECTED -> TaskStatus.TASK_REJECTED;
      case CANCELLED -> TaskStatus.TASK_CANCELLED;
      case FAILED -> TaskStatus.TASK_FAILED;
      case FINISH -> TaskStatus.TASK_FINISH;
      default -> TaskStatus.TASK_INIT;
    };
  }
}
