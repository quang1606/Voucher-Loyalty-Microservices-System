package com.example.voucherservice.grpc;

import com.example.common.BaseException;
import com.example.voucherservice.entity.MockInvoiceEntity;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.repository.MockInvoiceRepository;
import com.example.voucherservice.repository.VoucherRepository;
import com.example.voucherservice.repository.VoucherRequestRepository;
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
import vn.com.grpc.voucher.entity.GetMockInvoicesRequest;
import vn.com.grpc.voucher.entity.GetMockInvoicesResponse;
import vn.com.grpc.voucher.entity.GetVoucherByIdRequest;
import vn.com.grpc.voucher.entity.GetVoucherByIdResponse;
import vn.com.grpc.voucher.entity.GetVoucherByRequestIdRequest;
import vn.com.grpc.voucher.entity.GetVoucherByRequestIdResponse;
import vn.com.grpc.voucher.entity.MockInvoiceInfo;
import vn.com.grpc.voucher.entity.SearchVoucherRequest;
import vn.com.grpc.voucher.entity.SearchVoucherResponse;
import vn.com.grpc.voucher.entity.VoucherDetail;
import vn.com.grpc.voucher.entity.VoucherInfo;
import vn.com.grpc.voucher.service.VoucherGrpcServiceGrpc;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class VoucherGrpcServer extends VoucherGrpcServiceGrpc.VoucherGrpcServiceImplBase {

    private final VoucherService voucherService;
    private final VoucherRepository voucherRepository;
    private final MockInvoiceRepository mockInvoiceRepository;
    private final VoucherRequestRepository voucherRequestRepository;

    @Override
    public void searchVoucher(SearchVoucherRequest request,
                              StreamObserver<SearchVoucherResponse> responseObserver) {
        SearchVoucherResponse.Builder responseBuilder = SearchVoucherResponse.newBuilder();
        try {
            log.info("gRPC searchVoucher request: {}", request);

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
            log.info("gRPC searchVoucher response: {}", responseBuilder.build());
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
  public void getVoucherByRequestId(GetVoucherByRequestIdRequest request, StreamObserver<GetVoucherByRequestIdResponse> responseObserver) {
    GetVoucherByRequestIdResponse.Builder responseBuilder = GetVoucherByRequestIdResponse.newBuilder();
    try {
      log.info("gRPC getVoucherByRequestId request: {}", request);

      var vouchers = voucherService.getVouchersByRequestId(request.getRequestId());
      
      for (VoucherDetailEntity voucher : vouchers) {
        responseBuilder.addVoucherRequest(toVoucherDetail(voucher));
      }

      responseBuilder.setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()));
      log.info("gRPC getVoucherByRequestId response: {}", responseBuilder.build());

    } catch (BaseException e) {
      log.error("gRPC getVoucherByRequestId BaseException - errorCode: {}, message: {}",
                e.getErrorCode(), e.getDescription());
      responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
    } catch (Exception e) {
      log.error("gRPC getVoucherByRequestId Exception - error: {}", e.getMessage(), e);
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
      log.info("gRPC getVoucherById request: {}", request);

      VoucherDetailEntity voucher = voucherRepository.findById(request.getVoucherId())
          .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + request.getVoucherId()));

      VoucherDetail voucherDetail = toVoucherDetail(voucher);

      responseBuilder
          .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
          .setVoucher(voucherDetail);
      log.info("gRPC getVoucherById response: {}", responseBuilder.build());

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
                .setDiscountType(entity.getDiscountType() != null ? vn.com.grpc.voucher.entity.DiscountType.valueOf(entity.getDiscountType().name()) : vn.com.grpc.voucher.entity.DiscountType.FIXED)
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
        String creatorType = voucherRequestRepository.findByRequestId(entity.getRequestId())
                .map(r -> r.getCreatorType() != null ? r.getCreatorType().name() : "").orElse("");
        String nameStore = voucherRequestRepository.findByRequestId(entity.getRequestId())
                .map(r -> r.getStoreName() != null ? r.getStoreName() : "").orElse("");
        return VoucherDetail.newBuilder()
                .setId(entity.getId())
                .setVoucherCode(entity.getVoucherCode())
                .setVoucherName(entity.getVoucherName() != null ? entity.getVoucherName() : "")
                .setDescription(entity.getDescription() != null ? entity.getDescription() : "")
                .setCustomerTier(entity.getCustomerTier() != null ? entity.getCustomerTier().name() : "")
                .setDiscountType(entity.getDiscountType() != null ? vn.com.grpc.voucher.entity.DiscountType.valueOf(entity.getDiscountType().name()) : vn.com.grpc.voucher.entity.DiscountType.FIXED)
                .setDiscountValue(entity.getDiscountValue() != null ? entity.getDiscountValue().toPlainString() : "0")
                .setMaxDiscount(entity.getMaxDiscount() != null ? entity.getMaxDiscount().toPlainString() : "0")
                .setMinOrderValue(entity.getMinOrderValue() != null ? entity.getMinOrderValue().toPlainString() : "0")
                .setTotalStock(entity.getTotalStock() != null ? entity.getTotalStock() : 0)
                .setAvailableStock(entity.getAvailableStock() != null ? entity.getAvailableStock() : 0)
                .setMaxCollect(entity.getMaxCollect() != null ? entity.getMaxCollect() : 0)
                .setStartDate(entity.getStartDate() != null ? entity.getStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
                .setEndDate(entity.getEndDate() != null ? entity.getEndDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
                .setStatus(entity.getStatus() != null ? entity.getStatus().name() : "")
                .setNameStore(nameStore)
                .setCreatorType(!creatorType.isEmpty() ? vn.com.grpc.voucher.entity.CreatorType.valueOf(creatorType) : vn.com.grpc.voucher.entity.CreatorType.PARTNER)
                .setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
                .build();
    }
    @Override
    public void getMockInvoices(GetMockInvoicesRequest request,
                                StreamObserver<GetMockInvoicesResponse> responseObserver) {
        GetMockInvoicesResponse.Builder responseBuilder = GetMockInvoicesResponse.newBuilder();
        try {
            log.info("gRPC getMockInvoices request: {}", request);

            String nameStore = request.getNameStore().isEmpty() ? null : request.getNameStore();
            String title = request.getTitle().isEmpty() ? null : request.getTitle();
            int page = request.getPage();
            int size = request.getSize() > 0 ? request.getSize() : 20;

            Page<MockInvoiceEntity> pageResult = mockInvoiceRepository.findByFilters(
                    nameStore, title, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

            responseBuilder
                    .setResponseInfo(GrpcUtils.buildResponseInfoSuccess(request.getRequestInfo()))
                    .setTotalElements((int) pageResult.getTotalElements())
                    .setTotalPages(pageResult.getTotalPages());

            for (MockInvoiceEntity entity : pageResult.getContent()) {
                responseBuilder.addInvoices(toMockInvoiceInfo(entity));
            }
            log.info("gRPC getMockInvoices response: {}", responseBuilder.build());
        } catch (BaseException e) {
            log.error("gRPC getMockInvoices BaseException - errorCode: {}, message: {}",
                    e.getErrorCode(), e.getDescription());
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } catch (Exception e) {
            log.error("gRPC getMockInvoices Exception - error: {}", e.getMessage(), e);
            responseBuilder.setResponseInfo(GrpcUtils.buildResponseFail(request.getRequestInfo(), e));
        } finally {
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        }
    }

    private MockInvoiceInfo toMockInvoiceInfo(MockInvoiceEntity entity) {
        return MockInvoiceInfo.newBuilder()
                .setId(entity.getId())
                .setTitle(entity.getTitle() != null ? entity.getTitle() : "")
                .setNameStore(entity.getNameStore() != null ? entity.getNameStore() : "")
                .setAmount(entity.getAmount() != null ? entity.getAmount().toPlainString() : "0")
                .setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
                .setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0)
                .build();
    }


}
