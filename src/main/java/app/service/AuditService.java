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
}