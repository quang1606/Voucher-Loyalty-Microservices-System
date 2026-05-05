package com.example.voucherservice.grpc;

import com.example.common.BaseException;
import com.example.voucherservice.utils.GrpcUtils;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.identity.entity.CheckNameStoreRequest;
import vn.com.grpc.identity.entity.CheckNameStoreResponse;
import vn.com.grpc.identity.entity.GetPartnerByNameRequest;
import vn.com.grpc.identity.entity.GetPartnerByNameResponse;
import vn.com.grpc.identity.entity.GetPartnerRequest;
import vn.com.grpc.identity.entity.GetPartnerResponse;
import vn.com.grpc.identity.service.IdentityServiceGrpc;
import vn.com.grpc.identity.entity.IdentityRequest;
import vn.com.grpc.identity.entity.IdentityResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityGrpcClient {

  private final GrpcUtils grpcUtils;

  @GrpcClient("identity-service")
  private  IdentityServiceGrpc.IdentityServiceBlockingStub stub;

  public String getNameStore(String UserId) {
    IdentityRequest request = IdentityRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
        .setPartnerId(UserId)
        .build();

    log.info("gRPC getNameStore request: {}", request);

    try {
      IdentityResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS).getIdentity(request);
      log.info("gRPC getNameStore response: {}", response);
      String errorCode = response.getResponseInfo().getErrorCode();
      if (!"success".equalsIgnoreCase(errorCode)) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .description(response.getResponseInfo().getMessage())
            .errorCode(errorCode)
            .build();
      }
      return response.getNameStore();
    } catch (BaseException e) {
      log.error("gRPC getNameStore BaseException - partnerId: {}, error: {}",
              UserId, e.getDescription());
      throw BaseException.builder()
          .httpStatus(e.getHttpStatus())
          .errorCode(e.getErrorCode())
          .description(e.getDescription())
          .build();
    } catch (Exception e) {
      log.error("gRPC getNameStore Exception - partnerId: {}, error: {}",
              UserId, e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to get name store for partnerId: " + UserId)
          .build();
    }
  }
  public CheckNameStoreResponse checkNameStore(String nameStore) {
    CheckNameStoreRequest request = CheckNameStoreRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
            .setNameStore(nameStore)
        .build();

    log.info("gRPC checkNameStore request: {}", request);

    try {
      CheckNameStoreResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
          .checkNameStore(request);
      log.info("gRPC checkNameStore response: {}", response);
      return response;
    } catch (Exception e) {
      log.error("gRPC checkNameStore Exception - nameStore: {}, error: {}",
          nameStore, e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to check name store: " + nameStore)
          .build();
    }
  }

  public GetPartnerResponse getPartner(String userId) {
    GetPartnerRequest request = GetPartnerRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
        .setUserId(userId)
        .build();

    log.info("gRPC getPartner request: {}", request);

    try {
      GetPartnerResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS).getPartner(request);
      String errorCode = response.getResponseInfo().getErrorCode();
      if (!"success".equalsIgnoreCase(errorCode)) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .description(response.getResponseInfo().getMessage())
            .errorCode(errorCode)
            .build();
      }
      log.info("gRPC getPartner response: {}", response);
      return response;
    } catch (BaseException e) {
      throw BaseException.builder()
          .httpStatus(e.getHttpStatus())
          .errorCode(e.getErrorCode())
          .description(e.getDescription())
          .build();
    } catch (Exception e) {
      log.error("gRPC getPartner Exception - userId: {}, error: {}", userId, e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to get partner for userId: " + userId)
          .build();
    }
  }

  public GetPartnerByNameResponse getPartnerByStoreName(String storeName) {
    GetPartnerByNameRequest request = GetPartnerByNameRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
        .setStoreName(storeName)
        .build();

    log.info("gRPC getPartnerByStoreName request: {}", request);

    try {
      GetPartnerByNameResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
          .getPartnerByName(request);
      String errorCode = response.getResponseInfo().getErrorCode();
      if (!"success".equalsIgnoreCase(errorCode)) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .description(response.getResponseInfo().getMessage())
            .errorCode(errorCode)
            .build();
      }
      log.info("gRPC getPartnerByStoreName response: {}", response);
      return response;
    } catch (BaseException e) {
      throw BaseException.builder()
          .httpStatus(e.getHttpStatus())
          .errorCode(e.getErrorCode())
          .description(e.getDescription())
          .build();
    } catch (Exception e) {
      log.error("gRPC getPartnerByStoreName Exception - storeName: {}, error: {}",
          storeName, e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to get partner by store name: " + storeName)
          .build();
    }
  }
}
