package app.service;

import app.entity.AuditLog;
import app.repository.AuditLogRepository;
import jakarta.persistence.criteria.Predicate; // Pode ser javax.persistence.criteria.Predicate em versões mais antigas do Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
// ... outras importações ...
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    // --- MÉTODO ATUALIZADO ---
    public Page<AuditLog> getAllAuditLogs(Pageable pageable, LocalDate startDate, LocalDate endDate, String userId, String endpoint, String httpMethod) {

        // Cria uma Specification para construir a query dinâmica
        Specification<AuditLog> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Adiciona filtro por data de início (se fornecida)
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate.atStartOfDay()));
            }

            // Adiciona filtro por data de fim (se fornecida)
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate.atTime(23, 59, 59)));
            }

            // Adiciona filtro por usuário (se fornecido)
            if (userId != null && !userId.trim().isEmpty()) {
                // Usando 'like' para buscas parciais (ex: buscar "admin" encontra "admin@test.com")
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("userId")), "%" + userId.toLowerCase() + "%"));
            }

            // Adiciona filtro por endpoint (se fornecido)
            if (endpoint != null && !endpoint.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("endpoint")), "%" + endpoint.toLowerCase() + "%"));
            }

            // Adiciona filtro por método HTTP (se fornecido)
            if (httpMethod != null && !httpMethod.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("httpMethod"), httpMethod));
            }

            // Combina todos os predicados com 'AND'
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Executa a busca no repositório com a especificação e paginação
        return auditLogRepository.findAll(spec, pageable);
    }

    // ... O restante dos seus métodos continua aqui (logEntityChange, logHttpRequest, etc.) ...

}