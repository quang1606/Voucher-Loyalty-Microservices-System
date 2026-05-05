package com.example.voucherservice.mapper;

import com.example.voucherservice.constant.RewardType;
import com.example.voucherservice.constant.TaskStatus;
import com.example.voucherservice.dto.request.CreateMissionRequest;
import com.example.voucherservice.dto.response.MissionResponse;
import com.example.voucherservice.dto.response.MissionResponseDetail;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import vn.com.grpc.loyalty.entity.GetMissionByIdResponse;
import vn.com.grpc.loyalty.entity.MissionInfo;
import vn.com.grpc.loyalty.entity.SearchMissionResponse;

public final class MissionMapper {

    private MissionMapper() {
    }

    public static MissionResponse toMissionResponse(SearchMissionResponse grpcResponse, int page, int size) {
        List<MissionResponseDetail> details = grpcResponse.getMissionsList() == null
                ? Collections.emptyList()
                : grpcResponse.getMissionsList().stream()
                    .map(MissionMapper::toMissionResponseDetail)
                    .collect(Collectors.toList());

        return MissionResponse.builder()
                .data(details)
                .totalElements(grpcResponse.getTotalElements())
                .totalPages(grpcResponse.getTotalPages())
                .page(page)
                .size(size)
                .build();
    }

    public static MissionResponseDetail toMissionResponseDetail(MissionInfo info) {
        return MissionResponseDetail.builder()
                .missionId(info.getMissionId())
                .requestId(info.getRequestId())
                .missionName(info.getMissionName())
                .missionDescription(info.getMissionDescription())
                .targetValue(BigDecimal.valueOf(info.getTargetValue()))
                .rewardType(RewardType.valueOf(info.getRewardType().name()))
                .rewardValue(info.getRewardValue())
                .partnerId(info.getPartnerId())
                .startDate(toLocalDateTime(info.getStartDate()))
                .endDate(toLocalDateTime(info.getEndDate()))
                .status(TaskStatus.valueOf(info.getTaskStatus().name()))
                .requestId(info.getRequestId())
                .build();
    }

    public static MissionResponseDetail toMissionResponseDetail(GetMissionByIdResponse response) {
        return MissionResponseDetail.builder()
                .missionName(response.getMissionName())
                .missionDescription(response.getMissionDescription())
                .targetValue(BigDecimal.valueOf(response.getTargetValue()))
                .rewardType(RewardType.valueOf(response.getRewardType().name()))
                .rewardValue(response.getRewardValue())
                .partnerId(response.getPartnerId())
                .startDate(toLocalDateTime(response.getStartDate()))
                .endDate(toLocalDateTime(response.getEndDate()))
                .status(TaskStatus.valueOf(response.getTaskStatus().name()))
                .requestId(response.getRequestId())
                .build();
    }

    public static CreateMissionRequest toCreateMissionRequest(GetMissionByIdResponse response) {
        CreateMissionRequest request = new CreateMissionRequest();
        request.setMissionName(response.getMissionName());
        request.setMissionDescription(response.getMissionDescription());
        request.setTargetValue(BigDecimal.valueOf(response.getTargetValue()));
        request.setRewardType(RewardType.valueOf(response.getRewardType().name()));
        request.setRewardValue(response.getRewardValue());
        request.setPartnerId(response.getPartnerId() != 0 ? response.getPartnerId() : null);
        request.setMissionStartDate(toLocalDateTime(response.getStartDate()));
        request.setMissionEndDate(toLocalDateTime(response.getEndDate()));
        return request;
    }

    private static LocalDateTime toLocalDateTime(long epochMillis) {
        if (epochMillis == 0) {
            return null;
        }
        return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
