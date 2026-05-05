package com.example.identityservice.service.grpc;

import com.example.common.BaseException;
import com.example.identityservice.entity.Partner;
import com.example.identityservice.service.ProfileService;
import com.example.identityservice.utils.GrpcUtils;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.com.grpc.identity.entity.CheckNameStoreRequest;
import vn.com.grpc.identity.entity.CheckNameStoreResponse;
import vn.com.grpc.identity.entity.GetPartnerByNameRequest;
import vn.com.grpc.identity.entity.GetPartnerByNameResponse;
import vn.com.grpc.identity.entity.GetPartnerRequest;
import vn.com.grpc.identity.entity.GetPartnerResponse;
import vn.com.grpc.identity.entity.IdentityRequest;
import vn.com.grpc.identity.entity.IdentityResponse;
import vn.com.grpc.identity.service.IdentityServiceGrpc;

@GrpcService
@Slf4j
@AllArgsConstructor
public class IdentityGrpcService extends IdentityServiceGrpc.IdentityServiceImplBase {
    private final ProfileService profileService;

    @Override
    public void getIdentity(IdentityRequest request, StreamObserver<IdentityResponse> responseObserver) {
        try {
            log.info("gRPC getIdentity request: {}", request);
            String nameStore = profileService.getNameStore(request.getPartnerId());
            IdentityResponse response = IdentityResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                    .setNameStore(nameStore)
                    .build();
            log.info("gRPC getIdentity response: {}", response);
            responseObserver.onNext(response);
        } catch (BaseException e) {
            log.error("BaseException in getIdentity: {}", e.getMessage());
            responseObserver.onNext(IdentityResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build());
        } catch (Exception e) {
            log.error("Exception in getIdentity: {}", e.getMessage(), e);
            responseObserver.onNext(IdentityResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void checkNameStore(CheckNameStoreRequest request, StreamObserver<CheckNameStoreResponse> responseObserver) {
        try {
            log.info("gRPC checkNameStore request: {}", request);
            boolean exists = profileService.existsByStoreName(request.getNameStore());
            CheckNameStoreResponse response = CheckNameStoreResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                    .setExists(exists)
                    .build();
            log.info("gRPC checkNameStore response: {}", response);
            responseObserver.onNext(response);
        } catch (BaseException e) {
            log.error("BaseException in checkNameStore: {}", e.getMessage());
            responseObserver.onNext(CheckNameStoreResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build());
        } catch (Exception e) {
            log.error("Exception in checkNameStore: {}", e.getMessage(), e);
            responseObserver.onNext(CheckNameStoreResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getPartner(GetPartnerRequest request, StreamObserver<GetPartnerResponse> responseObserver) {
        try {
            log.info("gRPC getPartner request: {}", request);
            Partner partner = profileService.getPartnerByUserId(request.getUserId());
            GetPartnerResponse response = GetPartnerResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                    .setId(partner.getId())
                    .setUserId(partner.getUserId().toString())
                    .setStoreName(partner.getStoreName())
                    .build();
            log.info("gRPC getPartner response: {}", response);
            responseObserver.onNext(response);
        } catch (BaseException e) {
            log.error("BaseException in getPartner: {}", e.getMessage());
            responseObserver.onNext(GetPartnerResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build());
        } catch (Exception e) {
            log.error("Exception in getPartner: {}", e.getMessage(), e);
            responseObserver.onNext(GetPartnerResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getPartnerByName(GetPartnerByNameRequest request, StreamObserver<GetPartnerByNameResponse> responseObserver) {
        try {
            log.info("gRPC getPartnerByName request: {}", request);
            Partner partner = profileService.getPartnerByStoreName(request.getStoreName());
            GetPartnerByNameResponse response = GetPartnerByNameResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                    .setId(partner.getId())
                    .setUserId(partner.getUserId().toString())
                    .setStoreName(partner.getStoreName())
                    .build();
            log.info("gRPC getPartnerByName response: {}", response);
            responseObserver.onNext(response);
        } catch (BaseException e) {
            log.error("BaseException in getPartnerByName: {}", e.getMessage());
            responseObserver.onNext(GetPartnerByNameResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build());
        } catch (Exception e) {
            log.error("Exception in getPartnerByName: {}", e.getMessage(), e);
            responseObserver.onNext(GetPartnerByNameResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build());
        } finally {
            responseObserver.onCompleted();
        }
    }
}
