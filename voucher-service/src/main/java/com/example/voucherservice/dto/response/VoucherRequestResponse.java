package com.example.voucherservice.dto.response;

import com.example.voucherservice.constant.CreatorType;
import com.example.voucherservice.constant.RequestMode;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherPurpose;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherRequestResponse {

    private Long id;
    private String requestId;
    private RequestMode requestMode;
    private CreatorType creatorType;
    private VoucherPurpose voucherPurpose;
    private String fileName;
    private RequestStatus status;
    private String reason;
    private LocalDateTime createdTime;
    private String createdBy;
    private LocalDateTime updatedTime;
    private String updatedBy;
    private LocalDateTime confirmedTime;
    private String confirmedBy;
}
