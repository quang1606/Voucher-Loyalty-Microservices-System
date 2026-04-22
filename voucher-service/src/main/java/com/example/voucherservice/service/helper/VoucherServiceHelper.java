package com.example.voucherservice.service.helper;

import com.example.common.BaseException;
import com.example.voucherservice.constant.CreatorType;
import com.example.voucherservice.constant.CustomerTier;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestMode;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherPurpose;
import com.example.voucherservice.constant.VoucherStatus;
import com.example.voucherservice.dto.request.CreateVoucherExcel;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.entity.VoucherRequestEntity;
import com.example.voucherservice.repository.VoucherRepository;
import com.example.voucherservice.repository.VoucherRequestRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceHelper {

  private static final int BATCH_SIZE = 100;

  private final VoucherRepository voucherRepository;
    private final VoucherRequestRepository voucherRequestRepository;

    public VoucherRequestEntity findRequestByIdAndStatus(Long id, RequestStatus expectedStatus) {
        VoucherRequestEntity entity = voucherRequestRepository.findById(id)
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode("REQUEST_NOT_FOUND")
                        .description("Voucher request not found: " + id)
                        .build());
        if (entity.getStatus() != expectedStatus) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("INVALID_REQUEST_STATUS")
                    .description("Expected status " + expectedStatus + " but was " + entity.getStatus())
                    .build();
        }
        return entity;
    }

  @Transactional
  public VoucherRequestEntity saveExcelVoucherRequest(String requestId, String fileName,
      DiscountType discountType, String username, boolean isPartner,
      String storeName, List<CreateVoucherExcel> dataList) {
    VoucherRequestEntity entity = new VoucherRequestEntity();
    entity.setRequestId(requestId);
    entity.setRequestMode(RequestMode.EXCEL);
    entity.setCreatorType(isPartner ? CreatorType.PARTNER : CreatorType.SYSTEM);
    entity.setVoucherPurpose(VoucherPurpose.HUNT);
    entity.setFileName(fileName);
    entity.setStatus(RequestStatus.DRAFT);
    entity.setCreatedBy(username);
    entity.setStoreName(isPartner ? storeName : null);
    voucherRequestRepository.save(entity);

    List<VoucherDetailEntity> batch = new ArrayList<>();
    for (CreateVoucherExcel row : dataList) {
      VoucherDetailEntity detail = new VoucherDetailEntity();
      detail.setRequestId(requestId);
      detail.setVoucherName(row.getVoucherName());
      detail.setDescription(row.getDescription());
      detail.setCustomerTier(isPartner ? CustomerTier.ALL
          : (row.getCustomerTier() != null
              ? CustomerTier.valueOf(row.getCustomerTier()) : CustomerTier.ALL));
      detail.setDiscountType(row.getDiscountType() != null
          ? DiscountType.valueOf(row.getDiscountType()) : discountType);
      detail.setDiscountValue(row.getDiscountValue());
      detail.setMaxDiscount(row.getMaxDiscount());
      detail.setMinOrderValue(row.getMinOrderValue());
      detail.setTotalStock(row.getTotalStock());
      detail.setAvailableStock(row.getTotalStock());
      detail.setMaxCollect(row.getMaxCollect());
      detail.setStartDate(row.getStartDate());
      detail.setEndDate(row.getEndDate());
      detail.setRequestStatus(RequestStatus.INIT);
      detail.setStatus(VoucherStatus.INACTIVE);
      batch.add(detail);

      if (batch.size() >= BATCH_SIZE) {
        voucherRepository.saveAll(batch);
        batch.clear();
      }
    }
    if (!batch.isEmpty()) {
      voucherRepository.saveAll(batch);
    }

    log.info("Saved excel voucher request: {}, details: {}", requestId, dataList.size());
    return entity;
    }

    @Transactional
    public void saveVoucher(CreateVoucherRequest request, String username, boolean isPartner, String storeName) {
        try {
          String requestId = "VOUCHER_" + System.currentTimeMillis();

            VoucherRequestEntity requestEntity = new VoucherRequestEntity();
            requestEntity.setRequestId(requestId);
            requestEntity.setRequestMode(RequestMode.SINGLE);
            requestEntity.setCreatorType(isPartner ? CreatorType.PARTNER : CreatorType.SYSTEM);
            requestEntity.setVoucherPurpose(request.getVoucherPurpose() != null ? request.getVoucherPurpose() : VoucherPurpose.HUNT);
            requestEntity.setStatus(RequestStatus.INIT);
            requestEntity.setCreatedBy(username);
            requestEntity.setStoreName(isPartner ? storeName : null);
            voucherRequestRepository.save(requestEntity);

            VoucherDetailEntity voucherDetailEntity = new VoucherDetailEntity();
            voucherDetailEntity.setRequestId(requestId);
            voucherDetailEntity.setCustomerTier(isPartner ? CustomerTier.ALL : request.getCustomerTier());
            voucherDetailEntity.setVoucherName(request.getVoucherName());
            voucherDetailEntity.setDescription(request.getDescription());
            voucherDetailEntity.setDiscountType(request.getDiscountType());
            voucherDetailEntity.setDiscountValue(request.getDiscountValue());
          if (request.getDiscountType() != DiscountType.FIXED) {
            voucherDetailEntity.setMaxDiscount(request.getMaxDiscount());
          }
            voucherDetailEntity.setMinOrderValue(request.getMinOrderValue());
            voucherDetailEntity.setTotalStock(request.getTotalStock());
            voucherDetailEntity.setAvailableStock(request.getTotalStock());
            voucherDetailEntity.setMaxCollect(request.getMaxCollect());
            voucherDetailEntity.setStartDate(request.getStartDate());
            voucherDetailEntity.setEndDate(request.getEndDate());
            voucherDetailEntity.setRequestStatus(RequestStatus.INIT);
            voucherDetailEntity.setStatus(VoucherStatus.INACTIVE);
            voucherRepository.save(voucherDetailEntity);
        } catch (Exception e) {
            log.error("Failed to save voucher for user: {}, error: {}", username, e.getMessage(), e);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode("SAVE_VOUCHER_FAILED")
                    .description("Failed to save voucher")
                    .build();
        }
    }
}
