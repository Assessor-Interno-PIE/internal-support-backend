package app.component;

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

    @PostPersist
    public void onCreate(Object entity) {
        if (auditService != null) {
            auditService.logEntityChange(entity, "CREATE");
        }
    }

    @PostUpdate
    public void onUpdate(Object entity) {
        if (auditService != null) {
            auditService.logEntityChange(entity, "UPDATE");
        }
    }

    @PreRemove
    public void onDelete(Object entity) {
        if (auditService != null) {
            auditService.logEntityChange(entity, "DELETE");
        }
    }
}