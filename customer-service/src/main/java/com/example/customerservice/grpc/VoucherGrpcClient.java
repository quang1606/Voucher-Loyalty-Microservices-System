package com.example.customerservice.grpc;

import com.example.common.BaseException;
import com.example.customerservice.utils.GrpcUtils;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.voucher.entity.*;
import vn.com.grpc.voucher.service.VoucherGrpcServiceGrpc;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherGrpcClient {

    private final GrpcUtils grpcUtils;

    @GrpcClient("voucher-service")
    private VoucherGrpcServiceGrpc.VoucherGrpcServiceBlockingStub stub;

    public SearchVoucherResponse searchVouchers(String customerTier, int page, int size, String sort) {
        SearchVoucherRequest request = SearchVoucherRequest.newBuilder()
                .setRequestInfo(grpcUtils.builderRequestInfo())
                .setCustomerTier(customerTier)
                .setPageable(VoucherPageable.newBuilder()
                        .setPage(page)
                        .setSize(size)
                        .setSort(sort != null ? sort : "createdAt,desc")
                        .build())
                .build();

        log.info("gRPC searchVouchers request: {}", request);
        try {
            SearchVoucherResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
                    .searchVoucher(request);
            String errorCode = response.getResponseInfo().getErrorCode();
            if (!"success".equalsIgnoreCase(errorCode)) {
                throw BaseException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .errorCode(errorCode)
                        .description(response.getResponseInfo().getMessage())
                        .build();
            }
            log.info("gRPC searchVouchers response: {}", response);
            return response;
        } catch (BaseException e) {
            log.error("gRPC searchVouchers BaseException - error: {}", e.getDescription());
            throw BaseException.builder()
                    .httpStatus(e.getHttpStatus())
                    .errorCode(e.getErrorCode())
                    .description(e.getDescription())
                    .build();
        } catch (Exception e) {
            log.error("gRPC searchVouchers Exception - error: {}", e.getMessage(), e);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode("GRPC_ERROR")
                    .description("Failed to search vouchers")
                    .build();
        }
    }

    public GetVoucherByIdResponse getVoucherById(Long voucherId) {
        GetVoucherByIdRequest request = GetVoucherByIdRequest.newBuilder()
                .setRequestInfo(grpcUtils.builderRequestInfo())
                .setVoucherId(voucherId)
                .build();

        log.info("gRPC getVoucherById request: {}", request);
        try {
            GetVoucherByIdResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
                    .getVoucherById(request);
            String errorCode = response.getResponseInfo().getErrorCode();
            if (!"success".equalsIgnoreCase(errorCode)) {
                throw BaseException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .errorCode(errorCode)
                        .description(response.getResponseInfo().getMessage())
                        .build();
            }
            log.info("gRPC getVoucherById response: {}", response);
            return response;
        } catch (BaseException e) {
            log.error("gRPC getVoucherById BaseException - voucherId: {}, error: {}", voucherId, e.getDescription());
            throw BaseException.builder()
                    .httpStatus(e.getHttpStatus())
                    .errorCode(e.getErrorCode())
                    .description(e.getDescription())
                    .build();
        } catch (Exception e) {
            log.error("gRPC getVoucherById Exception - voucherId: {}, error: {}", voucherId, e.getMessage(), e);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode("GRPC_ERROR")
                    .description("Failed to get voucher by id: " + voucherId)
                    .build();
        }
    }

    public GetVoucherByRequestIdResponse getVoucherByRequestId(String requestId) {
        GetVoucherByRequestIdRequest request = GetVoucherByRequestIdRequest.newBuilder()
                .setRequestInfo(grpcUtils.builderRequestInfo())
                .setRequestId(requestId)
                .build();

        log.info("gRPC getVoucherByRequestId request: {}", request);
        try {
            GetVoucherByRequestIdResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
                    .getVoucherByRequestId(request);
            String errorCode = response.getResponseInfo().getErrorCode();
            if (!"success".equalsIgnoreCase(errorCode)) {
                throw BaseException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .errorCode(errorCode)
                        .description(response.getResponseInfo().getMessage())
                        .build();
            }
            log.info("gRPC getVoucherByRequestId response: {}", response);
            return response;
        } catch (BaseException e) {
            log.error("gRPC getVoucherByRequestId BaseException - requestId: {}, error: {}", requestId, e.getDescription());
            throw BaseException.builder()
                    .httpStatus(e.getHttpStatus())
                    .errorCode(e.getErrorCode())
                    .description(e.getDescription())
                    .build();
        } catch (Exception e) {
            log.error("gRPC getVoucherByRequestId Exception - requestId: {}, error: {}", requestId, e.getMessage(), e);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode("GRPC_ERROR")
                    .description("Failed to get voucher by requestId: " + requestId)
                    .build();
        }
    }

    public GetMockInvoicesResponse getMockInvoices(String nameStore, String title, int page, int size) {
        GetMockInvoicesRequest request = GetMockInvoicesRequest.newBuilder()
                .setRequestInfo(grpcUtils.builderRequestInfo())
                .setNameStore(nameStore != null ? nameStore : "")
                .setTitle(title != null ? title : "")
                .setPage(page)
                .setSize(size)
                .build();

        log.info("gRPC getMockInvoices request: {}", request);
        try {
            GetMockInvoicesResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
                    .getMockInvoices(request);
            String errorCode = response.getResponseInfo().getErrorCode();
            if (!"success".equalsIgnoreCase(errorCode)) {
                throw BaseException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .errorCode(errorCode)
                        .description(response.getResponseInfo().getMessage())
                        .build();
            }
            log.info("gRPC getMockInvoices response: {}", response);
            return response;
        } catch (BaseException e) {
            log.error("gRPC getMockInvoices BaseException - error: {}", e.getDescription());
            throw BaseException.builder()
                    .httpStatus(e.getHttpStatus())
                    .errorCode(e.getErrorCode())
                    .description(e.getDescription())
                    .build();
        } catch (Exception e) {
            log.error("gRPC getMockInvoices Exception - error: {}", e.getMessage(), e);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode("GRPC_ERROR")
                    .description("Failed to get mock invoices")
                    .build();
        }
    }
}