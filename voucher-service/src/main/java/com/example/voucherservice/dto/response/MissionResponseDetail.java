package com.example.voucherservice.dto.response;

import com.example.voucherservice.constant.RewardType;
import com.example.voucherservice.constant.TaskStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionResponseDetail {
  private Long missionId;
  private String requestId;
  private String missionName;
  private String missionDescription;
  private BigDecimal targetValue;
  private RewardType rewardType;
  private String rewardValue;
  private VoucherDetailResponse voucherDetail;
  private Long partnerId;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private TaskStatus status;
  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;

}
