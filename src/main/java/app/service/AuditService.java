package app.service;

import app.entity.AuditLog;
import app.repository.AuditLogRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public Page<AuditLog> getAllAuditLogs(Pageable pageable, LocalDate startDate, LocalDate endDate, String userId, String endpoint, String httpMethod) {

        Specification<AuditLog> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate.atStartOfDay()));
            }

            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate.atTime(23, 59, 59)));
            }

            if (userId != null && !userId.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("userId")), "%" + userId.toLowerCase() + "%"));
            }


            if (endpoint != null && !endpoint.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("endpoint")), "%" + endpoint.toLowerCase() + "%"));
            }

            if (httpMethod != null && !httpMethod.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("httpMethod"), httpMethod));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return auditLogRepository.findAll(spec, pageable);
    }

    /**
     * Método para salvar logs de auditoria de operações do Keycloak
     */
    public void logKeycloakOperation(String action, String tableName, String recordId, String userId, String details) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setTableName(tableName);
            auditLog.setAction(action);
            auditLog.setUserId(userId);
            auditLog.setTimestamp(java.time.LocalDateTime.now());
            
            // Se recordId não for null, converte para Long
            if (recordId != null && !recordId.trim().isEmpty()) {
                try {
                    auditLog.setRecordId(Long.parseLong(recordId));
                } catch (NumberFormatException e) {
                    // Se não conseguir converter para Long, salva como null
                    auditLog.setRecordId(null);
                }
            }
            
            // Salva o log no banco de dados
            auditLogRepository.save(auditLog);
            
            System.out.println("Log de auditoria salvo: " + action + " - " + details);
        } catch (Exception e) {
            System.err.println("Erro ao salvar log de auditoria: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método para salvar logs de auditoria de endpoints HTTP
     */
    public void logHttpOperation(String endpoint, String httpMethod, String action, String userId, String ipAddress, String userAgent) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setEndpoint(endpoint);
            auditLog.setHttpMethod(httpMethod);
            auditLog.setAction(action);
            auditLog.setUserId(userId);
            auditLog.setIpAddress(ipAddress);
            auditLog.setUserAgent(userAgent);
            auditLog.setTimestamp(java.time.LocalDateTime.now());
            
            // Salva o log no banco de dados
            auditLogRepository.save(auditLog);
            
            System.out.println("Log HTTP salvo: " + httpMethod + " " + endpoint + " - " + action);
        } catch (Exception e) {
            System.err.println("Erro ao salvar log HTTP: " + e.getMessage());
            e.printStackTrace();
        }
    }
}