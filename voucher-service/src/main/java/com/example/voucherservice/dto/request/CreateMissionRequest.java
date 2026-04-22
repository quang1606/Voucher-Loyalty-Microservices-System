package com.example.voucherservice.dto.request;

import com.example.voucherservice.constant.RewardType;
import com.example.voucherservice.constant.RequestStatus;
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

    @NotBlank(message = "Reward type is required")
    private RewardType rewardType; // "POINT" or "VOUCHER"

    @NotBlank(message = "Reward value is required")
    private String rewardValue; // Points amount or Voucher Campaign ID

    private Long partnerId;

    @NotNull(message = "Mission start date is required")
    private LocalDateTime missionStartDate;

    @NotNull(message = "Mission end date is required")
    @Future(message = "Mission end date must be in the future")
    private LocalDateTime missionEndDate;

    private RequestStatus taskStatus;
}