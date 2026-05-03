package com.example.customerservice.grpc;

import com.example.common.BaseException;
import com.example.customerservice.utils.GrpcUtils;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.voucher.entity.SearchVoucherRequest;
import vn.com.grpc.voucher.entity.SearchVoucherResponse;
import vn.com.grpc.voucher.entity.VoucherPageable;
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

        log.info("gRPC searchVouchers - customerTier: {}, page: {}, size: {}", customerTier, page, size);
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
            log.info("call gRPC response: {}", response);
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
}
