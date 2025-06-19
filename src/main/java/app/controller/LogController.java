package app.controller;

import app.entity.AuditLog;
import app.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Autowired
    private AuditService auditService;

    @GetMapping("/audit")
    public ResponseEntity<Page<AuditLog>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String endpoint,
            @RequestParam(required = false) String httpMethod
    ) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AuditLog> auditLogs = auditService.getAllAuditLogs(pageable, startDate, endDate, userId, endpoint, httpMethod);

        return ResponseEntity.ok(auditLogs);
    }
}