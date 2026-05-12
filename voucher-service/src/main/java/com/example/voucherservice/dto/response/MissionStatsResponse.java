package com.example.voucherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionStatsResponse {
    private Long totalMissions;
    private Long completedMissions;
    private Long incompleteMissions;
}