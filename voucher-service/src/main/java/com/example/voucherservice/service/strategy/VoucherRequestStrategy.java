package com.example.voucherservice.service.strategy;

import com.example.common.BaseException;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.dto.request.CreateVoucherExcel;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.entity.VoucherRequestEntity;
import com.example.voucherservice.repository.VoucherRepository;
import com.example.voucherservice.repository.VoucherRequestRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public abstract class VoucherRequestStrategy {

  private static final int BATCH_SIZE = 100;

  @Autowired
  private VoucherRepository voucherRepository;

  @Autowired
  private VoucherRequestRepository voucherRequestRepository;

  public abstract boolean support(DiscountType type);

  public abstract void validateRequest(CreateVoucherRequest request);

  public abstract void processApprovalDetail(VoucherDetailEntity detail);

  public abstract void processExcelRequest(VoucherRequestEntity requestEntity,
      List<CreateVoucherExcel> dataList);

  @Async
  public void processApprovedVoucher(VoucherRequestEntity entity) {
    String requestId = entity.getRequestId();

    if (!voucherRepository.existsByRequestIdAndRequestStatus(requestId, RequestStatus.PROCESSING)) {
      checkAndUpdateRequest(entity, RequestStatus.FINISH);
      return;
    }

    if (voucherRepository.existsByRequestIdAndDiscountTypeIsNull(requestId)) {
      checkAndUpdateRequest(entity, RequestStatus.FAILED);
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("NULL_DISCOUNT_TYPE")
          .description("Voucher detail with null discount type found for requestId: " + requestId)
          .build();
    }

    Long nextId = 0L;
    int batchNumber = 0;
    int totalProcessed = 0;
    while (true) {
      List<VoucherDetailEntity> batch = voucherRepository
          .findByRequestIdAndRequestStatusAndIdGreaterThanOrderByIdAsc(
              requestId, RequestStatus.PROCESSING, nextId,
              PageRequest.of(0, BATCH_SIZE));

      if (batch.isEmpty()) {
        break;
      }

      batchNumber++;
      for (VoucherDetailEntity detail : batch) {
        try {
          processApprovalDetail(detail);
          detail.setRequestStatus(RequestStatus.SUCCESS);
        } catch (Exception ex) {
          log.error("Failed to process detail id={}: {}", detail.getId(), ex.getMessage(), ex);
          detail.setRequestStatus(RequestStatus.FAILED);
          detail.setErrorMessage(ex.getMessage());
        }
      }
      voucherRepository.saveAll(batch);
      totalProcessed += batch.size();
      log.info("Saved batch {} with {} details, total processed: {}",
          batchNumber, batch.size(), totalProcessed);

      nextId = batch.get(batch.size() - 1).getId();
      log.info("Processed batch #{}: {} items for requestId: {}", batchNumber, batch.size(),
          requestId);

      if (batch.size() < BATCH_SIZE) {
        break;
      }
    }

    checkAndUpdateRequest(entity, RequestStatus.FINISH);
    log.info("Finished processing all voucher details for requestId: {}", requestId);
  }

  protected void updateStatusAllFailed(VoucherRequestEntity entity) {
    String requestId = entity.getRequestId();
    boolean hasNonFailed = voucherRepository.existsByRequestIdAndRequestStatusNot(
        requestId, RequestStatus.FAILED);
    if (!hasNonFailed) {
      entity.setStatus(RequestStatus.FAILED);
      voucherRequestRepository.save(entity);
      log.warn("All details FAILED for requestId: {}, request marked as FAILED", requestId);
    }else {
        log.info("successfully completed with entity id: {}", entity.getId());
        entity.setStatus(RequestStatus.INIT);
        voucherRequestRepository.save(entity);

    }
  }

  private void checkAndUpdateRequest(VoucherRequestEntity entity, RequestStatus status) {
    entity.setStatus(status);
    voucherRequestRepository.save(entity);
    log.info("Updated request {} to status {}", entity.getRequestId(), status);
  }
}
