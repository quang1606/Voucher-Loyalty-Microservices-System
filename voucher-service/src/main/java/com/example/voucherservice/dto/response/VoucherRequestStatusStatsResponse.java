package com.example.voucherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherRequestStatusStatsResponse {
    private Long draft;
    private Long cancelled;
    private Long pendingApprove;
    private Long finish;
    private Long rejected;
    private Long failed;
}
