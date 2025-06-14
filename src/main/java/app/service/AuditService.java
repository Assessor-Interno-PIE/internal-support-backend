package app.service;

import app.entity.AuditLog;
import app.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEntityChange(Object entity, String action) {
        try {
            String tableName = entity.getClass().getSimpleName();
            Long recordId = extractId(entity);

            AuditLog log = new AuditLog(tableName, recordId, action);
            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Erro ao salvar audit log: " + e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logHttpRequest(String endpoint, String method, String userAgent, String ip, String userId) {
        try {
            AuditLog log = new AuditLog(endpoint, method, "HTTP_REQUEST");
            log.setUserAgent(userAgent);
            log.setIpAddress(ip);
            log.setUserId(userId);
            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Erro ao salvar audit log HTTP: " + e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logKeycloakOperation(String operation, String resourceType, String resourceId, String userId, String details) {
        try {
            AuditLog log = new AuditLog();
            log.setAction(operation);
            log.setTableName(resourceType);
            log.setRecordId(resourceId != null ? Long.parseLong(resourceId) : null);
            log.setUserId(userId);
            log.setEndpoint(details);
            log.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Erro ao salvar audit log Keycloak: " + e.getMessage());
        }
    }

    private Long extractId(Object entity) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            return (Long) field.get(entity);
        } catch (Exception e) {
            return null;
        }
    }
}