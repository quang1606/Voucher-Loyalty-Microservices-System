package com.example.customerservice.service.impl;

import com.example.common.BaseException;
import com.example.customerservice.entity.CustomerProfile;
import com.example.customerservice.entity.CustomerVoucher;
import com.example.customerservice.grpc.VoucherGrpcClient;
import com.example.customerservice.repository.CustomerProfileRepository;
import com.example.customerservice.repository.CustomerVoucherRepository;
import com.example.customerservice.service.AuthorizationService;
import com.example.customerservice.service.CustomerVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.voucher.entity.SearchVoucherResponse;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerVoucherServiceImpl implements CustomerVoucherService {

    private final CustomerVoucherRepository customerVoucherRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final VoucherGrpcClient voucherGrpcClient;
    private final AuthorizationService authorizationService;

    @Override
    public List<CustomerVoucher> getMyVouchers(Long customerId) {
        return customerVoucherRepository.findByCustomerId(customerId);
    }

    @Override
    public SearchVoucherResponse getAvailableVouchers(int page, int size) {
        String userId = authorizationService.getUserId();
        CustomerProfile profile = customerProfileRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode("CUSTOMER_NOT_FOUND")
                        .description("Customer profile not found")
                        .build());

        return voucherGrpcClient.searchVouchers(profile.getTier().name(), page, size, "createdAt,desc");
    }
}
