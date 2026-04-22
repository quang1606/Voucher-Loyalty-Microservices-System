package com.example.identityservice.service.grpc;

import com.example.common.BaseException;
import com.example.identityservice.service.ProfileService;
import com.example.identityservice.utils.GrpcUtils;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.com.fcc.grpc.identity.service.IdentityServiceGrpc;
import vn.com.grpc.identity.entity.IdentityRequest;
import vn.com.grpc.identity.entity.IdentityResponse;

@GrpcService
@Slf4j
@AllArgsConstructor
public class IdentityGrpcService extends IdentityServiceGrpc.IdentityServiceImplBase {
    private final ProfileService profileService;

    @Override
    public void getIdentity(IdentityRequest request, StreamObserver<IdentityResponse> responseObserver) {
        try {
            String nameStore = profileService.getNameStore(request.getPartnerId());
            IdentityResponse response = IdentityResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                    .setNameStore(nameStore)
                    .build();
            responseObserver.onNext(response);
        } catch (BaseException e) {
            log.error("BaseException in getIdentity: {}", e.getMessage());
            IdentityResponse response = IdentityResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            log.error("Exception in getIdentity: {}", e.getMessage(), e);
            IdentityResponse response = IdentityResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build();
            responseObserver.onNext(response);

        } finally {
          responseObserver.onCompleted();
        }
    }
}
