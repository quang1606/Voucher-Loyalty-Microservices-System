package com.example.identityservice.service.grpc;

import com.example.common.BaseException;
import com.example.identityservice.service.ProfileService;
import com.example.identityservice.utils.GrpcUtils;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.com.grpc.identity.entity.CheckNameStoreRequest;
import vn.com.grpc.identity.entity.CheckNameStoreResponse;
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

    @Override
    public void checkNameStore(CheckNameStoreRequest request, StreamObserver<CheckNameStoreResponse> responseObserver) {
        try {
            boolean exists = profileService.existsByStoreName(request.getNameStore());
            CheckNameStoreResponse response = CheckNameStoreResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                    .setExists(exists)
                    .build();
            responseObserver.onNext(response);
        } catch (BaseException e) {
            log.error("BaseException in checkNameStore: {}", e.getMessage());
            CheckNameStoreResponse response = CheckNameStoreResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            log.error("Exception in checkNameStore: {}", e.getMessage(), e);
            CheckNameStoreResponse response = CheckNameStoreResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build();
            responseObserver.onNext(response);
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getPartner(GetPartnerRequest request, StreamObserver<GetPartnerResponse> responseObserver) {
        try {
            com.example.identityservice.entity.Partner partner = profileService.getPartnerByUserId(request.getUserId());
            GetPartnerResponse response = GetPartnerResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                    .setId(partner.getId())
                    .setUserId(partner.getUserId().toString())
                    .setStoreName(partner.getStoreName())
                    .setPhone(partner.getPhone() != null ? partner.getPhone() : "")
                    .setCategory(partner.getCategory().name())
                    .setTotalRevenue(partner.getTotalRevenue().toPlainString())
                    .setStatus(partner.getStatus().name())
                    .setCreatedBy(partner.getCreatedBy() != null ? partner.getCreatedBy() : "")
                    .setCreatedAt(partner.getCreatedAt() != null ? partner.getCreatedAt().toString() : "")
                    .setUpdatedAt(partner.getUpdatedAt() != null ? partner.getUpdatedAt().toString() : "")
                    .build();
            responseObserver.onNext(response);
        } catch (BaseException e) {
            log.error("BaseException in getPartner: {}", e.getMessage());
            GetPartnerResponse response = GetPartnerResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            log.error("Exception in getPartner: {}", e.getMessage(), e);
            GetPartnerResponse response = GetPartnerResponse.newBuilder()
                    .setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e))
                    .build();
            responseObserver.onNext(response);
        } finally {
            responseObserver.onCompleted();
        }
    }
}
