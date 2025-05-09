package app.repository;

import app.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByDepartmentName(String departmentName);
    List<Document> findByTitleContainingIgnoreCase(String keyword);
}
