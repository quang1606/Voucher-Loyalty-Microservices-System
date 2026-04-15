package com.example.voucherservice.service.impl;

import com.example.common.BaseException;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.entity.VoucherRequestEntity;
import com.example.voucherservice.repository.VoucherRepository;
import com.example.voucherservice.repository.VoucherRequestRepository;
import com.example.voucherservice.service.AuthorizationService;
import com.example.voucherservice.service.VoucherService;
import com.example.voucherservice.service.helper.VoucherServiceHelper;
import com.example.voucherservice.service.strategy.VoucherRequestStrategy;
import com.example.voucherservice.service.strategy.VoucherRequestStrategyFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public void createVoucher(CreateVoucherRequest request) {
        boolean isPartner = authorizationService.isPartner();
        if (!isPartner) {
            validateSystemFields(request);
        }

        VoucherRequestStrategy strategy = strategyFactory.getStrategy(request.getDiscountType());
        strategy.validateRequest(request);

        String username = authorizationService.getName();
        voucherServiceHelper.saveVoucher(request, username, isPartner);
    }

    @Override
    public void submitVoucher(Long id) {
        VoucherRequestEntity entity = voucherServiceHelper
                .findRequestByIdAndStatus(id, RequestStatus.INIT);
        entity.setStatus(RequestStatus.PENDING_APPROVE);
        entity.setUpdatedBy(authorizationService.getName());
        voucherRequestRepository.save(entity);
    }

    @Override
    public void cancelVoucher(Long id) {
        VoucherRequestEntity entity = voucherServiceHelper
                .findRequestByIdAndStatus(id, RequestStatus.INIT);
        entity.setStatus(RequestStatus.CANCELLED);
        entity.setUpdatedBy(authorizationService.getName());
      voucherRequestRepository.save(entity);
    }

    @Override
    @Transactional
    public void confirmVoucher(Long id, String action) {
        if (!"APPROVED".equalsIgnoreCase(action) && !"REJECTED".equalsIgnoreCase(action)) {
            log.warn("Invalid action received: {}", action);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("INVALID_ACTION")
                    .description("Action must be APPROVED or REJECTED")
                    .build();
        }

        VoucherRequestEntity entity = voucherServiceHelper
                .findRequestByIdAndStatus(id, RequestStatus.PENDING_APPROVE);

        if ("REJECTED".equalsIgnoreCase(action)) {
            handleRejected(entity);
        } else {
            handleApproved(entity);
        }
    }

    private void handleApproved(VoucherRequestEntity entity) {
        // TODO: implement approved logic
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
            List<VoucherDetailEntity> batch = voucherRepository
                    .findByRequestIdAndRequestStatusAndIdGreaterThanOrderByIdAsc(
                            entity.getRequestId(), RequestStatus.INIT, nextId,
                            PageRequest.of(0, BATCH_SIZE));

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

            log.info("Rejected batch #{}: {} items, totalProcessed: {}",
                    batchNumber, batch.size(), totalProcessed);

            if (batch.size() < BATCH_SIZE) {
                log.info("Last batch processed, breaking loop");
                break;
            }
        }

        log.info("Rejected all voucher details for requestId: {}, total: {}",
                entity.getRequestId(), totalProcessed);
    }

    private void validateSystemFields(CreateVoucherRequest request) {
        if (request.getVoucherPurpose() == null) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("MISSING_VOUCHER_PURPOSE")
                    .description("Voucher purpose is required for system creator")
                    .build();
        }
        if (request.getCustomerTier() == null) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("MISSING_CUSTOMER_TIER")
                    .description("Customer tier is required for system creator")
                    .build();
        }
    }
}
