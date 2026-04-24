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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.loyalty.entity.CreateMissionResponseGrpc;
import vn.com.grpc.loyalty.entity.GetMissionByIdRequest;
import vn.com.grpc.loyalty.entity.GetMissionByIdResponse;
import vn.com.grpc.loyalty.entity.RewardType;
import vn.com.grpc.loyalty.entity.SearchMissionRequest;
import vn.com.grpc.loyalty.entity.SearchMissionResponse;
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
    vn.com.grpc.loyalty.entity.CreateMissionRequestGrpc grpcRequest =
        vn.com.grpc.loyalty.entity.CreateMissionRequestGrpc.newBuilder()
            .setRequestInfo(grpcUtils.builderRequestInfo())
            .setMissionName(request.getMissionName())
            .setMissionDescription(request.getMissionDescription())
            .setTargetValue(request.getTargetValue().doubleValue())
            .setRewardType(mapRewardType(request.getRewardType()))
            .setRewardValue(request.getRewardValue())
            .setPartnerId(request.getPartnerId() != null ? request.getPartnerId() : 0L)
            .setStartDate(request.getMissionStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            .setEndDate(request.getMissionEndDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            .setTaskStatus(TaskStatus.valueOf(request.getTaskStatus().name()))
            .build();

    log.info("gRPC createMission request - missionName: {}, status: {}",
        request.getMissionName(), request.getTaskStatus());

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

  public SearchMissionResponse searchMissions(String nameStore, com.example.voucherservice.constant.RewardType rewardType,
      com.example.voucherservice.constant.TaskStatus taskStatus, Pageable pageable) {
    SearchMissionRequest request = SearchMissionRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
        .setPartnerId(nameStore)
        .setRewardType(RewardType.valueOf(rewardType.name()))
        .setTaskStatus(TaskStatus.valueOf(taskStatus.name()))
        .setPageable(
            vn.com.grpc.loyalty.entity.Pageable.newBuilder().setPage(pageable.getPageSize())
                .setPage(pageable.getPageNumber()).build())
        .build();
    log.info("gRPC searchMissions request - partnerId: {} - rewardType: {} - taskStatus: {}", nameStore,rewardType,taskStatus);
    try {
      SearchMissionResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
          .searchMission(request);
      log.info("gRPC response : {}", response);
      response.getResponseInfo().getErrorCode();
      if (!"success".equalsIgnoreCase(
                response.getResponseInfo().getErrorCode())
      ) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .errorCode(response.getResponseInfo().getErrorCode())
            .description(response.getResponseInfo().getMessage())
            .build();
      }
      return response;
    } catch (Exception e) {
      log.error("gRPC searchMissions Exception - error: {}", e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to search missions")
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


}
