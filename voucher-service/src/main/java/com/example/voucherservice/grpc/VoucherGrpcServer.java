package com.example.voucherservice.grpc;

import com.example.common.BaseException;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.repository.VoucherRepository;
import com.example.voucherservice.service.VoucherService;
import com.example.voucherservice.utils.GrpcUtils;
import io.grpc.stub.StreamObserver;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import vn.com.grpc.voucher.entity.GetVoucherByIdRequest;
import vn.com.grpc.voucher.entity.GetVoucherByIdResponse;
import vn.com.grpc.voucher.entity.GetVoucherByRequestIdRequest;
import vn.com.grpc.voucher.entity.GetVoucherByRequestIdResponse;
import vn.com.grpc.voucher.entity.SearchVoucherRequest;
import vn.com.grpc.voucher.entity.SearchVoucherResponse;
import vn.com.grpc.voucher.entity.VoucherDetail;
import vn.com.grpc.voucher.entity.VoucherInfo;
import vn.com.grpc.voucher.entity.VoucherRequestDetail;
import vn.com.grpc.voucher.service.VoucherGrpcServiceGrpc;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class VoucherGrpcServer extends VoucherGrpcServiceGrpc.VoucherGrpcServiceImplBase {

    private final VoucherService voucherService;
    private final VoucherRepository voucherRepository;

    @Override
    public void searchVoucher(SearchVoucherRequest request,
                              StreamObserver<SearchVoucherResponse> responseObserver) {
        SearchVoucherResponse.Builder responseBuilder = SearchVoucherResponse.newBuilder();
        try {
            log.info("gRPC searchVoucher - requestId: {}, customerTier: {}",
                    request.getRequestInfo().getRequestId(), request.getCustomerTier());

            int page = request.getPageable().getPage();
            int size = request.getPageable().getSize();
            Sort sort = parseSort(request.getPageable().getSort());

            Page<VoucherDetailEntity> pageResult = voucherService.searchVouchersByTier(
                    request.getCustomerTier(), PageRequest.of(page, size, sort));

            responseBuilder
                    .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                    .setTotalElements((int) pageResult.getTotalElements())
                    .setTotalPages(pageResult.getTotalPages());

            for (VoucherDetailEntity entity : pageResult.getContent()) {
                responseBuilder.addVouchers(toVoucherInfo(entity));
            }
        } catch (BaseException e) {
            log.error("gRPC searchVoucher BaseException - errorCode: {}, message: {}",
                    e.getErrorCode(), e.getDescription());
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } catch (Exception e) {
            log.error("gRPC searchVoucher Exception - error: {}", e.getMessage(), e);
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } finally {
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        }
    }
  @Override
  public void getVoucherById(GetVoucherByIdRequest request, StreamObserver<GetVoucherByIdResponse> responseObserver) {
    GetVoucherByIdResponse.Builder responseBuilder = GetVoucherByIdResponse.newBuilder();
    try {
      log.info("gRPC getVoucherById - requestId: {}, voucherId: {}", 
               request.getRequestInfo().getRequestId(), request.getVoucherId());

      VoucherDetailEntity voucher = voucherRepository.findById(request.getVoucherId())
          .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + request.getVoucherId()));

      VoucherDetail voucherDetail = toVoucherDetail(voucher);

      responseBuilder
          .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
          .setVoucher(voucherDetail);

    } catch (BaseException e) {
      log.error("gRPC getVoucherById BaseException - errorCode: {}, message: {}",
                e.getErrorCode(), e.getDescription());
      responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
    } catch (Exception e) {
      log.error("gRPC getVoucherById Exception - error: {}", e.getMessage(), e);
      responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
    } finally {
      responseObserver.onNext(responseBuilder.build());
      responseObserver.onCompleted();
    }
  }

    private Sort parseSort(String sortStr) {
        if (sortStr == null || sortStr.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sortStr.split(",");
        String field = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private VoucherInfo toVoucherInfo(VoucherDetailEntity entity) {
        return VoucherInfo.newBuilder()
                .setId(entity.getId())
                .setVoucherCode(entity.getVoucherCode())
                .setVoucherName(entity.getVoucherName() != null ? entity.getVoucherName() : "")
                .setDescription(entity.getDescription() != null ? entity.getDescription() : "")
                .setCustomerTier(entity.getCustomerTier() != null ? entity.getCustomerTier().name() : "")
                .setDiscountType(entity.getDiscountType() != null ? entity.getDiscountType().name() : "")
                .setDiscountValue(entity.getDiscountValue() != null ? entity.getDiscountValue().toPlainString() : "0")
                .setMaxDiscount(entity.getMaxDiscount() != null ? entity.getMaxDiscount().toPlainString() : "0")
                .setMinOrderValue(entity.getMinOrderValue() != null ? entity.getMinOrderValue().toPlainString() : "0")
                .setTotalStock(entity.getTotalStock() != null ? entity.getTotalStock() : 0)
                .setAvailableStock(entity.getAvailableStock() != null ? entity.getAvailableStock() : 0)
                .setMaxCollect(entity.getMaxCollect() != null ? entity.getMaxCollect() : 0)
                .setStartDate(entity.getStartDate() != null ? entity.getStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
                .setEndDate(entity.getEndDate() != null ? entity.getEndDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
                .setStatus(entity.getStatus() != null ? entity.getStatus().name() : "")
                .setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
                .build();
    }

    private VoucherDetail toVoucherDetail(VoucherDetailEntity entity) {
        return VoucherDetail.newBuilder()
                .setId(entity.getId())
                .setVoucherCode(entity.getVoucherCode())
                .setVoucherName(entity.getVoucherName() != null ? entity.getVoucherName() : "")
                .setDescription(entity.getDescription() != null ? entity.getDescription() : "")
                .setCustomerTier(entity.getCustomerTier() != null ? entity.getCustomerTier().name() : "")
                .setDiscountType(entity.getDiscountType() != null ? entity.getDiscountType().name() : "")
                .setDiscountValue(entity.getDiscountValue() != null ? entity.getDiscountValue().toPlainString() : "0")
                .setMaxDiscount(entity.getMaxDiscount() != null ? entity.getMaxDiscount().toPlainString() : "0")
                .setMinOrderValue(entity.getMinOrderValue() != null ? entity.getMinOrderValue().toPlainString() : "0")
                .setTotalStock(entity.getTotalStock() != null ? entity.getTotalStock() : 0)
                .setAvailableStock(entity.getAvailableStock() != null ? entity.getAvailableStock() : 0)
                .setMaxCollect(entity.getMaxCollect() != null ? entity.getMaxCollect() : 0)
                .setStartDate(entity.getStartDate() != null ? entity.getStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
                .setEndDate(entity.getEndDate() != null ? entity.getEndDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
                .setStatus(entity.getStatus() != null ? entity.getStatus().name() : "")
                .setMerchantId(1L)
                .setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
                .build();
    }
  private VoucherRequestDetail mapToVoucherRequestDetail(VoucherDetailEntity entity, String nameStore) {
    return VoucherRequestDetail.newBuilder()
        .setVoucherCode(entity.getVoucherCode() != null ? entity.getVoucherCode() : "")
        .setVoucherName(entity.getVoucherName() != null ? entity.getVoucherName() : "")
        .setDescription(entity.getDescription() != null ? entity.getDescription() : "")
        .setDiscountType(entity.getDiscountType() != null ? entity.getDiscountType().name() : "")
        .setDiscountValue(entity.getDiscountValue() != null ? entity.getDiscountValue().toString() : "")
        .setMaxDiscount(entity.getMaxDiscount() != null ? entity.getMaxDiscount().toString() : "")
        .setMinOrderValue(entity.getMinOrderValue() != null ? entity.getMinOrderValue().toString() : "")
        .setTotalStock(entity.getTotalStock() != null ? entity.getTotalStock() : 0)
        .setAvailableStock(entity.getAvailableStock() != null ? entity.getAvailableStock() : 0)
        .setStartDate(entity.getStartDate() != null ? entity.getStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
        .setEndDate(entity.getEndDate() != null ? entity.getEndDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
        .setVoucherStatus(entity.getStatus() != null ? entity.getStatus().name() : "")
        .setNameStore(nameStore != null ? nameStore : "")
        .build();
  }
}
