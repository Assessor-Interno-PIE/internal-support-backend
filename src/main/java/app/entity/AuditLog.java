package app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_log")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tableName;
    private Long recordId;
    private String action;
    private LocalDateTime timestamp;

    private String endpoint;
    private String httpMethod;
    private String userAgent;
    private String ipAddress;
    private String userId;

    public AuditLog(String tableName, Long recordId, String action) {
        this.tableName = tableName;
        this.recordId = recordId;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog(String endpoint, String httpMethod, String action) {
        this.endpoint = endpoint;
        this.httpMethod = httpMethod;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }
}