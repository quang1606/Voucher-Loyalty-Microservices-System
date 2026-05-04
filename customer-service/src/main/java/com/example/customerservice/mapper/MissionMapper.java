package com.example.customerservice.mapper;

import com.example.customerservice.dto.response.MissionResponse;
import com.example.customerservice.dto.response.VoucherRequestDetail;
import com.example.customerservice.grpc.VoucherGrpcClient;
import vn.com.grpc.loyalty.entity.SearchMissionResponse;
import vn.com.grpc.loyalty.entity.MissionInfo;
import vn.com.grpc.voucher.entity.GetVoucherByRequestIdResponse;

import java.util.List;
import java.util.stream.Collectors;

public final class MissionMapper {

    private MissionMapper() {
    }

    public static MissionResponse toMissionResponse(SearchMissionResponse grpcResponse, int page, int size, VoucherGrpcClient voucherGrpcClient) {
        List<MissionResponse.MissionInfo> missions = grpcResponse.getMissionsList()
                .stream()
                .map(mission -> toMissionInfo(mission, voucherGrpcClient))
                .collect(Collectors.toList());

        return MissionResponse.builder()
                .missions(missions)
                .totalElements(grpcResponse.getTotalElements())
                .totalPages(grpcResponse.getTotalPages())
                .currentPage(page)
                .pageSize(size)
                .build();
    }

    private static MissionResponse.MissionInfo toMissionInfo(MissionInfo grpcMission, VoucherGrpcClient voucherGrpcClient) {
        VoucherRequestDetail voucherRequest = null;
        if (grpcMission.getRequestId() != null && !grpcMission.getRequestId().isEmpty()) {
            try {
                GetVoucherByRequestIdResponse voucherResponse = voucherGrpcClient.getVoucherByRequestId(grpcMission.getRequestId());
                voucherRequest = toVoucherRequestDetail(voucherResponse.getVoucherRequest());
            } catch (Exception e) {
                // Log error but continue processing
                System.err.println("Failed to get voucher details for requestId: " + grpcMission.getRequestId() + ", error: " + e.getMessage());
            }
        }

        return MissionResponse.MissionInfo.builder()
                .missionId(grpcMission.getMissionId())
                .missionName(grpcMission.getMissionName())
                .missionDescription(grpcMission.getMissionDescription())
                .targetValue(grpcMission.getTargetValue())
                .rewardType(grpcMission.getRewardType().name())
                .rewardValue(grpcMission.getRewardValue())
                .partnerId(grpcMission.getPartnerId() != 0 ? grpcMission.getPartnerId() : null)
                .startDate(grpcMission.getStartDate())
                .endDate(grpcMission.getEndDate())
                .taskStatus(grpcMission.getTaskStatus().name())
                .voucherRequest(voucherRequest)
                .build();
    }

    private static VoucherRequestDetail toVoucherRequestDetail(vn.com.grpc.voucher.entity.VoucherRequestDetail grpcVoucher) {
        return VoucherRequestDetail.builder()
                .voucherCode(grpcVoucher.getVoucherCode())
                .voucherName(grpcVoucher.getVoucherName())
                .description(grpcVoucher.getDescription())
                .discountType(grpcVoucher.getDiscountType())
                .discountValue(grpcVoucher.getDiscountValue())
                .maxDiscount(grpcVoucher.getMaxDiscount())
                .minOrderValue(grpcVoucher.getMinOrderValue())
                .totalStock(grpcVoucher.getTotalStock())
                .availableStock(grpcVoucher.getAvailableStock())
                .startDate(grpcVoucher.getStartDate() != 0 ? grpcVoucher.getStartDate() : null)
                .endDate(grpcVoucher.getEndDate() != 0 ? grpcVoucher.getEndDate() : null)
                .voucherStatus(grpcVoucher.getVoucherStatus())
                .nameStore(grpcVoucher.getNameStore())
                .build();
    }
}