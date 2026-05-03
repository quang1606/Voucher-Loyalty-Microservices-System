package com.example.identityservice.grpc;

import com.example.common.BaseException;
import com.example.identityservice.utils.GrpcUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.customer.entity.CreateCustomerProfileRequest;
import vn.com.grpc.customer.entity.CreateCustomerProfileResponse;
import vn.com.grpc.customer.service.CustomerServiceGrpc;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerGrpcClient {

    private final GrpcUtils grpcUtils;

    @GrpcClient("customer-service")
    private CustomerServiceGrpc.CustomerServiceBlockingStub stub;

    public void createCustomerProfile(String userId, String fullName) {
        CreateCustomerProfileRequest request = CreateCustomerProfileRequest.newBuilder()
                .setRequestInfo(grpcUtils.builderRequestInfo())
                .setUserId(userId)
                .setFullName(fullName)
                .build();

        log.info("gRPC createCustomerProfile request - userId: {}", userId);

        try {
            CreateCustomerProfileResponse response = stub
                    .withDeadlineAfter(30, TimeUnit.SECONDS)
                    .createCustomerProfile(request);

            String errorCode = response.getResponseInfo().getErrorCode();
            if (!"success".equalsIgnoreCase(errorCode)) {
                throw BaseException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .description(response.getResponseInfo().getMessage())
                        .errorCode(errorCode)
                        .build();
            }
            log.info("gRPC createCustomerProfile success - userId: {}, response: {}", userId,response);
        } catch (BaseException e) {
            log.error("gRPC createCustomerProfile BaseException - userId: {}, error: {}", userId, e.getDescription());
            throw e;
        } catch (Exception e) {
            log.error("gRPC createCustomerProfile Exception - userId: {}, error: {}", userId, e.getMessage(), e);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode("GRPC_ERROR")
                    .description("Failed to create customer profile for userId: " + userId)
                    .build();
        }
    }
}
