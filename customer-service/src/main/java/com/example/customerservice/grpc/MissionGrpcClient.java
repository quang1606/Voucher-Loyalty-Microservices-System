package com.example.customerservice.grpc;

import com.example.common.BaseException;
import com.example.customerservice.utils.GrpcUtils;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.loyalty.entity.GetMissionByIdRequest;
import vn.com.grpc.loyalty.entity.GetMissionByIdResponse;
import vn.com.grpc.loyalty.entity.Pageable;
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

    public SearchMissionResponse getMissions(int page, int size) {
        SearchMissionRequest request = SearchMissionRequest.newBuilder()
                .setRequestInfo(grpcUtils.builderRequestInfo())
                .setTaskStatus(TaskStatus.FINISH) // Mặc định FINISH
                .setPageable(Pageable.newBuilder()
                        .setPage(page)
                        .setSize(size)
                        .build())
                .build();

        log.info("gRPC getMissions request: {}", request);
        try {
            SearchMissionResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
                    .searchMission(request);
            String errorCode = response.getResponseInfo().getErrorCode();
            if (!"success".equalsIgnoreCase(errorCode)) {
                throw BaseException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .errorCode(errorCode)
                        .description(response.getResponseInfo().getMessage())
                        .build();
            }
            log.info("gRPC getMissions response: {}", response);
            return response;
        } catch (BaseException e) {
            log.error("gRPC getMissions BaseException - error: {}", e.getDescription());
            throw BaseException.builder()
                    .httpStatus(e.getHttpStatus())
                    .errorCode(e.getErrorCode())
                    .description(e.getDescription())
                    .build();
        } catch (Exception e) {
            log.error("gRPC getMissions Exception - error: {}", e.getMessage(), e);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode("GRPC_ERROR")
                    .description("Failed to get missions")
                    .build();
        }
    }

    public GetMissionByIdResponse getMissionById(Long missionId) {
        GetMissionByIdRequest request = GetMissionByIdRequest.newBuilder()
                .setRequestInfo(grpcUtils.builderRequestInfo())
                .setMissionId(missionId)
                .build();

        log.info("gRPC getMissionById request: {}", request);
        try {
            GetMissionByIdResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
                    .getMissionById(request);
            String errorCode = response.getResponseInfo().getErrorCode();
            if (!"success".equalsIgnoreCase(errorCode)) {
                throw BaseException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .errorCode(errorCode)
                        .description(response.getResponseInfo().getMessage())
                        .build();
            }
            log.info("gRPC getMissionById response: {}", response);
            return response;
        } catch (BaseException e) {
            log.error("gRPC getMissionById BaseException - missionId: {}, error: {}", missionId, e.getDescription());
            throw BaseException.builder()
                    .httpStatus(e.getHttpStatus())
                    .errorCode(e.getErrorCode())
                    .description(e.getDescription())
                    .build();
        } catch (Exception e) {
            log.error("gRPC getMissionById Exception - missionId: {}, error: {}", missionId, e.getMessage(), e);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode("GRPC_ERROR")
                    .description("Failed to get mission by id: " + missionId)
                    .build();
        }
    }
}