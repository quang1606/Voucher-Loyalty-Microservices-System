package com.example.customerservice.grpc;

import com.example.common.BaseException;
import com.example.customerservice.service.CustomerProfileService;
import com.example.customerservice.utils.GrpcUtils;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.com.grpc.customer.entity.CreateCustomerProfileRequest;
import vn.com.grpc.customer.entity.CreateCustomerProfileResponse;
import vn.com.grpc.customer.service.CustomerServiceGrpc;

import java.util.UUID;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CustomerGrpcService extends CustomerServiceGrpc.CustomerServiceImplBase {

    private final CustomerProfileService customerProfileService;

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
}
