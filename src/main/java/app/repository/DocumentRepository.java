package app.repository;

import app.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByDepartment(String departmentName);
    List<Document> findByTitleContaining(String keyword);
    List<Document> findByTitleContainingIgnoreCase(String keyword);
}
