package com.example.voucherservice.service.impl;

import com.example.common.BaseException;
import com.example.voucherservice.constant.CreatorType;
import com.example.voucherservice.constant.ConfirmAction;
import com.example.voucherservice.constant.RewardType;
import com.example.voucherservice.constant.CustomerTier;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestMode;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherPurpose;
import com.example.voucherservice.constant.VoucherStatus;
import com.example.voucherservice.dto.projection.ProjectionStatus;
import com.example.voucherservice.dto.projection.ProjectionTotalVoucher;
import com.example.voucherservice.dto.request.ConfirmVoucherRequest;
import com.example.voucherservice.dto.request.CreateVoucherExcel;
import com.example.voucherservice.dto.request.CreateVoucherExcelRequest;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
import com.example.voucherservice.dto.request.CreateMissionRequest;
import com.example.voucherservice.dto.response.VoucherDetailResponsePage;
import com.example.voucherservice.dto.response.VoucherRequestResponse;
import com.example.voucherservice.dto.response.VoucherRequestResponsePage;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.entity.VoucherRequestEntity;
import com.example.voucherservice.grpc.IdentityGrpcClient;
import com.example.voucherservice.grpc.MissionGrpcClient;
import com.example.voucherservice.mapper.VoucherMapper;
import com.example.voucherservice.repository.VoucherRepository;
import com.example.voucherservice.repository.VoucherRequestRepository;
import com.example.voucherservice.service.AuthorizationService;
import com.example.voucherservice.service.VoucherService;
import com.example.voucherservice.service.helper.VoucherServiceHelper;
import com.example.voucherservice.service.helper.excel.ExcelReaderHelper;
import com.example.voucherservice.service.strategy.VoucherRequestStrategy;
import com.example.voucherservice.service.strategy.VoucherRequestStrategyFactory;
import com.example.voucherservice.specification.VoucherDetailSpecification;
import com.example.voucherservice.specification.VoucherRequestSpecification;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.com.fcc.grpc.identity.service.IdentityServiceGrpc;

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
  private final IdentityGrpcClient identityGrpcClient;
  private final MissionGrpcClient missionGrpcClient;
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
      String partnerId = isPartner ? authorizationService.getPartnerId() : null;
      String storeName = isPartner ? identityGrpcClient.getNameStore(partnerId) : null;
      VoucherRequestEntity requestEntity = voucherServiceHelper.saveExcelVoucherRequest(
          request.getRequestId(), request.getFile().getOriginalFilename(),
          request.getDiscountType(), username, isPartner, storeName, dataList);

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
  public VoucherRequestResponsePage getVouchers(RequestStatus status, RequestMode requestMode,
      CreatorType creatorType, VoucherPurpose voucherPurpose, String storeName,
      LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {

    if (authorizationService.isPartner()
        && creatorType != null && creatorType != CreatorType.PARTNER) {
      return VoucherRequestResponsePage.builder()
          .data(Collections.emptyList())
          .totalElements(0)
          .totalPages(0)
          .page(pageable.getPageNumber())
          .size(pageable.getPageSize())
          .build();
    }

    String statusValue = status != null ? status.name() : null;
    List<String> listStatus;
    if (authorizationService.isCheckerRole()) {
      listStatus = List.of(RequestStatus.PENDING_APPROVE.name(), RequestStatus.APPROVED.name(),
          RequestStatus.REJECTED.name(), RequestStatus.FINISH.name());
      if (statusValue != null && !listStatus.contains(statusValue)) {
        return VoucherRequestResponsePage.builder()
            .data(Collections.emptyList())
            .totalElements(0)
            .totalPages(0)
            .page(pageable.getPageNumber())
            .size(pageable.getPageSize())
            .build();
      }
    } else {
      listStatus = statusValue != null ? List.of(statusValue) : null;
    }


    String createdBy = authorizationService.isPartner() ? authorizationService.getName() : null;

    Specification<VoucherRequestEntity> spec = VoucherRequestSpecification.withFilters(
        listStatus, fromDate, toDate, createdBy, requestMode, creatorType, voucherPurpose, storeName);

    Page<VoucherRequestEntity> pageResult = voucherRequestRepository.findAll(spec, pageable);

    List<String> resultRequestIds = pageResult.getContent().stream()
        .map(VoucherRequestEntity::getRequestId)
        .toList();

    List<ProjectionTotalVoucher> totals = resultRequestIds.isEmpty()
        ? Collections.emptyList()
        : voucherRepository.countTotalVoucherByRequestIds(resultRequestIds);
    List<ProjectionStatus> statuses = resultRequestIds.isEmpty()
        ? Collections.emptyList()
        : voucherRepository.countStatusByRequestIds(resultRequestIds);

    return VoucherRequestResponsePage.builder()
        .data(VoucherMapper.toRequestResponseList(pageResult.getContent(), totals, statuses))
        .totalElements(pageResult.getTotalElements())
        .totalPages(pageResult.getTotalPages())
        .page(pageResult.getNumber())
        .size(pageResult.getSize())
        .build();
  }

  @Override
  public VoucherDetailResponsePage getAllVoucherDetails(CreatorType creatorType,
      CustomerTier customerTier, DiscountType discountType, VoucherPurpose voucherPurpose,
      VoucherStatus voucherStatus, String storeName,
      LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {

    if (authorizationService.isPartner()
        && creatorType != null && creatorType != CreatorType.PARTNER) {
      return emptyDetailPage(pageable);
    }

    if (authorizationService.isPartner()) {
      creatorType = CreatorType.PARTNER;
    }

    String createdBy = authorizationService.isPartner() ? authorizationService.getName() : null;

    Specification<VoucherRequestEntity> requestSpec = VoucherRequestSpecification.withFilters(
        null, null, null, createdBy, null, creatorType, voucherPurpose, storeName);
    List<String> requestIds = voucherRequestRepository.findAll(requestSpec).stream()
        .map(VoucherRequestEntity::getRequestId)
        .toList();

    if (requestIds.isEmpty()) {
      return emptyDetailPage(pageable);
    }

    Specification<VoucherDetailEntity> detailSpec = VoucherDetailSpecification.withAllFilters(
        requestIds, customerTier, discountType, voucherStatus, fromDate, toDate);
    Page<VoucherDetailEntity> page = voucherRepository.findAll(detailSpec, pageable);

    return VoucherDetailResponsePage.builder()
        .data(VoucherMapper.toDetailResponseList(page.getContent()))
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .page(page.getNumber())
        .size(page.getSize())
        .build();
  }

  private VoucherDetailResponsePage emptyDetailPage(Pageable pageable) {
    return VoucherDetailResponsePage.builder()
        .data(Collections.emptyList())
        .totalElements(0)
        .totalPages(0)
        .page(pageable.getPageNumber())
        .size(pageable.getPageSize())
        .build();
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
    String partnerId = isPartner ? authorizationService.getPartnerId() : null;
    String storeName = isPartner ? identityGrpcClient.getNameStore(partnerId) : null;
    voucherServiceHelper.saveVoucher(request, username, isPartner, storeName);
  }

  @Override
  public void submitVoucher(Long id) {
    VoucherRequestEntity entity = voucherServiceHelper.findRequestByIdAndStatus(id,
        RequestStatus.INIT);
    validateNotRewardPurpose(entity);
    entity.setStatus(RequestStatus.PENDING_APPROVE);
    entity.setUpdatedBy(authorizationService.getName());
    voucherRequestRepository.save(entity);
  }

  @Override
  public void cancelVoucher(Long id) {
    VoucherRequestEntity entity = voucherServiceHelper.findRequestByIdAndStatus(id,
        RequestStatus.INIT);
    validateNotRewardPurpose(entity);
    entity.setStatus(RequestStatus.CANCELLED);
    entity.setUpdatedBy(authorizationService.getName());
    voucherRequestRepository.save(entity);
  }

  @Override
  public void confirmVoucher(Long id, ConfirmVoucherRequest request) {
    if (request.getAction() == ConfirmAction.REJECTED
        && (request.getReason() == null || request.getReason().isBlank())) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("MISSING_REASON")
          .description("Reason is required when rejecting")
          .build();
    }

    VoucherRequestEntity entity = voucherServiceHelper.findRequestByIdAndStatus(id,
        RequestStatus.PENDING_APPROVE);
    validateNotRewardPurpose(entity);

    if (request.getAction() == ConfirmAction.REJECTED) {
      handleRejected(entity, request.getReason());
    } else {
      handleApproved(entity);
    }
  }

  public void handleApproved(VoucherRequestEntity entity) {
    validateNotRewardPurpose(entity);
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

  public void handleRejected(VoucherRequestEntity entity, String reason) {
    String username = authorizationService.getName();

    entity.setStatus(RequestStatus.REJECTED);
    entity.setReason(reason);
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

  private void validateNotRewardPurpose(VoucherRequestEntity entity) {
    if (entity.getVoucherPurpose() == VoucherPurpose.REWARD) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("REWARD_VOUCHER_NOT_ALLOWED")
          .description("Cannot perform this action on REWARD voucher. Use mission APIs instead.")
          .build();
    }
  }

  private void validateSystemFields(CreateVoucherRequest request) {
    if (request.getCustomerTier() == null) {
      throw BaseException.builder().httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("MISSING_CUSTOMER_TIER")
          .description("Customer tier is required for system creator").build();
    }
  }

  @Override
  public VoucherRequestResponse getVoucherById(Long id, String voucherName,
      RequestStatus status, Pageable pageable) {
    VoucherRequestEntity entity = voucherRequestRepository.findById(id)
        .orElseThrow(() -> BaseException.builder()
            .httpStatus(HttpStatus.NOT_FOUND)
            .errorCode("NOT_FOUND")
            .description("Voucher request not found: " + id)
            .build());

    String requestId = entity.getRequestId();

    Specification<VoucherDetailEntity> spec = VoucherDetailSpecification.withFilters(
        requestId, voucherName, status);
    Page<VoucherDetailEntity> detailPage = voucherRepository.findAll(spec, pageable);

    List<ProjectionTotalVoucher> totals = voucherRepository
        .countTotalVoucherByRequestIds(List.of(requestId));
    List<ProjectionStatus> statuses = voucherRepository
        .countStatusByRequestIds(List.of(requestId));

    VoucherRequestResponse response = VoucherMapper.toRequestResponse(entity);
    response.setTotalVoucher(totals.isEmpty() ? 0L : totals.get(0).getTotalVoucher());
    response.setStatusCounts(statuses.stream()
        .map(s -> VoucherRequestResponse.StatusCount.builder()
            .requestStatus(s.getRequestStatus())
            .count(s.getCount())
            .build())
        .toList());
    response.setVoucherDetailResponses(VoucherMapper.toDetailResponseList(detailPage.getContent()));
    return response;
  }


}
