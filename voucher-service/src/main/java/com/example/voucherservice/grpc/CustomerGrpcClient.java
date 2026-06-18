package com.example.voucherservice.grpc;

import com.example.common.BaseException;
import com.example.voucherservice.utils.GrpcUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.customer.entity.GetVoucherUsageStatsRequest;
import vn.com.grpc.customer.entity.GetVoucherUsageStatsResponse;
import vn.com.grpc.customer.service.CustomerServiceGrpc;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerGrpcClient {

    private final GrpcUtils grpcUtils;

    @GrpcClient("customer-service")
    private CustomerServiceGrpc.CustomerServiceBlockingStub stub;

    public GetVoucherUsageStatsResponse getVoucherUsageStats(List<String> requestIds, int month, int year, String requestIdFilter) {
        GetVoucherUsageStatsRequest.Builder builder = GetVoucherUsageStatsRequest.newBuilder()
                .setRequestInfo(grpcUtils.builderRequestInfo())
                .addAllRequestIds(requestIds)
                .setMonth(month)
                .setYear(year);

        if (requestIdFilter != null && !requestIdFilter.isEmpty()) {
            builder.setRequestIdFilter(requestIdFilter);
        }

        GetVoucherUsageStatsRequest request = builder.build();

        log.info("gRPC getVoucherUsageStats request: month={}, year={}, requestIds={}, filter={}", month, year, requestIds.size(), requestIdFilter);

        try {
            GetVoucherUsageStatsResponse response = stub.withDeadlineAfter(30, TimeUnit.SECONDS)
                    .getVoucherUsageStats(request);
            log.info("gRPC getVoucherUsageStats response: statsCount={}, responseInfo={}", response.getStatsCount(), response.getResponseInfo());
            if (!"success".equalsIgnoreCase(response.getResponseInfo().getErrorCode())) {
                throw BaseException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .errorCode(response.getResponseInfo().getErrorCode())
                        .description(response.getResponseInfo().getMessage())
                        .build();
            }
            return response;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("gRPC getVoucherUsageStats Exception - error: {}", e.getMessage(), e);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode("GRPC_ERROR")
                    .description("Failed to get voucher usage stats")
                    .build();
        }
    }
}
