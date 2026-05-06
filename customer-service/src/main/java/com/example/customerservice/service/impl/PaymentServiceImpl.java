package com.example.customerservice.service.impl;

import com.example.common.BaseException;
import com.example.customerservice.constant.DiscountType;
import com.example.customerservice.dto.event.LoyaltyPointEvent;
import com.example.customerservice.dto.event.VoucherUsedEvent;
import com.example.customerservice.dto.request.KafkaRequest;
import com.example.customerservice.dto.request.PaymentRequest;
import com.example.customerservice.dto.response.PaymentResponse;
import com.example.customerservice.dto.response.VoucherApplyResult;
import com.example.customerservice.constant.CustomerMissionStatus;
import com.example.customerservice.constant.TargetType;
import com.example.customerservice.entity.CustomerMission;
import com.example.customerservice.entity.CustomerProfile;
import com.example.customerservice.entity.CustomerVoucher;
import com.example.customerservice.entity.Transaction;
import com.example.customerservice.grpc.MissionGrpcClient;
import com.example.customerservice.grpc.VoucherGrpcClient;
import com.example.customerservice.repository.CustomerMissionRepository;
import com.example.customerservice.repository.CustomerProfileRepository;
import com.example.customerservice.repository.CustomerVoucherRepository;
import com.example.customerservice.repository.TransactionRepository;
import com.example.customerservice.service.AuthorizationService;
import com.example.customerservice.service.KafkaService;
import com.example.customerservice.service.LeaderboardService;
import com.example.customerservice.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.grpc.loyalty.entity.GetMissionByIdResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final TransactionRepository transactionRepository;
    private final CustomerVoucherRepository customerVoucherRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final CustomerMissionRepository customerMissionRepository;
    private final VoucherGrpcClient voucherGrpcClient;
    private final MissionGrpcClient missionGrpcClient;
    private final AuthorizationService authorizationService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaService kafkaService;
    private final ObjectMapper objectMapper;
    private final LeaderboardService leaderboardService;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        String userId = authorizationService.getUserId();
        CustomerProfile profile = customerProfileRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode("CUSTOMER_NOT_FOUND")
                        .description("Customer profile not found")
                        .build());

        String transactionId = "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        
        BigDecimal originalAmount = request.getOrderAmount();
        VoucherApplyResult voucherResult = validateAndApplyVoucher(request, profile, originalAmount);

        BigDecimal finalAmount = originalAmount.subtract(voucherResult.getDiscountAmount());
        
        // Calculate loyalty points (1 point per 1000 VND)
        Integer pointsEarned = finalAmount.divide(new BigDecimal("1000"), RoundingMode.DOWN).intValue();

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setCustomerId(profile.getId());
        transaction.setInvoiceId(request.getInvoiceId());
        transaction.setVoucherId(request.getVoucherId());
        transaction.setVoucherCode(voucherResult.getVoucherCode());
        transaction.setOriginalAmount(originalAmount);
        transaction.setDiscountAmount(voucherResult.getDiscountAmount());
        transaction.setFinalAmount(finalAmount);
        transaction.setPointsEarned(pointsEarned);
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        
        transactionRepository.save(transaction);

        profile.setTotalPoints(profile.getTotalPoints() + pointsEarned);
        customerProfileRepository.save(profile);

        // Update leaderboard in Redis
        leaderboardService.updateCustomerPoints(profile.getId(), pointsEarned);

        // Update customer missions
        updateCustomerMissions(profile.getId(), finalAmount);

        if (request.getVoucherId() != null) {
            reduceVoucherStockInRedis(voucherResult.getVoucherCode());
            sendVoucherUsedEvent(transactionId, request.getVoucherId(), voucherResult.getVoucherCode(), profile.getId(), originalAmount, voucherResult.getDiscountAmount());
        }

        sendLoyaltyPointEvent(transactionId, profile.getId(), finalAmount, pointsEarned);

        log.info("Payment processed successfully - transactionId: {}, finalAmount: {}, pointsEarned: {}", 
                transactionId, finalAmount, pointsEarned);

        return PaymentResponse.builder()
                .transactionId(transactionId)
                .originalAmount(originalAmount)
                .discountAmount(voucherResult.getDiscountAmount())
                .finalAmount(finalAmount)
                .pointsEarned(pointsEarned)
                .status("SUCCESS")
                .build();
    }

    private BigDecimal calculateDiscount(vn.com.grpc.voucher.entity.VoucherDetail voucherDetail, BigDecimal orderAmount) {
        BigDecimal discount = BigDecimal.ZERO;
        
        if ("PERCENT".equals(voucherDetail.getDiscountType().name())) {
            discount = orderAmount.multiply(new BigDecimal(voucherDetail.getDiscountValue()))
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            
            BigDecimal maxDiscount = new BigDecimal(voucherDetail.getMaxDiscount());
            if (discount.compareTo(maxDiscount) > 0) {
                discount = maxDiscount;
            }
        } else if ("FIXED".equals(voucherDetail.getDiscountType().name())) {
            discount = new BigDecimal(voucherDetail.getDiscountValue());
        }
        
        return discount;
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

    private void reduceVoucherStockInRedis(String voucherCode) {
        try {
            String key = String.format("voucher:%s:stock", voucherCode);
            redisTemplate.opsForValue().decrement(key);
            log.debug("Reduced voucher stock in Redis for code: {}", voucherCode);
        } catch (Exception ex) {
            log.error("Failed to reduce voucher stock in Redis for code {}: {}", voucherCode, ex.getMessage());
        }
    }

    private void sendVoucherUsedEvent(String transactionId, Long voucherId, String voucherCode,
                                    Long customerId, BigDecimal orderAmount, BigDecimal discountAmount) {
        try {
            VoucherUsedEvent event = VoucherUsedEvent.builder()
                    .transactionId(transactionId)
                    .voucherId(voucherId)
                    .voucherCode(voucherCode)
                    .customerId(customerId)
                    .orderAmount(orderAmount)
                    .discountAmount(discountAmount)
                    .build();

            KafkaRequest kafkaRequest = KafkaRequest.builder()
                    .topic("voucher-used-topic")
                    .key(voucherCode)
                    .payload(objectMapper.writeValueAsString(event))
                    .build();

            kafkaService.send(kafkaRequest);
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize voucher used event: {}", ex.getMessage());
        }
    }

    private void sendLoyaltyPointEvent(String transactionId, Long customerId,
                                     BigDecimal orderAmount, Integer pointsEarned) {
        try {
            LoyaltyPointEvent event = LoyaltyPointEvent.builder()
                    .transactionId(transactionId)
                    .customerId(customerId)
                    .orderAmount(orderAmount)
                    .pointsEarned(pointsEarned)
                    .build();

            KafkaRequest kafkaRequest = KafkaRequest.builder()
                    .topic("loyalty-point-topic")
                    .key(customerId.toString())
                    .payload(objectMapper.writeValueAsString(event))
                    .build();

            kafkaService.send(kafkaRequest);
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize loyalty point event: {}", ex.getMessage());
        }
    }

    @Async
     void updateCustomerMissions(Long customerId, BigDecimal orderAmount) {
        try {
            // Get all active missions from loyalty-service
            vn.com.grpc.loyalty.entity.SearchMissionResponse allMissions = 
                    missionGrpcClient.getMissions(0, 100);
            
            for (vn.com.grpc.loyalty.entity.MissionInfo missionInfo : allMissions.getMissionsList()) {
                // Create CustomerMission if not exists
                createCustomerMissionIfNotExists(customerId, missionInfo.getMissionId(), 
                        (int) missionInfo.getTargetValue());
            }
            
            // Update progress for existing missions
            List<CustomerMission> inProgressMissions = customerMissionRepository
                    .findByCustomerIdAndStatus(customerId, CustomerMissionStatus.IN_PROGRESS);

            for (CustomerMission mission : inProgressMissions) {
                updateMissionProgress(mission, orderAmount);
            }
        } catch (Exception ex) {
            log.error("Failed to update customer missions for customerId {}: {}", customerId, ex.getMessage());
        }
    }

    private void updateMissionProgress(CustomerMission mission, BigDecimal orderAmount) {
        try {
            GetMissionByIdResponse missionResponse = 
                    missionGrpcClient.getMissionById(mission.getMissionId());
            
            int currentProgress = mission.getCurrentProgress();
            int progressIncrement;
            
            if ("AMOUNT".equals(missionResponse.getTargetType().name())) {
                progressIncrement = orderAmount.intValue();
            } else if ("COUNT".equals(missionResponse.getTargetType().name())) {
                progressIncrement = 1;
            } else {
                log.warn("Unknown target type for mission {}: {}", 
                        mission.getMissionId(), missionResponse.getTargetType().name());
                return;
            }
            
            int newProgress = currentProgress + progressIncrement;
            mission.setCurrentProgress(newProgress);
            
            if (newProgress >= mission.getTargetValue()) {
                mission.setStatus(CustomerMissionStatus.COMPLETED);
                log.info("Mission completed - customerId: {}, missionId: {}, type: {}, progress: {}/{}", 
                        mission.getCustomerId(), mission.getMissionId(), missionResponse.getTargetType().name(),
                        newProgress, mission.getTargetValue());
            }
            
            customerMissionRepository.save(mission);
            log.debug("Updated mission progress - customerId: {}, missionId: {}, type: {}, progress: {}/{}", 
                    mission.getCustomerId(), mission.getMissionId(), missionResponse.getTargetType().name(),
                    newProgress, mission.getTargetValue());
                    
        } catch (Exception missionEx) {
            log.error("Failed to get mission details for missionId {}: {}", 
                    mission.getMissionId(), missionEx.getMessage());
        }
    }

    private void createCustomerMissionIfNotExists(Long customerId, Long missionId, Integer targetValue) {
        boolean exists = customerMissionRepository
                .findByCustomerIdAndMissionId(customerId, missionId).isPresent();
        
        if (!exists) {
            CustomerMission customerMission = new CustomerMission();
            customerMission.setCustomerId(customerId);
            customerMission.setMissionId(missionId);
            customerMission.setCurrentProgress(0);
            customerMission.setTargetValue(targetValue);
            customerMission.setStatus(CustomerMissionStatus.IN_PROGRESS);
            customerMission.setStartedAt(java.time.LocalDateTime.now());
            
            customerMissionRepository.save(customerMission);
            log.info("Created new customer mission - customerId: {}, missionId: {}", customerId, missionId);
        }
    }

    private VoucherApplyResult validateAndApplyVoucher(PaymentRequest request, CustomerProfile profile, BigDecimal originalAmount) {
        if (request.getVoucherId() == null) {
            return VoucherApplyResult.builder()
                    .discountAmount(BigDecimal.ZERO)
                    .voucherCode(null)
                    .build();
        }

        CustomerVoucher customerVoucher = customerVoucherRepository
                .findByCustomerIdAndVoucherId(profile.getId(), request.getVoucherId())
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .errorCode("VOUCHER_NOT_FOUND")
                        .description("Voucher not found for this customer")
                        .build());

        vn.com.grpc.voucher.entity.GetVoucherByIdResponse voucherResponse = 
                voucherGrpcClient.getVoucherById(request.getVoucherId());
        vn.com.grpc.voucher.entity.VoucherDetail voucherDetail = voucherResponse.getVoucher();

        // Validate voucher stock from Redis
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

        // Validate minimum order
      if (voucherDetail.getDiscountType().name().equals(DiscountType.RECENT.name())) {
        BigDecimal minOrder = new BigDecimal(voucherDetail.getMinOrderValue());
        if (originalAmount.compareTo(minOrder) < 0) {
          throw BaseException.builder()
              .httpStatus(HttpStatus.BAD_REQUEST)
              .errorCode("MIN_ORDER_NOT_MET")
              .description("Order amount does not meet minimum requirement: " + minOrder)
              .build();
        }
      }

        // Calculate discount
        BigDecimal discountAmount = calculateDiscount(voucherDetail, originalAmount);
        
        return VoucherApplyResult.builder()
                .discountAmount(discountAmount)
                .voucherCode(voucherDetail.getVoucherCode())
                .build();
    }
}