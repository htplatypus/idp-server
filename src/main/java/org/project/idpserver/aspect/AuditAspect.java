package org.project.idpserver.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.project.idpserver.annotation.Auditable;
import org.project.idpserver.service.AuditService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @AfterReturning("@annotation(auditable)")
    public void logSuccessAudit(JoinPoint joinPoint, Auditable auditable) {
        logAudit(joinPoint, auditable, true, null);
    }

    @AfterThrowing(value = "@annotation(auditable)", throwing = "exception")
    public void logFailureAudit(JoinPoint joinPoint, Auditable auditable, Throwable exception) {
        logAudit(joinPoint, auditable, false, exception.getMessage());
    }

    private void logAudit(JoinPoint joinPoint, Auditable auditable, boolean success, String failureReason) {
        String username = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> authentication.getName())
                .orElse("Unknown NAME");

        HttpServletRequest request = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .orElse(null);

        String ipAddress = request != null ? request.getRemoteAddr() : "Unknown ORIGIN";

        String reason = success ?
                String.format("method '%s' executed with arguments: %s",
                        joinPoint.getSignature().toShortString(),
                        Arrays.toString(joinPoint.getArgs())) :
                String.format("method '%s' failed with exception: %s",
                        joinPoint.getSignature().toShortString(),
                        failureReason);

        auditService.logEvent(auditable.action(), username, success, reason, ipAddress, request);
    }
}
