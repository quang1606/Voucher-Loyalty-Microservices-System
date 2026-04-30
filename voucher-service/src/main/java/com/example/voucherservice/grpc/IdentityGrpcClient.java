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
  private IdentityServiceGrpc.IdentityServiceBlockingStub stub;

  public String getNameStore(String UserId) {
    IdentityRequest request = IdentityRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
        .setPartnerId(UserId)
        .build();

    log.info("gRPC getNameStore request - partnerId: {}", UserId);

    try {
      IdentityResponse response = stub.withDeadlineAfter(30, TimeUnit.MILLISECONDS).getIdentity(request);
      log.info("gRPC getNameStore response - partnerId: {}, nameStore: {}",
              UserId, response.getNameStore());
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
      throw e;
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

    log.info("gRPC checkNameStore request - nameStore: {}", nameStore);

    try {
      CheckNameStoreResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
          .checkNameStore(request);
      log.info("gRPC checkNameStore response - nameStore: {}, result: {}",
          nameStore, response.getExists());
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

    log.info("gRPC getPartner request - userId: {}", userId);

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
      log.info("gRPC getPartner response - userId: {}, storeName: {}", userId, response.getStoreName());
      return response;
    } catch (BaseException e) {
      throw e;
    } catch (Exception e) {
      log.error("gRPC getPartner Exception - userId: {}, error: {}", userId, e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to get partner for userId: " + userId)
          .build();
    }
  }
}
