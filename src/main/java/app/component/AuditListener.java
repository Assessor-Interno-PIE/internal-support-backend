package app.component;

import app.entity.AuditLog;
import app.entity.Document;
import app.repository.AuditLogRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditListener {

    private static AuditLogRepository staticRepo;

    @Autowired
    public void init(AuditLogRepository repo) {
        staticRepo = repo;
    }

    @PostPersist
    public void onCreate(Object entity) {
        saveLog(entity, "CREATE");
    }

    @PostUpdate
    public void onUpdate(Object entity) {
        saveLog(entity, "UPDATE");
    }

    @PreRemove
    public void onDelete(Object entity) {
        saveLog(entity, "DELETE");
    }

    private void saveLog(Object entity, String action) {
        if (entity instanceof Document doc && staticRepo != null) {
            AuditLog log = new AuditLog();
            log.
            staticRepo.save(log);
        }
    }
}
