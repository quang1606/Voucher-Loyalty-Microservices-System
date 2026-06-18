package com.example.customerservice.grpc;

import com.example.common.BaseException;
import com.example.customerservice.dto.response.VoucherUsageStatsProjection;
import com.example.customerservice.repository.TransactionRepository;
import com.example.customerservice.service.CustomerProfileService;
import com.example.customerservice.utils.GrpcUtils;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.com.grpc.customer.entity.*;
import vn.com.grpc.customer.service.CustomerServiceGrpc;

import java.util.List;
import java.util.UUID;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CustomerGrpcService extends CustomerServiceGrpc.CustomerServiceImplBase {

    private final CustomerProfileService customerProfileService;
    private final TransactionRepository transactionRepository;

    @Override
    public void createCustomerProfile(CreateCustomerProfileRequest request,
                                       StreamObserver<CreateCustomerProfileResponse> responseObserver) {
        try {
            log.info("gRPC createCustomerProfile request: {}", request);
            customerProfileService.createCustomerProfile(
                    UUID.fromString(request.getUserId()), request.getFullName());
            CreateCustomerProfileResponse response = CreateCustomerProfileResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                    .build();
            log.info("gRPC createCustomerProfile response: {}", response);
            responseObserver.onNext(response);
        } catch (BaseException e) {
            log.error("BaseException in createCustomerProfile: {}", e.getDescription());
            responseObserver.onNext(CreateCustomerProfileResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build());
        } catch (Exception e) {
            log.error("Exception in createCustomerProfile: {}", e.getMessage(), e);
            responseObserver.onNext(CreateCustomerProfileResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getVoucherUsageStats(GetVoucherUsageStatsRequest request,
                                     StreamObserver<GetVoucherUsageStatsResponse> responseObserver) {
        GetVoucherUsageStatsResponse.Builder responseBuilder = GetVoucherUsageStatsResponse.newBuilder();
        try {
            log.info("gRPC getVoucherUsageStats request: {}", request);

            List<String> requestIds = request.getRequestIdsList();
            int month = request.getMonth();
            int year = request.getYear();

            // Filter by specific requestId if provided
            String requestIdFilter = request.getRequestIdFilter();
            if (requestIdFilter != null && !requestIdFilter.isEmpty()) {
                requestIds = requestIds.stream()
                        .filter(id -> id.equals(requestIdFilter))
                        .toList();
            }

            if (requestIds.isEmpty()) {
                responseBuilder.setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()));
                responseObserver.onNext(responseBuilder.build());
                responseObserver.onCompleted();
                return;
            }

            List<VoucherUsageStatsProjection> stats = transactionRepository.getUsageStatsByRequestIds(
                    requestIds, month, year);

            for (VoucherUsageStatsProjection stat : stats) {
                responseBuilder.addStats(VoucherUsageStatsItem.newBuilder()
                        .setRequestId(stat.getRequestId())
                        .setVoucherCode(stat.getVoucherCode() != null ? stat.getVoucherCode() : "")
                        .setUsedCount(stat.getUsedCount())
                        .setTotalDiscountAmount(stat.getTotalDiscount().toPlainString())
                        .build());
            }

            responseBuilder.setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()));

            GetVoucherUsageStatsResponse response = responseBuilder.build();

        } catch (BaseException e) {
            log.error("BaseException in getVoucherUsageStats: {}", e.getDescription());
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } catch (Exception e) {
            log.error("Exception in getVoucherUsageStats: {}", e.getMessage(), e);
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } finally {
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        }
    }
}
