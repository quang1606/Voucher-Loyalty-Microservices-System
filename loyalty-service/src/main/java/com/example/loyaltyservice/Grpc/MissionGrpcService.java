package com.example.loyaltyservice.Grpc;

import com.example.common.BaseException;
import com.example.loyaltyservice.entity.MissionEntity;
import com.example.loyaltyservice.service.MissionService;
import com.example.loyaltyservice.utils.GrpcUtils;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import vn.com.grpc.loyalty.entity.*;
import vn.com.grpc.loyalty.service.LoyaltyServiceGrpc;

import java.time.ZoneId;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class MissionGrpcService extends LoyaltyServiceGrpc.LoyaltyServiceImplBase {

    private final MissionService missionService;

    @Override
    public void createMission(CreateMissionRequestGrpc request,
        StreamObserver<CreateMissionResponseGrpc> responseObserver) {
        CreateMissionResponseGrpc.Builder responseBuilder = CreateMissionResponseGrpc.newBuilder();
        try {
            log.info("gRPC createMission - requestId: {}, missionName: {}",
                request.getRequestInfo().getRequestId(), request.getMissionName());
            missionService.createMission(request);
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()));
        } catch (BaseException e) {
            log.error("gRPC createMission BaseException - requestId: {}, errorCode: {}, message: {}",
                request.getRequestInfo().getRequestId(), e.getErrorCode(), e.getDescription());
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } catch (Exception e) {
            log.error("gRPC createMission Exception - requestId: {}, error: {}",
                request.getRequestInfo().getRequestId(), e.getMessage(), e);
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } finally {
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateMissionStatus(UpdateMissionStatusRequest request,
        StreamObserver<UpdateMissionStatusResponse> responseObserver) {
        UpdateMissionStatusResponse.Builder responseBuilder = UpdateMissionStatusResponse.newBuilder();
        try {
            log.info("gRPC updateMissionStatus - missionId: {}, newStatus: {}",
                request.getMissionId(), request.getTaskStatus());
            missionService.updateMissionStatus(request.getMissionId(), request.getTaskStatus().name());
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()));
        } catch (BaseException e) {
            log.error("gRPC updateMissionStatus BaseException - missionId: {}, error: {}",
                request.getMissionId(), e.getDescription());
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } catch (Exception e) {
            log.error("gRPC updateMissionStatus Exception - missionId: {}, error: {}",
                request.getMissionId(), e.getMessage(), e);
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } finally {
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getMissionById(GetMissionByIdRequest request,
        StreamObserver<GetMissionByIdResponse> responseObserver) {
        GetMissionByIdResponse.Builder responseBuilder = GetMissionByIdResponse.newBuilder();
        try {
            log.info("gRPC getMissionById - requestId: {}, missionId: {}",
                request.getRequestInfo().getRequestId(), request.getMissionId());

            MissionEntity entity = missionService.getMissionById(request.getMissionId());

            responseBuilder
                .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                .setMissionName(entity.getName())
                .setMissionDescription(entity.getDescription())
                .setTargetValue(entity.getTargetValue().doubleValue())
                .setRewardType(RewardType.valueOf(entity.getRewardType().name()))
                .setRewardValue(entity.getRewardValue())
                .setPartnerId(entity.getPartnerId() != null ? entity.getPartnerId() : 0L)
                .setStartDate(entity.getStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .setEndDate(entity.getEndDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .setTaskStatus(TaskStatus.valueOf(entity.getStatus().name()))
                    .setRequestId(entity.getRequestId());
        } catch (BaseException e) {
            log.error("gRPC getMissionById BaseException - missionId: {}, errorCode: {}, message: {}",
                request.getMissionId(), e.getErrorCode(), e.getDescription());
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } catch (Exception e) {
            log.error("gRPC getMissionById Exception - missionId: {}, error: {}",
                request.getMissionId(), e.getMessage(), e);
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } finally {
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void searchMission(SearchMissionRequest request,
        StreamObserver<SearchMissionResponse> responseObserver) {
        SearchMissionResponse.Builder responseBuilder = SearchMissionResponse.newBuilder();
        try {
            log.info("gRPC searchMission - partnerId: {}, partnerId: {}, rewardType: {}, status: {}",
                request.getRequestInfo().getRequestId(), request.getPartnerId(),
                request.getRewardType(), request.getTaskStatus());

            Page<MissionEntity> page = missionService.searchMission(request);

            responseBuilder
                .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                .setTotalElements((int) page.getTotalElements())
                .setTotalPages(page.getTotalPages());

            for (MissionEntity entity : page.getContent()) {
                MissionInfo missionInfo = MissionInfo.newBuilder()
                    .setMissionId(entity.getId())
                    .setMissionName(entity.getName())
                    .setMissionDescription(entity.getDescription())
                    .setTargetValue(entity.getTargetValue().doubleValue())
                    .setRewardType(RewardType.valueOf(entity.getRewardType().name()))
                    .setRewardValue(entity.getRewardValue())
                    .setPartnerId(entity.getPartnerId() != null ? entity.getPartnerId() : 0L)
                    .setStartDate(entity.getStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .setEndDate(entity.getEndDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .setTaskStatus(TaskStatus.valueOf(entity.getStatus().name()))
                        .setRequestId(entity.getRequestId())
                    .build();
                responseBuilder.addMissions(missionInfo);
            }
        } catch (BaseException e) {
            log.error("gRPC searchMission BaseException - requestId: {}, errorCode: {}, message: {}",
                request.getRequestInfo().getRequestId(), e.getErrorCode(), e.getDescription());
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } catch (Exception e) {
            log.error("gRPC searchMission Exception - requestId: {}, error: {}",
                request.getRequestInfo().getRequestId(), e.getMessage(), e);
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } finally {
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        }
    }


}
