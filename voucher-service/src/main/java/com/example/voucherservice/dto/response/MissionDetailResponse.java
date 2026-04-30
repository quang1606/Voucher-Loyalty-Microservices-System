package com.example.voucherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionDetailResponse {
    private MissionResponseDetail mission;
    private VoucherDetailResponse voucherDetail;
}
