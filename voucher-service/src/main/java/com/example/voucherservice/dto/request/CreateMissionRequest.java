package com.example.voucherservice.dto.request;

import com.example.voucherservice.constant.RewardType;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.TargetType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateMissionRequest extends CreateVoucherRequest {

    @NotBlank(message = "Mission name is required")
    private String missionName;

    @NotBlank(message = "Mission description is required")
    private String missionDescription;

    @NotNull(message = "Target value is required")
    @Positive(message = "Target value must be positive")
    private BigDecimal targetValue;

    @NotNull(message = "Target type is required")
    private TargetType targetType;

    @NotNull(message = "Reward type is required")
    private RewardType rewardType;

    private String rewardValue;

    private Long partnerId;

    @NotNull(message = "Mission start date is required")
    private LocalDateTime missionStartDate;

    @NotNull(message = "Mission end date is required")
    @Future(message = "Mission end date must be in the future")
    private LocalDateTime missionEndDate;

    private RequestStatus taskStatus;
}