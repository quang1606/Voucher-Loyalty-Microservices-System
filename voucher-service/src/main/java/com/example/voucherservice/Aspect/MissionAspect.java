package com.example.voucherservice.Aspect;

import com.example.voucherservice.dto.request.CreateMissionRequest;
import com.example.voucherservice.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MissionAspect {

    private final AuditLogService auditLogService;

    @Around("execution(* com.example.voucherservice.controller.VoucherController.createMission(..))")
    public Object aroundCreateMission(ProceedingJoinPoint joinPoint) throws Throwable {
        String[] authInfo = extractAuthInfo();
        String userId = authInfo[0];
        String userRole = authInfo[1];

        String action = "TẠO_NHIỆM_VỤ";
        String resource = String.format("Người dùng [%s] thực hiện tạo nhiệm vụ", userId);

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof CreateMissionRequest request) {
            resource = String.format(
                "Người dùng [%s] đã tạo nhiệm vụ [%s], mục tiêu [%s], phần thưởng [%s: %s]",
                userId, request.getMissionName(), request.getTargetValue(),
                request.getRewardType(), request.getRewardValue());
        }

        return executeAndAudit(joinPoint, userId, userRole, action, resource);
    }

    @Around("execution(* com.example.voucherservice.controller.VoucherController.submitMission(..))")
    public Object aroundSubmitMission(ProceedingJoinPoint joinPoint) throws Throwable {
        String[] authInfo = extractAuthInfo();
        String userId = authInfo[0];
        String userRole = authInfo[1];

        String action = "GỬI_DUYỆT_NHIỆM_VỤ";
        String resource = String.format("Người dùng [%s] thực hiện gửi duyệt nhiệm vụ", userId);

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof Long id) {
            resource = String.format("Người dùng [%s] đã gửi duyệt nhiệm vụ [%s]", userId, id);
        }

        return executeAndAudit(joinPoint, userId, userRole, action, resource);
    }

    @Around("execution(* com.example.voucherservice.controller.VoucherController.confirmMission(..))")
    public Object aroundConfirmMission(ProceedingJoinPoint joinPoint) throws Throwable {
        String[] authInfo = extractAuthInfo();
        String userId = authInfo[0];
        String userRole = authInfo[1];

        String action = "XÁC_NHẬN_NHIỆM_VỤ";
        String resource = String.format("Người dùng [%s] thực hiện xác nhận nhiệm vụ", userId);

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length >= 2) {
            Long id = args[0] instanceof Long ? (Long) args[0] : null;
            String confirmAction = args[1] instanceof String ? (String) args[1] : "unknown";

            if ("APPROVED".equalsIgnoreCase(confirmAction)) {
                action = "PHÊ_DUYỆT_NHIỆM_VỤ";
                resource = String.format("Người dùng [%s] đã phê duyệt nhiệm vụ [%s]", userId, id);
            } else if ("REJECTED".equalsIgnoreCase(confirmAction)) {
                action = "TỪ_CHỐI_NHIỆM_VỤ";
                resource = String.format("Người dùng [%s] đã từ chối nhiệm vụ [%s]", userId, id);
            }
        }

        return executeAndAudit(joinPoint, userId, userRole, action, resource);
    }

    @Around("execution(* com.example.voucherservice.controller.VoucherController.cancelMission(..))")
    public Object aroundCancelMission(ProceedingJoinPoint joinPoint) throws Throwable {
        String[] authInfo = extractAuthInfo();
        String userId = authInfo[0];
        String userRole = authInfo[1];

        String action = "HỦY_NHIỆM_VỤ";
        String resource = String.format("Người dùng [%s] thực hiện hủy nhiệm vụ", userId);

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof Long id) {
            resource = String.format("Người dùng [%s] đã hủy nhiệm vụ [%s]", userId, id);
        }

        return executeAndAudit(joinPoint, userId, userRole, action, resource);
    }

    private Object executeAndAudit(ProceedingJoinPoint joinPoint, String userId,
            String userRole, String action, String resource) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            auditLogService.saveAuditLog(userId, userRole, action, resource, true, null);
            log.info("Audit: action={}, userId={}, success=true", action, userId);
            return result;
        } catch (Exception ex) {
            auditLogService.saveAuditLog(userId, userRole, action, resource, false, ex.getMessage());
            log.error("Audit: action={}, userId={}, success=false, error={}", action, userId, ex.getMessage());
            throw ex;
        }
    }

    private String[] extractAuthInfo() {
        String userId = "anonymous";
        String userRole = "unknown";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            userId = jwt.getClaimAsString("preferred_username");
            if (userId == null) {
                userId = jwt.getSubject();
            }
            userRole = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("unknown");
        }

        return new String[]{userId, userRole};
    }
}
