package com.example.voucherservice.service;

import com.example.voucherservice.entity.AuditLogEntity;
import com.example.voucherservice.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
