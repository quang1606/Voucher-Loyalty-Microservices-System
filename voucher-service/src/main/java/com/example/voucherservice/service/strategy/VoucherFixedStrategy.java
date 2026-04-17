package com.example.voucherservice.service.strategy;

import com.example.common.BaseException;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherStatus;
import com.example.voucherservice.dto.request.CreateVoucherExcel;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.entity.VoucherRequestEntity;
import com.example.voucherservice.repository.VoucherRepository;
import io.micrometer.common.util.StringUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoucherFixedStrategy extends VoucherRequestStrategy {

  private static final int BATCH_SIZE = 100;
  private final VoucherRepository voucherRepository;

  @Override
  public boolean support(DiscountType type) {
    return DiscountType.FIXED == type;
  }

  @Override
  public void validateRequest(CreateVoucherRequest request) {
    if (request.getMinOrderValue() == null) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("INVALID_FIXED_VOUCHER")
          .description("Min order value is required for FIXED discount")
          .build();
    }
    if (request.getDiscountValue().compareTo(request.getMinOrderValue()) > 0) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("INVALID_FIXED_VOUCHER")
          .description("Discount value must not exceed min order value")
          .build();
    }
  }

  @Override
  public void processApprovalDetail(VoucherDetailEntity detail) {
    detail.setMaxDiscount(detail.getDiscountValue());
    detail.setStatus(VoucherStatus.ACTIVE);
  }

  @Override
  @Async
  public void processExcelRequest(VoucherRequestEntity requestEntity,
      List<CreateVoucherExcel> dataList) {
    String requestId = requestEntity.getRequestId();
    log.info("Start processing FIXED excel request: {}", requestId);

    Long nextId = 0L;
    int batchNumber = 0;
    int totalProcessed = 0;
    int failedCount = 0;

    while (true) {
      List<VoucherDetailEntity> batch = voucherRepository
          .findByRequestIdAndRequestStatusAndIdGreaterThanOrderByIdAsc(
              requestId, RequestStatus.INIT, nextId, PageRequest.of(0, BATCH_SIZE));

      if (batch.isEmpty()) {
        break;
      }

      batchNumber++;
      for (VoucherDetailEntity detail : batch) {
        try {
          if (StringUtils.isBlank(detail.getVoucherName())) {
            throw BaseException.builder().httpStatus(HttpStatus.BAD_REQUEST)
                .errorCode("INVALID_VOUCHER_NAME")
                .description("Voucher name is required").build();
          }
          if (detail.getDiscountValue() == null) {
            throw BaseException.builder().httpStatus(HttpStatus.BAD_REQUEST)
                .errorCode("INVALID_DISCOUNT_VALUE")
                .description("Discount value is required").build();
          }
          if (detail.getMinOrderValue() == null) {
            throw BaseException.builder().httpStatus(HttpStatus.BAD_REQUEST)
                .errorCode("INVALID_MIN_ORDER_VALUE")
                .description("Min order value is required for FIXED discount").build();
          }
          if (detail.getDiscountValue().compareTo(detail.getMinOrderValue()) > 0) {
            throw BaseException.builder().httpStatus(HttpStatus.BAD_REQUEST)
                .errorCode("INVALID_FIXED_VOUCHER")
                .description("Discount value must not exceed min order value").build();
          }
          if (detail.getTotalStock() == null || detail.getTotalStock() <= 0) {
            throw BaseException.builder().httpStatus(HttpStatus.BAD_REQUEST)
                .errorCode("INVALID_TOTAL_STOCK")
                .description("Total stock must be positive").build();
          }
          if (detail.getStartDate() == null || detail.getEndDate() == null) {
            throw BaseException.builder().httpStatus(HttpStatus.BAD_REQUEST)
                .errorCode("INVALID_DATE")
                .description("Start date and end date are required").build();
          }
          if (detail.getStartDate().isAfter(detail.getEndDate())) {
            throw BaseException.builder().httpStatus(HttpStatus.BAD_REQUEST)
                .errorCode("INVALID_DATE_RANGE")
                .description("Start date must be before end date").build();
          }
          detail.setMaxDiscount(detail.getDiscountValue());
        } catch (BaseException ex) {
          log.warn("Detail id={} validation failed: [{}] {}",
              detail.getId(), ex.getErrorCode(), ex.getDescription());
          detail.setRequestStatus(RequestStatus.FAILED);
          detail.setErrorMessage(ex.getDescription());
          failedCount++;
        }
      }
      voucherRepository.saveAll(batch);

      totalProcessed += batch.size();
      nextId = batch.get(batch.size() - 1).getId();
      log.info("FIXED batch #{}: {} items, failed: {}, totalProcessed: {}",
          batchNumber, batch.size(), failedCount, totalProcessed);

      if (batch.size() < BATCH_SIZE) {
        break;
      }
    }

    log.info("Finished FIXED excel request: {}, total: {}, failed: {}",
        requestId, totalProcessed, failedCount);

    updateStatusAllFailed(requestEntity);
  }
}
