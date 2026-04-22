package com.example.voucherservice.grpc;

import com.example.common.BaseException;
import com.example.voucherservice.utils.GrpcUtils;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.fcc.grpc.identity.service.IdentityServiceGrpc;
import vn.com.grpc.identity.entity.IdentityRequest;
import vn.com.grpc.identity.entity.IdentityResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityGrpcClient {

  private final GrpcUtils grpcUtils;

  @GrpcClient("identity-service")
  private IdentityServiceGrpc.IdentityServiceBlockingStub stub;

  public String getNameStore(String partnerId) {
    IdentityRequest request = IdentityRequest.newBuilder()
        .setRequestInfo(grpcUtils.builderRequestInfo())
        .setPartnerId(partnerId)
        .build();

    log.info("gRPC getNameStore request - partnerId: {}", partnerId);

    try {
      IdentityResponse response = stub.withDeadlineAfter(30, TimeUnit.MILLISECONDS).getIdentity(request);
      log.info("gRPC getNameStore response - partnerId: {}, nameStore: {}",
          partnerId, response.getNameStore());
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
          partnerId, e.getDescription());
      throw e;
    } catch (Exception e) {
      log.error("gRPC getNameStore Exception - partnerId: {}, error: {}",
          partnerId, e.getMessage(), e);
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .errorCode("GRPC_ERROR")
          .description("Failed to get name store for partnerId: " + partnerId)
          .build();
    }
  }
}
