package com.example.customerservice.service.impl;

import com.example.common.BaseException;
import com.example.customerservice.constant.CustomerVoucherStatus;
import com.example.customerservice.dto.response.CustomerVoucherListResponse;
import com.example.customerservice.dto.response.CustomerVoucherResponse;
import com.example.customerservice.entity.CustomerProfile;
import com.example.customerservice.entity.CustomerVoucher;
import com.example.customerservice.grpc.VoucherGrpcClient;
import com.example.customerservice.mapper.CustomerVoucherMapper;
import com.example.customerservice.repository.CustomerProfileRepository;
import com.example.customerservice.repository.CustomerVoucherRepository;
import com.example.customerservice.service.AuthorizationService;
import com.example.customerservice.service.CustomerVoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.voucher.entity.SearchVoucherResponse;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerVoucherServiceImpl implements CustomerVoucherService {

    private final CustomerVoucherRepository customerVoucherRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final VoucherGrpcClient voucherGrpcClient;
    private final AuthorizationService authorizationService;

    @Override
    public List<CustomerVoucherResponse> getMyVouchers(Long customerId) {
        List<CustomerVoucher> vouchers = customerVoucherRepository.findByCustomerId(customerId);
        return CustomerVoucherMapper.toResponseList(vouchers);
    }


    @Override
    public SearchVoucherResponse getAvailableVouchersWithCollectedStatus(Long customerId, int page, int size) {
        String userId = authorizationService.getUserId();
        CustomerProfile profile = customerProfileRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode("CUSTOMER_NOT_FOUND")
                        .description("Customer profile not found")
                        .build());

        SearchVoucherResponse response = voucherGrpcClient.searchVouchers(profile.getTier().name(), page, size, "createdAt,desc");
        
        // Kiểm tra trạng thái đã lấy voucher cho từng voucher
        if (response.getVouchersList() != null) {
            response.getVouchersList().forEach(voucher -> {
                boolean isCollected = customerVoucherRepository.findByCustomerIdAndVoucherId(customerId, voucher.getId()).isPresent();
            });
        }

        return response;
    }

    @Override
    public CustomerVoucherListResponse getCustomerVouchers(Long customerId, Long voucherId, 
                                                         CustomerVoucherStatus status,
                                                         Pageable pageable) {
        log.info("Getting customer vouchers - customerId: {}, voucherId: {}, status: {}, page: {}, size: {}",
                customerId, voucherId, status, pageable.getPageNumber(), pageable.getPageSize());

        Page<CustomerVoucher> voucherPage = customerVoucherRepository.findByFilters(
                customerId, voucherId, status, pageable);

        log.info("Found {} customer vouchers", voucherPage.getTotalElements());

        return CustomerVoucherListResponse.builder()
                .data(CustomerVoucherMapper.toResponseList(voucherPage.getContent()))
                .totalElements((int) voucherPage.getTotalElements())
                .totalPages(voucherPage.getTotalPages())
                .currentPage(voucherPage.getNumber())
                .pageSize(voucherPage.getSize())
                .build();
    }

    @Override
    public void collectVoucher(Long customerId, Long voucherId) {
        log.info("Collecting voucher - customerId: {}, voucherId: {}", customerId, voucherId);
        
        boolean alreadyCollected = customerVoucherRepository.findByCustomerIdAndVoucherId(customerId, voucherId).isPresent();
        if (alreadyCollected) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("VOUCHER_ALREADY_COLLECTED")
                    .description("Voucher has already been collected by this customer")
                    .build();
        }
        
        vn.com.grpc.voucher.entity.GetVoucherByIdResponse voucherResponse = voucherGrpcClient.getVoucherById(voucherId);
        vn.com.grpc.voucher.entity.VoucherDetail voucherDetail = voucherResponse.getVoucher();

        CustomerVoucher customerVoucher = new CustomerVoucher();
        customerVoucher.setCustomerId(customerId);
        customerVoucher.setVoucherId(voucherId);
        customerVoucher.setAvailableUsage(voucherDetail.getMaxCollect());
        customerVoucher.setVoucherCode(voucherDetail.getVoucherCode());
        customerVoucher.setMerchantId(voucherDetail.getMerchantId());
        customerVoucher.setStatus(CustomerVoucherStatus.AVAILABLE);
        customerVoucher.setObtainedAt(java.time.LocalDateTime.now());
        
        customerVoucherRepository.save(customerVoucher);
        log.info("Voucher collected successfully - customerId: {}, voucherId: {}, voucherCode: {}",
                customerId, voucherId, voucherDetail.getVoucherCode());
    }
}