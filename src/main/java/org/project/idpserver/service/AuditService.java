package org.project.idpserver.service;

import org.project.idpserver.entity.Audit;
import org.project.idpserver.entity.AuditRepository;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void logEvent(String eventType, String username, boolean success, String reason, String ipAddress, HttpServletRequest request) {
        Audit log = new Audit();
        log.setEventType(eventType);
        log.setUsername(username);
        log.setSuccess(success);
        log.setReason(reason);
        log.setIpAddress(request.getRemoteAddr());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setTimestamp(LocalDateTime.now());
        log.setIpAddress(ipAddress);

        auditRepository.save(log);
    }
}
