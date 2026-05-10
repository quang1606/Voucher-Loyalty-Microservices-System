package com.example.customerservice.service.impl;

import com.example.common.BaseException;
import com.example.customerservice.constant.CreatorType;
import com.example.customerservice.constant.CustomerMissionStatus;
import com.example.customerservice.constant.CustomerVoucherStatus;
import com.example.customerservice.dto.event.LoyaltyPointEvent;
import com.example.customerservice.dto.request.ClaimMissionRewardResponse;
import com.example.customerservice.dto.request.KafkaRequest;
import com.example.customerservice.dto.response.MissionResponse;
import com.example.customerservice.entity.CustomerMission;
import com.example.customerservice.entity.CustomerProfile;
import com.example.customerservice.entity.CustomerVoucher;
import com.example.customerservice.grpc.MissionGrpcClient;
import com.example.customerservice.grpc.VoucherGrpcClient;
import com.example.customerservice.repository.CustomerMissionRepository;
import com.example.customerservice.repository.CustomerProfileRepository;
import com.example.customerservice.repository.CustomerVoucherRepository;
import com.example.customerservice.service.AuthorizationService;
import com.example.customerservice.service.CustomerMissionService;
import com.example.customerservice.service.KafkaService;
import com.example.customerservice.service.LeaderboardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.grpc.loyalty.entity.SearchMissionResponse;
import vn.com.grpc.voucher.entity.GetVoucherByRequestIdResponse;
import vn.com.grpc.voucher.entity.VoucherDetail;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerMissionServiceImpl implements CustomerMissionService {

    private final MissionGrpcClient missionGrpcClient;
    private final CustomerMissionRepository customerMissionRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final CustomerVoucherRepository customerVoucherRepository;
    private final VoucherGrpcClient voucherGrpcClient;
    private final AuthorizationService authorizationService;
    private final KafkaService kafkaService;
    private final ObjectMapper objectMapper;
    private final LeaderboardService leaderboardService;

    @Override
    public MissionResponse getCustomerMissions(int page, int size) {
        String userId = authorizationService.getUserId();
        CustomerProfile profile = customerProfileRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode("CUSTOMER_NOT_FOUND")
                        .description("Customer profile not found")
                        .build());

        // Get all missions from loyalty-service
        SearchMissionResponse grpcResponse = missionGrpcClient.getMissions(page, size);

        // Get customer mission progress
        List<CustomerMission> customerMissions = customerMissionRepository.findByCustomerId(profile.getId());
        Map<Long, CustomerMission> missionProgressMap = customerMissions.stream()
                .collect(Collectors.toMap(CustomerMission::getMissionId, cm -> cm));

        // Merge mission info with progress
        List<MissionResponse.MissionInfo> missionInfos = grpcResponse.getMissionsList().stream()
                .map(grpcMission -> {
                    CustomerMission customerMission = missionProgressMap.get(grpcMission.getMissionId());

                    return MissionResponse.MissionInfo.builder()
                            .missionId(grpcMission.getMissionId())
                            .missionName(grpcMission.getMissionName())
                            .missionDescription(grpcMission.getMissionDescription())
                            .targetValue(grpcMission.getTargetValue())
                            .targetType(grpcMission.getTargetType().name())
                            .rewardType(grpcMission.getRewardType().name())
                            .rewardValue(grpcMission.getRewardValue())
                            .partnerId(grpcMission.getPartnerId())
                            .startDate(grpcMission.getStartDate())
                            .endDate(grpcMission.getEndDate())
                            .taskStatus(grpcMission.getTaskStatus().name())
                            .currentProgress(customerMission != null ? customerMission.getCurrentProgress() : 0)
                            .status(customerMission != null ? customerMission.getStatus() : CustomerMissionStatus.IN_PROGRESS)
                            .build();
                })
                .collect(Collectors.toList());

        return MissionResponse.builder()
                .missions(missionInfos)
                .totalElements(grpcResponse.getTotalElements())
                .totalPages(grpcResponse.getTotalPages())
                .currentPage(page)
                .pageSize(size)
                .build();
    }

    @Override
    public ClaimMissionRewardResponse claimMissionReward(Long missionId) {
        String userId = authorizationService.getUserId();
        CustomerProfile profile = customerProfileRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode("CUSTOMER_NOT_FOUND")
                        .description("Customer profile not found")
                        .build());

        // Get customer mission
        CustomerMission customerMission = customerMissionRepository
                .findByCustomerIdAndMissionId(profile.getId(), missionId)
                .orElseThrow(() -> BaseException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .errorCode("CUSTOMER_MISSION_NOT_FOUND")
                        .description("Customer mission not found")
                        .build());

        // Check if mission is completed
        if (!CustomerMissionStatus.COMPLETED.equals(customerMission.getStatus())) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("MISSION_NOT_COMPLETED")
                    .description("Mission is not completed yet")
                    .build();
        }

        // Get mission details
        vn.com.grpc.loyalty.entity.GetMissionByIdResponse missionResponse =
                missionGrpcClient.getMissionById(missionId);

        String rewardType = missionResponse.getRewardType().name();
        String rewardValue = missionResponse.getRewardValue();

        String request_Id = missionResponse.getRequestId();

        if ("POINT".equals(rewardType)) {
            return handlePointReward(customerMission, profile, rewardValue);
        } else if ("VOUCHER".equals(rewardType)) {
            return handleVoucherReward(customerMission, profile, request_Id);
        } else {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("INVALID_REWARD_TYPE")
                    .description("Invalid reward type: " + rewardType)
                    .build();
        }
    }

    private ClaimMissionRewardResponse handlePointReward(CustomerMission customerMission,
                                                         CustomerProfile profile, String rewardValue) {
        try {
            int points = Integer.parseInt(rewardValue);

            // Update customer mission status
            customerMission.setStatus(CustomerMissionStatus.CLAIMED);
            customerMissionRepository.save(customerMission);

            // Update customer profile points
            profile.setTotalPoints(profile.getTotalPoints() + points);
            customerProfileRepository.save(profile);

            // Update leaderboard in Redis
            leaderboardService.updateCustomerPoints(profile.getId(), points);

            // Send loyalty point event
            sendLoyaltyPointEvent("MISSION-" + customerMission.getId(), profile.getId(), points);

            log.info("Claimed point reward - customerId: {}, missionId: {}, points: {}",
                    profile.getId(), customerMission.getMissionId(), points);

            return ClaimMissionRewardResponse.builder()
                    .rewardType("POINT")
                    .rewardValue(rewardValue + " points")
                    .message("Successfully claimed " + points + " points")
                    .build();

        } catch (NumberFormatException ex) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("INVALID_POINT_VALUE")
                    .description("Invalid point value: " + rewardValue)
                    .build();
        }
    }

    private ClaimMissionRewardResponse handleVoucherReward(CustomerMission customerMission,
                                                           CustomerProfile profile, String request_Id) {
        log.info("check voucher 2");
        try {


            // Get voucher details from voucher-service
            GetVoucherByRequestIdResponse voucherResponse =
                    voucherGrpcClient.getVoucherByRequestId(request_Id);
            VoucherDetail voucherDetail = voucherResponse.getVoucherRequest(0);

            // Create customer voucher
            CustomerVoucher customerVoucher = new CustomerVoucher();
            customerVoucher.setCustomerId(profile.getId());
            customerVoucher.setVoucherId(voucherDetail.getId());
            customerVoucher.setAvailableUsage(voucherDetail.getMaxCollect());
            customerVoucher.setVoucherCode(voucherDetail.getVoucherCode());
            customerVoucher.setNameStore(voucherDetail.getNameStore());
            customerVoucher.setCreatorType(CreatorType.SYSTEM);
            customerVoucher.setStatus(CustomerVoucherStatus.AVAILABLE);
            customerVoucher.setObtainedAt(java.time.LocalDateTime.now());

            customerVoucherRepository.save(customerVoucher);

            // Update customer mission status
            customerMission.setStatus(CustomerMissionStatus.CLAIMED);
            customerMissionRepository.save(customerMission);

            log.info("Claimed voucher reward - customerId: {}, missionId: {}, voucherId: {}, voucherCode: {}",
                    profile.getId(), customerMission.getMissionId(), voucherDetail.getId(), voucherDetail.getVoucherCode());

            return ClaimMissionRewardResponse.builder()
                    .rewardType("VOUCHER")
                    .rewardValue(voucherDetail.getVoucherName())
                    .message("Successfully claimed voucher: " + voucherDetail.getVoucherName())
                    .build();

        } catch (NumberFormatException ex) {
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("INVALID_VOUCHER_ID")
                    .description("Invalid voucher ID: ")
                    .build();
        }
    }

    private void sendLoyaltyPointEvent(String transactionId, Long customerId, Integer pointsEarned) {
        try {
            LoyaltyPointEvent event = LoyaltyPointEvent.builder()
                    .transactionId(transactionId)
                    .customerId(customerId)
                    .orderAmount(java.math.BigDecimal.ZERO)
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
}
