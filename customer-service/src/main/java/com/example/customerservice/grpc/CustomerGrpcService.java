package com.example.customerservice.grpc;

import com.example.common.BaseException;
import com.example.customerservice.entity.CustomerProfile;
import com.example.customerservice.repository.CustomerProfileRepository;
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

    private final CustomerProfileRepository customerProfileRepository;

    @Override
    public void createCustomerProfile(CreateCustomerProfileRequest request,
                                       StreamObserver<CreateCustomerProfileResponse> responseObserver) {
        try {
            UUID userId = UUID.fromString(request.getUserId());
            if (customerProfileRepository.existsByUserId(userId)) {
                log.warn("CustomerProfile already exists for userId: {}", userId);
            } else {
                CustomerProfile profile = new CustomerProfile();
                profile.setUserId(userId);
                profile.setFullName(request.getFullName());
                customerProfileRepository.save(profile);
                log.info("Created CustomerProfile for userId: {}", userId);
            }
            responseObserver.onNext(CreateCustomerProfileResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                    .build());
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
