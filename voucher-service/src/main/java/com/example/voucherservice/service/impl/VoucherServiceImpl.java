package com.example.voucherservice.service.impl;

import com.example.common.BaseException;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.dto.request.CreateVoucherExcel;
import com.example.voucherservice.dto.request.CreateVoucherExcelRequest;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
import com.example.voucherservice.dto.response.VoucherRequestResponse;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.entity.VoucherRequestEntity;
import com.example.voucherservice.mapper.VoucherMapper;
import com.example.voucherservice.repository.VoucherRepository;
import com.example.voucherservice.repository.VoucherRequestRepository;
import com.example.voucherservice.service.AuthorizationService;
import com.example.voucherservice.service.VoucherService;
import com.example.voucherservice.service.helper.VoucherServiceHelper;
import com.example.voucherservice.service.helper.excel.ExcelReaderHelper;
import com.example.voucherservice.service.strategy.VoucherRequestStrategy;
import com.example.voucherservice.service.strategy.VoucherRequestStrategyFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImpl implements VoucherService {

  private static final int BATCH_SIZE = 100;

  private final VoucherRequestStrategyFactory strategyFactory;
  private final VoucherServiceHelper voucherServiceHelper;
  private final AuthorizationService authorizationService;
  private final VoucherRequestRepository voucherRequestRepository;
  private final VoucherRepository voucherRepository;
  private final ExcelReaderHelper excelReaderHelper;

  @Override
  public void createVoucherByExcel(CreateVoucherExcelRequest request) {
    MultipartFile file = request.getFile();
    log.info("CreateRequestFromExcel - fileName: {}, requestType: {}", file.getOriginalFilename(),
        request.getDiscountType());
    validateExcelRequest(request);

    try {
      List<CreateVoucherExcel> dataList = excelReaderHelper.readExcel(request.getFile(),
          request.getDiscountType());
      if (dataList == null || dataList.isEmpty()) {
        throw BaseException.builder()
            .httpStatus(HttpStatus.BAD_REQUEST)
            .errorCode("EMPTY_EXCEL_DATA")
            .description("Excel file contains no data")
            .build();
      }
      String username = authorizationService.getName();
      boolean isPartner = authorizationService.isPartner();
      VoucherRequestEntity requestEntity = voucherServiceHelper.saveExcelVoucherRequest(
          request.getRequestId(), request.getFile().getOriginalFilename(),
          request.getDiscountType(), username, isPartner, dataList);

      VoucherRequestStrategy strategy = strategyFactory.getStrategy(request.getDiscountType());
      strategy.processExcelRequest(requestEntity, dataList);
    } catch (BaseException e) {
      throw e;
    } catch (Exception e) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
          .description("Internal Server Error")
          .errorCode("INTERNAL_SERVER_ERROR")
          .build();
    }


  }

  private void validateExcelRequest(CreateVoucherExcelRequest request) {
    if (request.getFile() == null || request.getFile().isEmpty()) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("EMPTY_FILE")
          .description("Excel file is empty")
          .build();
    }

    try {
      DiscountType.valueOf(request.getDiscountType().name());
    } catch (Exception e) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("INVALID_DISCOUNT_TYPE")
          .description("Invalid discount type: " + request.getDiscountType())
          .build();
    }

    List<RequestStatus> activeStatuses = Arrays.asList(
        RequestStatus.INIT, RequestStatus.PENDING_APPROVE,
        RequestStatus.APPROVED, RequestStatus.FINISH);
    if (voucherRequestRepository.existsByRequestIdAndStatusIn(
        request.getRequestId(), activeStatuses)) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.CONFLICT)
          .errorCode("DUPLICATE_REQUEST_ID")
          .description("Request ID already exists with active status: " + request.getRequestId())
          .build();
    }
  }

  @Override
  public Page<VoucherRequestResponse> getVouchers(RequestStatus status, LocalDateTime fromDate,
      LocalDateTime toDate, String partnerId, DiscountType discountType, Pageable pageable) {
    String createdBy = null;
    if (authorizationService.isPartner()) {
      createdBy = authorizationService.getName();
    } else if (partnerId != null) {
      createdBy = partnerId;
    }
    Page<VoucherRequestEntity> page = voucherRequestRepository.findByFilters(
        status, fromDate, toDate, createdBy, pageable);
    return page.map(VoucherMapper::toRequestResponse);
  }

  @Override
  public void createVoucher(CreateVoucherRequest request) {
    boolean isPartner = authorizationService.isPartner();
    if (!isPartner) {
      validateSystemFields(request);
    }

    VoucherRequestStrategy strategy = strategyFactory.getStrategy(request.getDiscountType());
    strategy.validateRequest(request);

    String username = authorizationService.getName();
    String partnerId = isPartner ? username : username;
    voucherServiceHelper.saveVoucher(request, username, isPartner, partnerId);
  }

  @Override
  public void submitVoucher(Long id) {
    VoucherRequestEntity entity = voucherServiceHelper.findRequestByIdAndStatus(id,
        RequestStatus.INIT);
    entity.setStatus(RequestStatus.PENDING_APPROVE);
    entity.setUpdatedBy(authorizationService.getName());
    voucherRequestRepository.save(entity);
  }

  @Override
  public void cancelVoucher(Long id) {
    VoucherRequestEntity entity = voucherServiceHelper.findRequestByIdAndStatus(id,
        RequestStatus.INIT);
    entity.setStatus(RequestStatus.CANCELLED);
    entity.setUpdatedBy(authorizationService.getName());
    voucherRequestRepository.save(entity);
  }

  @Override
  @Transactional
  public void confirmVoucher(Long id, String action) {
    if (!"APPROVED".equalsIgnoreCase(action) && !"REJECTED".equalsIgnoreCase(action)) {
      log.warn("Invalid action received: {}", action);
      throw BaseException.builder().httpStatus(HttpStatus.BAD_REQUEST).errorCode("INVALID_ACTION")
          .description("Action must be APPROVED or REJECTED").build();
    }

    VoucherRequestEntity entity = voucherServiceHelper.findRequestByIdAndStatus(id,
        RequestStatus.PENDING_APPROVE);

    if ("REJECTED".equalsIgnoreCase(action)) {
      handleRejected(entity);
    } else {
      handleApproved(entity);
    }
  }

  private void handleApproved(VoucherRequestEntity entity) {
    String username = authorizationService.getName();

    entity.setStatus(RequestStatus.APPROVED);
    entity.setConfirmedBy(username);
    entity.setConfirmedTime(LocalDateTime.now());
    entity.setUpdatedBy(username);
    voucherRequestRepository.save(entity);

    Long nextId = 0L;
    int totalProcessed = 0;
    int batchNumber = 0;

    while (true) {
      List<VoucherDetailEntity> batch = voucherRepository.findByRequestIdAndRequestStatusAndIdGreaterThanOrderByIdAsc(
          entity.getRequestId(), RequestStatus.INIT, nextId, PageRequest.of(0, BATCH_SIZE));

      if (batch.isEmpty()) {
        break;
      }

      batchNumber++;
      for (VoucherDetailEntity detail : batch) {
        detail.setRequestStatus(RequestStatus.PROCESSING);
      }
      voucherRepository.saveAll(batch);

      totalProcessed += batch.size();
      nextId = batch.get(batch.size() - 1).getId();

      log.info("Approved batch #{}: {} items, totalProcessed: {}", batchNumber, batch.size(),
          totalProcessed);

      if (batch.size() < BATCH_SIZE) {
        log.info("Last batch processed, breaking loop");
        break;
      }
    }
    log.info("Processing batch #{} completed", batchNumber);

    VoucherDetailEntity firstDetail = voucherRepository
        .findByRequestIdAndRequestStatusAndIdGreaterThanOrderByIdAsc(
            entity.getRequestId(), RequestStatus.PROCESSING, 0L, PageRequest.of(0, 1))
        .stream().findFirst().orElse(null);
    if (firstDetail != null) {
      VoucherRequestStrategy strategy = strategyFactory.getStrategy(firstDetail.getDiscountType());
      strategy.processApprovedVoucher(entity);
    }
  }

  private void handleRejected(VoucherRequestEntity entity) {
    String username = authorizationService.getName();

    entity.setStatus(RequestStatus.REJECTED);
    entity.setConfirmedBy(username);
    entity.setConfirmedTime(LocalDateTime.now());
    entity.setUpdatedBy(username);
    voucherRequestRepository.save(entity);

    Long nextId = 0L;
    int totalProcessed = 0;
    int batchNumber = 0;

    while (true) {
      List<VoucherDetailEntity> batch = voucherRepository.findByRequestIdAndRequestStatusAndIdGreaterThanOrderByIdAsc(
          entity.getRequestId(), RequestStatus.INIT, nextId, PageRequest.of(0, BATCH_SIZE));

      if (batch.isEmpty()) {
        log.warn("Batch #{} returned empty result, breaking loop", batchNumber);
        break;
      }

      batchNumber++;
      for (VoucherDetailEntity detail : batch) {
        detail.setRequestStatus(RequestStatus.REJECTED);
      }
      voucherRepository.saveAll(batch);

      totalProcessed += batch.size();
      nextId = batch.get(batch.size() - 1).getId();

      log.info("Rejected batch #{}: {} items, totalProcessed: {}", batchNumber, batch.size(),
          totalProcessed);

      if (batch.size() < BATCH_SIZE) {
        log.info("Last batch processed, breaking loop");
        break;
      }
    }

    log.info("Rejected all voucher details for requestId: {}, total: {}", entity.getRequestId(),
        totalProcessed);
  }

  private void validateSystemFields(CreateVoucherRequest request) {
    if (request.getCustomerTier() == null) {
      throw BaseException.builder().httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("MISSING_CUSTOMER_TIER")
          .description("Customer tier is required for system creator").build();
    }
  }
}
