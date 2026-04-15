package com.example.voucherservice.service.impl;

import com.example.common.BaseException;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
import com.example.voucherservice.entity.VoucherRequestEntity;
import com.example.voucherservice.repository.VoucherRequestRepository;
import com.example.voucherservice.service.AuthorizationService;
import com.example.voucherservice.service.VoucherService;
import com.example.voucherservice.service.helper.VoucherServiceHelper;
import com.example.voucherservice.service.strategy.VoucherRequestStrategy;
import com.example.voucherservice.service.strategy.VoucherRequestStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRequestStrategyFactory strategyFactory;
    private final VoucherServiceHelper voucherServiceHelper;
    private final AuthorizationService authorizationService;
    private final VoucherRequestRepository voucherRequestRepository;

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
        // TODO: implement rejected logic
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
