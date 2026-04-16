package com.example.voucherservice.Aspect;

import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.dto.request.CreateVoucherExcelRequest;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
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
public class VoucherAspect {

    private final AuditLogService auditLogService;

    @Around("execution(* com.example.voucherservice.controller.VoucherController.createVoucher(..))")
    public Object aroundCreateVoucher(ProceedingJoinPoint joinPoint) throws Throwable {
        String[] authInfo = extractAuthInfo();
        String userId = authInfo[0];
        String userRole = authInfo[1];

        String action = "TẠO_VOUCHER";
        String resource = String.format("Người dùng [%s] thực hiện tạo voucher", userId);

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof CreateVoucherRequest request) {
            DiscountType discountType = request.getDiscountType();
            if (discountType != null) {
                switch (discountType) {
                    case FIXED:
                        action = "TẠO_VOUCHER_FIXED";
                        resource = String.format(
                                "Người dùng [%s] đã tạo voucher giảm giá cố định [%s] với giá trị [%s], đơn tối thiểu [%s]",
                                userId, request.getVoucherName(), request.getDiscountValue(), request.getMinOrderValue());
                        break;
                    case PERCENT:
                        action = "TẠO_VOUCHER_PERCENT";
                        resource = String.format(
                                "Người dùng [%s] đã tạo voucher giảm giá phần trăm [%s] với giá trị [%s%%], giảm tối đa [%s]",
                                userId, request.getVoucherName(), request.getDiscountValue(), request.getMaxDiscount());
                        break;
                }
            }
        }

        return executeAndAudit(joinPoint, userId, userRole, action, resource);
    }

    @Around("execution(* com.example.voucherservice.controller.VoucherController.createVoucherByExcel(..))")
    public Object aroundCreateVoucherByExcel(ProceedingJoinPoint joinPoint) throws Throwable {
        String[] authInfo = extractAuthInfo();
        String userId = authInfo[0];
        String userRole = authInfo[1];

        String action = "TẠO_VOUCHER_EXCEL";
        String resource = String.format("Người dùng [%s] thực hiện tạo voucher bằng file Excel", userId);

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof CreateVoucherExcelRequest request) {
            DiscountType discountType = request.getDiscountType();
            if (discountType != null) {
                switch (discountType) {
                    case FIXED:
                        action = "TẠO_VOUCHER_EXCEL_FIXED";
                        resource = String.format(
                                "Người dùng [%s] đã tạo voucher giảm giá cố định bằng file Excel [%s], mã yêu cầu [%s]",
                                userId, request.getFile().getOriginalFilename(), request.getRequestId());
                        break;
                    case PERCENT:
                        action = "TẠO_VOUCHER_EXCEL_PERCENT";
                        resource = String.format(
                                "Người dùng [%s] đã tạo voucher giảm giá phần trăm bằng file Excel [%s], mã yêu cầu [%s]",
                                userId, request.getFile().getOriginalFilename(), request.getRequestId());
                        break;
                }
            }
        }

        return executeAndAudit(joinPoint, userId, userRole, action, resource);
    }

    @Around("execution(* com.example.voucherservice.controller.VoucherController.submitVoucher(..))")
    public Object aroundSubmitVoucher(ProceedingJoinPoint joinPoint) throws Throwable {
        String[] authInfo = extractAuthInfo();
        String userId = authInfo[0];
        String userRole = authInfo[1];

        String action = "GỬI_DUYỆT_VOUCHER";
        String resource = String.format("Người dùng [%s] thực hiện gửi duyệt voucher", userId);

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof Long id) {
            resource = String.format("Người dùng [%s] đã gửi duyệt yêu cầu voucher [%s]", userId, id);
        }

        return executeAndAudit(joinPoint, userId, userRole, action, resource);
    }

    @Around("execution(* com.example.voucherservice.controller.VoucherController.confirmVoucher(..))")
    public Object aroundConfirmVoucher(ProceedingJoinPoint joinPoint) throws Throwable {
        String[] authInfo = extractAuthInfo();
        String userId = authInfo[0];
        String userRole = authInfo[1];

        String action = "XÁC_NHẬN_VOUCHER";
        String resource = String.format("Người dùng [%s] thực hiện xác nhận voucher", userId);

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length >= 2) {
            Long id = args[0] instanceof Long ? (Long) args[0] : null;
            String confirmAction = args[1] instanceof String ? (String) args[1] : "unknown";

            if ("APPROVED".equalsIgnoreCase(confirmAction)) {
                action = "PHÊ_DUYỆT_VOUCHER";
                resource = String.format("Người dùng [%s] đã phê duyệt yêu cầu voucher [%s]", userId, id);
            } else if ("REJECTED".equalsIgnoreCase(confirmAction)) {
                action = "TỪ_CHỐI_VOUCHER";
                resource = String.format("Người dùng [%s] đã từ chối yêu cầu voucher [%s]", userId, id);
            }
        }

        return executeAndAudit(joinPoint, userId, userRole, action, resource);
    }

    @Around("execution(* com.example.voucherservice.controller.VoucherController.cancelVoucher(..))")
    public Object aroundCancelVoucher(ProceedingJoinPoint joinPoint) throws Throwable {
        String[] authInfo = extractAuthInfo();
        String userId = authInfo[0];
        String userRole = authInfo[1];

        String action = "HỦY_VOUCHER";
        String resource = String.format("Người dùng [%s] thực hiện hủy voucher", userId);

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof Long id) {
            resource = String.format("Người dùng [%s] đã hủy yêu cầu voucher [%s]", userId, id);
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
