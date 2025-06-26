package app.component;

import app.entity.AuditLog;
import app.service.AuditService;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditListener {

    private static AuditService auditService;

    @Autowired
    public void setAuditService(AuditService auditService) {
        AuditListener.auditService = auditService;
    }

    @PrePersist
    public void prePersist(Object entity) {
        if (auditService != null) {
            try {
                String entityName = entity.getClass().getSimpleName();
                String action = "CREATE";
                
                // Tenta obter o ID da entidade
                String recordId = null;
                try {
                    java.lang.reflect.Method getIdMethod = entity.getClass().getMethod("getId");
                    Object id = getIdMethod.invoke(entity);
                    if (id != null) {
                        recordId = id.toString();
                    }
                } catch (Exception e) {
                    // Se não conseguir obter o ID, continua sem ele
                }
                
                auditService.logKeycloakOperation(action, entityName, recordId, "system", "Entity created: " + entityName);
            } catch (Exception e) {
                System.err.println("Erro no prePersist: " + e.getMessage());
            }
        }
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        if (auditService != null) {
            try {
                String entityName = entity.getClass().getSimpleName();
                String action = "UPDATE";
                
                // Tenta obter o ID da entidade
                String recordId = null;
                try {
                    java.lang.reflect.Method getIdMethod = entity.getClass().getMethod("getId");
                    Object id = getIdMethod.invoke(entity);
                    if (id != null) {
                        recordId = id.toString();
                    }
                } catch (Exception e) {
                    // Se não conseguir obter o ID, continua sem ele
                }
                
                auditService.logKeycloakOperation(action, entityName, recordId, "system", "Entity updated: " + entityName);
            } catch (Exception e) {
                System.err.println("Erro no preUpdate: " + e.getMessage());
            }
        }
    }

    @PreRemove
    public void preRemove(Object entity) {
        if (auditService != null) {
            try {
                String entityName = entity.getClass().getSimpleName();
                String action = "DELETE";
                
                // Tenta obter o ID da entidade
                String recordId = null;
                try {
                    java.lang.reflect.Method getIdMethod = entity.getClass().getMethod("getId");
                    Object id = getIdMethod.invoke(entity);
                    if (id != null) {
                        recordId = id.toString();
                    }
                } catch (Exception e) {
                    // Se não conseguir obter o ID, continua sem ele
                }
                
                auditService.logKeycloakOperation(action, entityName, recordId, "system", "Entity deleted: " + entityName);
            } catch (Exception e) {
                System.err.println("Erro no preRemove: " + e.getMessage());
            }
        }
    }
}