package com.example.voucherservice.service;

import com.example.voucherservice.dto.response.AuditLogResponse;
import com.example.voucherservice.entity.AuditLogEntity;
import com.example.voucherservice.repository.AuditLogRepository;
import com.example.voucherservice.specification.AuditLogSpecification;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void saveAuditLog(String userId, String userRole, String action,
            String resource, boolean success, String errorMessage) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setUserId(userId);
        entity.setUserRole(userRole);
        entity.setAction(action);
        entity.setResource(resource);
        entity.setSuccess(success);
        entity.setErrorMessage(errorMessage);
        auditLogRepository.save(entity);
        log.info("Audit log saved: userId={}, action={}, success={}", userId, action, success);
    }

    public Page<AuditLogResponse> getAuditLogs(String userRole, String userId,
            LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        Specification<AuditLogEntity> spec = AuditLogSpecification.withFilters(
            userRole, userId, fromDate, toDate);
        return auditLogRepository.findAll(spec, pageable).map(this::toResponse);
    }

    private AuditLogResponse toResponse(AuditLogEntity entity) {
        return AuditLogResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .userRole(entity.getUserRole())
                .action(entity.getAction())
                .resource(entity.getResource())
                .success(entity.getSuccess())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
