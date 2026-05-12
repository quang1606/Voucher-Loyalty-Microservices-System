package com.example.customerservice.dto.response;

import com.example.customerservice.constant.CustomerMissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionResponse {
    private List<MissionInfo> missions;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MissionInfo {
        private Long missionId;
        private String missionName;
        private String missionDescription;
        private Double targetValue;
        private String targetType;
        private String rewardType;
        private String rewardValue;
        private Long partnerId;
        private Long startDate;
        private Long endDate;
        private String taskStatus;
        private Integer currentProgress;
        private CustomerMissionStatus status;
        private VoucherRequestDetail voucherRequest;
        private AvailableVoucherResponse voucherDetail;
    }
}