package com.example.voucherservice.grpc;

import com.example.common.BaseException;
import com.example.voucherservice.dto.request.CreateMissionRequest;
import com.example.voucherservice.service.AuthorizationService;
import com.example.voucherservice.utils.GrpcUtils;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.loyalty.entity.CreateMissionResponseGrpc;
import vn.com.grpc.loyalty.entity.GetMissionByIdRequest;
import vn.com.grpc.loyalty.entity.GetMissionByIdResponse;
import vn.com.grpc.loyalty.entity.GetMissionMonthlyStatsRequest;
import vn.com.grpc.loyalty.entity.GetMissionMonthlyStatsResponse;
import vn.com.grpc.loyalty.entity.RewardType;
import vn.com.grpc.loyalty.entity.SearchMissionRequest;
import vn.com.grpc.loyalty.entity.SearchMissionResponse;
import vn.com.grpc.loyalty.entity.TaskStatus;
import vn.com.grpc.loyalty.entity.UpdateMissionStatusRequest;
import vn.com.grpc.loyalty.entity.UpdateMissionStatusResponse;
import vn.com.grpc.loyalty.service.LoyaltyServiceGrpc;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissionGrpcClient {

  private final GrpcUtils grpcUtils;

  @GrpcClient("loyalty-service")
  private LoyaltyServiceGrpc.LoyaltyServiceBlockingStub stub;
  private final AuthorizationService authorizationService;
  private final IdentityGrpcClient identityGrpcClient;

  public void createMission(CreateMissionRequest request) {
    long partnerId = 0L;
    if (authorizationService.isPartner()) {
      partnerId = identityGrpcClient.getPartner(authorizationService.getUserId()).getId();
    }
    vn.com.grpc.loyalty.entity.CreateMissionRequestGrpc grpcRequest =
        vn.com.grpc.loyalty.entity.CreateMissionRequestGrpc.newBuilder()
            .setRequestInfo(grpcUtils.builderRequestInfo())
            .setMissionName(request.getMissionName())
            .setMissionDescription(request.getMissionDescription())
            .setTargetValue(request.getTargetValue().doubleValue())
            .setTargetType(vn.com.grpc.loyalty.entity.TargetType.valueOf(request.getTargetType().name()))
            .setRewardType(RewardType.valueOf(request.getRewardType().name()))
            .setRewardValue(request.getRewardValue() != null ? request.getRewardValue() : "")
            .setPartnerId(partnerId)
            .setStartDate(request.getMissionStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            .setEndDate(request.getMissionEndDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            .setTaskStatus(TaskStatus.valueOf(request.getTaskStatus().name()))
            .setRequestId(request.getRequestId())
            .build();

    log.info("gRPC createMission request: {}", grpcRequest);

    try {
      CreateMissionResponseGrpc response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
          .createMission(grpcRequest);
      String errorCode = response.getResponseInfo().getErrorCode();
      if (!"success".equalsIgnoreCase(errorCode)) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .description(response.getResponseInfo().getMessage())
            .errorCode(errorCode)
            .build();
      }
      log.info("gRPC createMission response: {}", response);
    } catch (BaseException e) {
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

  public GetMissionByIdResponse getMissionById(Long missionId) {
    GetMissionByIdRequest grpcRequest = GetMissionByIdRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
        .setMissionId(missionId)
        .build();

    log.info("gRPC getMissionById request: {}", grpcRequest);

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
      log.info("gRPC getMissionById response: {}", response);
      return response;
    } catch (BaseException e) {
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

  public SearchMissionResponse searchMissions(Long partnerId,
      com.example.voucherservice.constant.RewardType rewardType,
      com.example.voucherservice.constant.TaskStatus taskStatus,
      com.example.voucherservice.constant.MissionStatus missionStatus, Pageable pageable) {

    SearchMissionRequest.Builder builder = SearchMissionRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
        .setPageable(
            vn.com.grpc.loyalty.entity.Pageable.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize()).build());

    if (partnerId != null && partnerId > 0) {
      builder.setPartnerId(partnerId);
    }
    if (rewardType != null) {
      builder.setRewardType(RewardType.valueOf(rewardType.name()));
    }
    if (taskStatus != null) {
      builder.setTaskStatus(TaskStatus.valueOf(taskStatus.name()));
    }
    if (missionStatus != null) {
      String grpcMissionStatusName = "APPROVED".equals(missionStatus.name()) ? "MS_APPROVED" : missionStatus.name();
      builder.setMissionStatus(vn.com.grpc.loyalty.entity.MissionStatus.valueOf(grpcMissionStatusName));
    }

    SearchMissionRequest request = builder.build();
    log.info("gRPC searchMissions request: {}", request);

    try {
      SearchMissionResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
          .searchMission(request);
      if (!"success".equalsIgnoreCase(response.getResponseInfo().getErrorCode())) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .errorCode(response.getResponseInfo().getErrorCode())
            .description(response.getResponseInfo().getMessage())
            .build();
      }
      log.info("gRPC searchMissions response: {}", response);
      return response;
    } catch (BaseException e) {
      throw e;
    } catch (Exception e) {
      log.error("gRPC searchMissions Exception - error: {}", e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to search missions")
          .build();
    }
  }

  public SearchMissionResponse getMissionStats(Long partnerId) {
    SearchMissionRequest.Builder builder = SearchMissionRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
        .setPageable(
            vn.com.grpc.loyalty.entity.Pageable.newBuilder()
                .setPage(0)
                .setSize(Integer.MAX_VALUE).build());

    if (partnerId != null && partnerId > 0) {
      builder.setPartnerId(partnerId);
    }

    SearchMissionRequest request = builder.build();
    log.info("gRPC getMissionStats request: {}", request);

    try {
      SearchMissionResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
          .searchMission(request);
      if (!"success".equalsIgnoreCase(response.getResponseInfo().getErrorCode())) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .errorCode(response.getResponseInfo().getErrorCode())
            .description(response.getResponseInfo().getMessage())
            .build();
      }
      log.info("gRPC getMissionStats response: {}", response);
      return response;
    } catch (BaseException e) {
      throw e;
    } catch (Exception e) {
      log.error("gRPC getMissionStats Exception - error: {}", e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to get mission statistics")
          .build();
    }
  }

  public UpdateMissionStatusResponse updateMissionStatus(Long missionId, com.example.voucherservice.constant.RequestStatus status) {
    UpdateMissionStatusRequest request = UpdateMissionStatusRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
        .setMissionId(missionId)
        .setTaskStatus(TaskStatus.valueOf(status.name()))
        .build();

    log.info("gRPC updateMissionStatus request: {}", request);

    try {
      UpdateMissionStatusResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
          .updateMissionStatus(request);
      if (!"success".equalsIgnoreCase(response.getResponseInfo().getErrorCode())) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .errorCode(response.getResponseInfo().getErrorCode())
            .description(response.getResponseInfo().getMessage())
            .build();
      }
      log.info("gRPC updateMissionStatus response: {}", response);
      return response;
    } catch (BaseException e) {
      throw e;
    } catch (Exception e) {
      log.error("gRPC updateMissionStatus Exception - missionId: {}, error: {}",
          missionId, e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to update mission status: " + missionId)
          .build();
    }
  }

  public GetMissionMonthlyStatsResponse getMissionMonthlyStats(int year, Long partnerId) {
    GetMissionMonthlyStatsRequest.Builder builder = GetMissionMonthlyStatsRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
        .setYear(year);

    if (partnerId != null && partnerId > 0) {
      builder.setPartnerId(partnerId);
    }

    GetMissionMonthlyStatsRequest request = builder.build();
    log.info("gRPC getMissionMonthlyStats request: {}", request);

    try {
      GetMissionMonthlyStatsResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
          .getMissionMonthlyStats(request);
      if (!"success".equalsIgnoreCase(response.getResponseInfo().getErrorCode())) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .errorCode(response.getResponseInfo().getErrorCode())
            .description(response.getResponseInfo().getMessage())
            .build();
      }
      return response;
    } catch (BaseException e) {
      throw e;
    } catch (Exception e) {
      log.error("gRPC getMissionMonthlyStats Exception - error: {}", e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to get mission monthly stats")
          .build();
    }
  }
}
