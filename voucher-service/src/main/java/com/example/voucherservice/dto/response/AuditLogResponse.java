package com.example.voucherservice.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private String userId;
    private String userRole;
    private String action;
    private String resource;
    private Boolean success;
    private String errorMessage;
    private LocalDateTime createdAt;
}
