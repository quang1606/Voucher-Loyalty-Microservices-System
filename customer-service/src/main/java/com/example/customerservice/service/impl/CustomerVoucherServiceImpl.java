package com.example.customerservice.service.impl;

import com.example.common.BaseException;
import com.example.customerservice.constant.CreatorType;
import com.example.customerservice.constant.CustomerVoucherStatus;
import com.example.customerservice.dto.response.ApplicableVoucherListResponse;
import com.example.customerservice.dto.response.ApplicableVoucherResponse;
import com.example.customerservice.dto.response.AvailableVoucherListResponse;
import com.example.customerservice.dto.response.AvailableVoucherResponse;
import com.example.customerservice.dto.response.CustomerVoucherListResponse;
import com.example.customerservice.entity.CustomerProfile;
import com.example.customerservice.entity.CustomerVoucher;
import com.example.customerservice.grpc.VoucherGrpcClient;
import com.example.customerservice.mapper.CustomerVoucherMapper;
import com.example.customerservice.repository.CustomerProfileRepository;
import com.example.customerservice.repository.CustomerVoucherRepository;
import com.example.customerservice.service.AuthorizationService;
import com.example.customerservice.service.CustomerVoucherService;
import com.example.customerservice.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.voucher.entity.SearchVoucherResponse;
import vn.com.grpc.voucher.entity.VoucherInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerVoucherServiceImpl implements CustomerVoucherService {

    private final CustomerVoucherRepository customerVoucherRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final VoucherGrpcClient voucherGrpcClient;
    private final AuthorizationService authorizationService;
    private final RedisTemplate<String, Object> redisTemplate;




    @Override
    public AvailableVoucherListResponse getAvailableVouchersWithCollectedStatus(Long customerId, int page, int size) {
        String userId = authorizationService.getUserId();
        CustomerProfile profile = customerProfileRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode("CUSTOMER_NOT_FOUND")
                        .description("Customer profile not found")
                        .build());

        SearchVoucherResponse grpcResponse = voucherGrpcClient.searchVouchers(profile.getTier().name(), page, size, "createdAt,desc");

        List<AvailableVoucherResponse> vouchers = grpcResponse.getVouchersList().stream()
                .map(v -> toAvailableVoucherResponse(v, customerId))
                .collect(Collectors.toList());

        return AvailableVoucherListResponse.builder()
                .data(vouchers)
                .totalElements(grpcResponse.getTotalElements())
                .totalPages(grpcResponse.getTotalPages())
                .currentPage(page)
                .pageSize(size)
                .build();
    }

    private AvailableVoucherResponse toAvailableVoucherResponse(VoucherInfo v, Long customerId) {
        boolean isCollected = customerVoucherRepository.findByCustomerIdAndVoucherId(customerId, v.getId()).isPresent();
        return AvailableVoucherResponse.builder()
                .id(v.getId())
                .voucherCode(v.getVoucherCode())
                .voucherName(v.getVoucherName())
                .description(v.getDescription())
                .customerTier(v.getCustomerTier())
                .discountType(v.getDiscountType().name())
                .discountValue(v.getDiscountValue())
                .maxDiscount(v.getMaxDiscount())
                .minOrderValue(v.getMinOrderValue())
                .totalStock(v.getTotalStock())
                .availableStock(v.getAvailableStock())
                .maxCollect(v.getMaxCollect())
                .startDate(TimestampUtils.convertMillisToString(v.getStartDate()))
                .endDate(TimestampUtils.convertMillisToString(v.getEndDate()))
                .status(v.getStatus())
                .createdAt(TimestampUtils.convertMillisToString(v.getCreatedAt()))
                .collected(isCollected)
                .build();

    }

    @Override
    public AvailableVoucherListResponse getCustomerVouchers(Long customerId, Long voucherId, 
                                                         CustomerVoucherStatus status,
                                                         Pageable pageable) {
        log.info("Getting customer vouchers - customerId: {}, voucherId: {}, status: {}, page: {}, size: {}",
                customerId, voucherId, status, pageable.getPageNumber(), pageable.getPageSize());

        Page<CustomerVoucher> voucherPage = customerVoucherRepository.findByFilters(
                customerId, voucherId, status, pageable);

        log.info("Found {} customer vouchers", voucherPage.getTotalElements());

        // Fetch voucher details via gRPC
        List<AvailableVoucherResponse> voucherResponses = new ArrayList<>();
        for (CustomerVoucher cv : voucherPage.getContent()) {
            try {
                vn.com.grpc.voucher.entity.GetVoucherByIdResponse grpcResponse =
                        voucherGrpcClient.getVoucherById(cv.getVoucherId());
                vn.com.grpc.voucher.entity.VoucherDetail detail = grpcResponse.getVoucher();

                AvailableVoucherResponse voucherResponse = AvailableVoucherResponse.builder()
                        .id(cv.getVoucherId())
                        .voucherCode(cv.getVoucherCode())
                        .voucherStatus(cv.getStatus())
                        .availableUsage(cv.getAvailableUsage())
                        .voucherName(detail.getVoucherName())
                        .description(detail.getDescription())
                        .customerTier(detail.getCustomerTier())
                        .discountType(detail.getDiscountType().name())
                        .discountValue(detail.getDiscountValue())
                        .maxDiscount(detail.getMaxDiscount())
                        .minOrderValue(detail.getMinOrderValue())
                        .totalStock(detail.getTotalStock())
                        .availableStock(detail.getAvailableStock())
                        .maxCollect(detail.getMaxCollect())
                        .startDate(TimestampUtils.convertMillisToString(detail.getStartDate()))
                        .endDate(TimestampUtils.convertMillisToString(detail.getEndDate()))
                        .status(detail.getStatus())
                        .createdAt(TimestampUtils.convertMillisToString(detail.getCreatedAt()))
                        .collected(true) // Always true for customer's collected vouchers
                        .build();

                voucherResponses.add(voucherResponse);
            } catch (Exception e) {
                log.warn("Failed to get voucher detail for voucherId: {}, skip", cv.getVoucherId());
            }
        }

        return AvailableVoucherListResponse.builder()
                .data(voucherResponses)
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

        // Check stock from Redis
        Integer availableStock = getVoucherStockFromRedis(voucherDetail.getVoucherCode());
        if (availableStock == null) {
            availableStock = voucherDetail.getAvailableStock();
        }
        
        if (availableStock <= 0) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("VOUCHER_OUT_OF_STOCK")
                    .description("Voucher is out of stock")
                    .build();
        }

        // Calculate actual available usage based on remaining stock
        int maxCollect = voucherDetail.getMaxCollect();
        int actualAvailableUsage = Math.min(maxCollect, availableStock);

        CustomerVoucher customerVoucher = new CustomerVoucher();
        customerVoucher.setCustomerId(customerId);
        customerVoucher.setVoucherId(voucherId);
        customerVoucher.setAvailableUsage(actualAvailableUsage);
        customerVoucher.setVoucherCode(voucherDetail.getVoucherCode());
        customerVoucher.setNameStore(voucherDetail.getNameStore());
        customerVoucher.setCreatorType(CreatorType.valueOf(voucherDetail.getCreatorType().name()));
        customerVoucher.setStatus(CustomerVoucherStatus.AVAILABLE);
        customerVoucher.setExpiredAt(TimestampUtils.convertMillisToLocalDateTime(voucherDetail.getEndDate()));
        customerVoucher.setObtainedAt(java.time.LocalDateTime.now());
        
        customerVoucherRepository.save(customerVoucher);
        log.info("Voucher collected successfully - customerId: {}, voucherId: {}, voucherCode: {}, availableUsage: {}",
                customerId, voucherId, voucherDetail.getVoucherCode(), actualAvailableUsage);
    }

    @Override
    public ApplicableVoucherListResponse getApplicableVouchers(Long customerId, String nameStore, BigDecimal orderAmount) {
        log.info("Getting applicable vouchers - customerId: {}, nameStore: {}, orderAmount: {}",
                customerId, nameStore, orderAmount);

        List<CustomerVoucher> customerVouchers = customerVoucherRepository
                .findAvailableByCustomerAndStore(customerId, nameStore, CreatorType.SYSTEM);

        List<ApplicableVoucherResponse> results = new ArrayList<>();

        for (CustomerVoucher cv : customerVouchers) {
            try {
                vn.com.grpc.voucher.entity.GetVoucherByIdResponse grpcResponse =
                        voucherGrpcClient.getVoucherById(cv.getVoucherId());
                vn.com.grpc.voucher.entity.VoucherDetail detail = grpcResponse.getVoucher();

                boolean applicable = true;
                String reason = null;

                Integer availableStock = getVoucherStockFromRedis(detail.getVoucherCode());
                if (availableStock != null && availableStock <= 0) {
                    applicable = false;
                    reason = "Voucher đã hết lượt sử dụng";
                } else if (availableStock == null && detail.getAvailableStock() <= 0) {
                    applicable = false;
                    reason = "Voucher đã hết lượt sử dụng";
                } else {
                    BigDecimal minOrder = new BigDecimal(detail.getMinOrderValue());
                    if (orderAmount.compareTo(minOrder) < 0) {
                        applicable = false;
                        reason = "Đơn hàng tối thiểu " + detail.getMinOrderValue();
                    }
                }

                results.add(ApplicableVoucherResponse.builder()
                        .voucherId(cv.getVoucherId())
                        .voucherCode(cv.getVoucherCode())
                        .voucherName(detail.getVoucherName())
                        .description(detail.getDescription())
                        .discountType(detail.getDiscountType().name())
                        .discountValue(detail.getDiscountValue())
                        .maxDiscount(detail.getMaxDiscount())
                        .minOrderValue(detail.getMinOrderValue())
                        .availableStock(detail.getAvailableStock())
                        .nameStore(detail.getNameStore())
                        .creatorType(detail.getCreatorType().name())
                        .applicable(applicable)
                        .reason(reason)
                        .build());
            } catch (Exception e) {
                log.warn("Failed to get voucher detail for voucherId: {}, skip", cv.getVoucherId());
            }
        }

        results.sort((a, b) -> Boolean.compare(b.isApplicable(), a.isApplicable()));

        return ApplicableVoucherListResponse.builder()
                .data(results)
                .totalElements(results.size())
                .totalPages(1)
                .currentPage(0)
                .pageSize(results.size())
                .build();
    }

    private Integer getVoucherStockFromRedis(String voucherCode) {
        try {
            String key = String.format("voucher:%s:stock", voucherCode);
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? (Integer) value : null;
        } catch (Exception ex) {
            log.warn("Failed to get voucher stock from Redis for code {}: {}", voucherCode, ex.getMessage());
            return null;
        }
    }
}