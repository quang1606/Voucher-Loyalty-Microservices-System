package com.example.voucherservice.service.helper;

import com.example.voucherservice.constant.CreatorType;
import com.example.voucherservice.constant.CustomerTier;
import com.example.voucherservice.constant.RequestMode;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherPurpose;
import com.example.voucherservice.constant.VoucherStatus;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.entity.VoucherRequestEntity;
import com.example.voucherservice.repository.VoucherRepository;
import com.example.voucherservice.repository.VoucherRequestRepository;
import jakarta.transaction.Transactional;
import com.example.common.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceHelper {

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
    public void saveVoucher(CreateVoucherRequest request, String username, boolean isPartner) {
        try {
          String requestId = "VOUCHER_" + System.currentTimeMillis();

            VoucherRequestEntity requestEntity = new VoucherRequestEntity();
            requestEntity.setRequestId(requestId);
            requestEntity.setRequestMode(RequestMode.SINGLE);
            requestEntity.setCreatorType(isPartner ? CreatorType.PARTNER : CreatorType.SYSTEM);
            requestEntity.setVoucherPurpose(isPartner ? VoucherPurpose.HUNT : request.getVoucherPurpose());
            requestEntity.setStatus(RequestStatus.INIT);
            requestEntity.setCreatedBy(username);
            voucherRequestRepository.save(requestEntity);

            VoucherDetailEntity voucherDetailEntity = new VoucherDetailEntity();
            voucherDetailEntity.setRequestId(requestId);
            voucherDetailEntity.setPartnerId(isPartner ? username : null);
            voucherDetailEntity.setCustomerTier(isPartner ? CustomerTier.ALL : request.getCustomerTier());
            voucherDetailEntity.setVoucherName(request.getVoucherName());
            voucherDetailEntity.setDescription(request.getDescription());
            voucherDetailEntity.setDiscountType(request.getDiscountType());
            voucherDetailEntity.setDiscountValue(request.getDiscountValue());
            voucherDetailEntity.setMaxDiscount(request.getMaxDiscount());
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
