package com.example.voucherservice.dto.response;

import com.example.voucherservice.constant.CreatorType;
import com.example.voucherservice.constant.RequestMode;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherPurpose;
import java.time.LocalDateTime;
import java.util.List;
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
    private Long totalVoucher;
    private List<StatusCount> statusCounts;
    private List<VoucherDetailResponse> voucherDetailResponses;
    private LocalDateTime createdTime;
    private String createdBy;
    private LocalDateTime updatedTime;
    private String updatedBy;
    private LocalDateTime confirmedTime;
    private String confirmedBy;
    private String storeName;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusCount {
        private RequestStatus requestStatus;
        private Long count;
    }
}
